package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.ConcurrentBag;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LeakedConnectionsDoctor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeakedConnectionsDoctor.class);

    private static final long MAX_IN_USE_MILLIS =
            TimeUnit.SECONDS.toMillis(DirigibleConfig.LEAKED_CONNECTIONS_MAX_IN_USE_SECONDS.getIntValue());

    private static final Set<InUseConnectionEntry> IN_USE_CONNECTIONS = new HashSet<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(LeakedConnectionsDoctor::closeLeakedConnections, 30,
                DirigibleConfig.LEAKED_CONNECTIONS_CHECK_INTERVAL_SECONDS.getIntValue(), TimeUnit.SECONDS);

        Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> {
                   executor.shutdown();
                   try {
                       executor.awaitTermination(5, TimeUnit.SECONDS);
                   } catch (InterruptedException ex) {
                       LOGGER.warn("Failed to await for termination", ex);
                   } finally {
                       if (!executor.isTerminated()) {
                           executor.shutdownNow();
                       }
                   }
               }));
    }


    private static class InUseConnectionEntry {
        private final Connection connection;
        private final long borrowedAt;

        public InUseConnectionEntry(Connection connection) {
            this.connection = connection;
            this.borrowedAt = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            InUseConnectionEntry that = (InUseConnectionEntry) o;
            return Objects.equals(connection, that.connection);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(connection);
        }
    }

    public static void registerConnection(Connection connection) {
        IN_USE_CONNECTIONS.add(new InUseConnectionEntry(connection));
    }

    private static void closeLeakedConnections() {
        LOGGER.debug("Checking for leaked connections...");
        long executionStartedAt = System.currentTimeMillis();
        Set<InUseConnectionEntry> leftInUseConnections = new HashSet<>();
        Set<InUseConnectionEntry> connectionsForRemove = new HashSet<>();

        IN_USE_CONNECTIONS.forEach(entry -> {
            if (isClosed(entry.connection)) {
                LOGGER.debug("Connection [{}] borrowed at [{}] is closed. Will be removed from the list.", entry.connection,
                        entry.borrowedAt);
                connectionsForRemove.add(entry);
                return;
            }

            if (entry.connection instanceof HikariProxyConnection hikariProxyConnection && isInUse(hikariProxyConnection.getPoolEntry())
                    && (isNotBorrowedSinceRegistered(hikariProxyConnection.getPoolEntry(), entry)
                            && isNotAccessedSinceRegistered(hikariProxyConnection.getPoolEntry(), entry))) {

                boolean maxInUsePassed = executionStartedAt > (entry.borrowedAt + MAX_IN_USE_MILLIS);
                if (maxInUsePassed) {
                    closeLeakedEntry(entry, hikariProxyConnection);
                    connectionsForRemove.add(entry);
                } else {
                    LOGGER.debug(
                            "Connection [{}] borrowed at [{}] didn't reached the configured max in use time of [{}] millis. Will check it on the next execution.",
                            entry.connection, entry.borrowedAt, MAX_IN_USE_MILLIS);
                    leftInUseConnections.add(entry);
                }
            } else {
                connectionsForRemove.add(entry);
            }
        });
        IN_USE_CONNECTIONS.removeAll(connectionsForRemove);
        IN_USE_CONNECTIONS.addAll(leftInUseConnections);
    }

    private static void closeLeakedEntry(InUseConnectionEntry entry, HikariProxyConnection hikariProxyConnection) {
        try {
            LOGGER.warn("Found leaked connection [{}] borrowed at [{}] and remained in state IN_USE. Will be closed.", entry.connection,
                    entry.borrowedAt);
            entry.connection.close();
            hikariProxyConnection.getPoolEntry()
                                 .evict("The connection is leaked since it is in state IN_USE for more than [" + MAX_IN_USE_MILLIS
                                         + "] millis.");
            LOGGER.debug("Leaked connection [{}] borrowed at [{}] was closed", entry.connection, entry.borrowedAt);
        } catch (RuntimeException | SQLException ex) {
            LOGGER.warn("Failed to close connection [{}] which was borrowed at [{}]", entry.connection, entry.borrowedAt, ex);
        }
    }

    private static boolean isNotAccessedSinceRegistered(PoolEntry poolEntry, InUseConnectionEntry entry) {
        return poolEntry.lastAccessed <= entry.borrowedAt;

    }

    private static boolean isInUse(PoolEntry poolEntry) {
        return poolEntry.getState() == ConcurrentBag.IConcurrentBagEntry.STATE_IN_USE;
    }

    private static boolean isNotBorrowedSinceRegistered(PoolEntry poolEntry, InUseConnectionEntry entry) {
        return poolEntry.lastBorrowed <= entry.borrowedAt;
    }

    private static boolean isClosed(Connection connection) {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            LOGGER.warn("Connection [{}] cannot be checked for isClosed. Will consider it is closed.", e);
            return true;
        }
    }
}
