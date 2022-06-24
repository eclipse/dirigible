package org.eclipse.dirigible.web.quarkus.extension;

import io.smallrye.mutiny.Uni;
//import io.vertx.mutiny.core.Context;
//import io.vertx.mutiny.core.Vertx;
import org.graalvm.polyglot.Value;

public class UniThenUtil {
    public static void then(Uni<?> self, Value onFulfilled, Value onRejected) {
//        Context context = Vertx.currentContext();
        self.subscribe().with(res -> {
            if (onFulfilled != null) {
//                context.runOnContext(onFulfilled::executeVoid);
                onFulfilled.executeVoid();
            }
        }, ex -> {
            if (onRejected != null) {
//                context.runOnContext(onRejected::executeVoid);
                onRejected.executeVoid(ex);
            }
        });
    }
}
