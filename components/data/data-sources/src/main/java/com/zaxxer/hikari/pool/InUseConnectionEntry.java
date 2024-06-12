package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.util.Objects;

class InUseConnectionEntry {

    private final Connection connection;
    private final long borrowedAt;

    InUseConnectionEntry(Connection connection) {
        this.connection = connection;
        this.borrowedAt = System.currentTimeMillis();
    }

    public Connection getConnection() {
        return connection;
    }

    public long getBorrowedAt() {
        return borrowedAt;
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

    @Override
    public String toString() {
        return "InUseConnectionEntry{" + "connection=" + connection + ", borrowedAt=" + borrowedAt + '}';
    }
}
