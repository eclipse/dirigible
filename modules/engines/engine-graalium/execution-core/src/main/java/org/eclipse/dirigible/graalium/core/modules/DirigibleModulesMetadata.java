package org.eclipse.dirigible.graalium.core.modules;

import java.util.List;

public class DirigibleModulesMetadata {
    private static final List<String> PURE_ESM_MODULES = List.of(
            "@dirigible/http",
            "@dirigible/io",
            "@dirigible/bpm",
            "@dirigible/cms",
            "@dirigible/core",
            "@dirigible/db",
            "@dirigible/etcd",
            "@dirigible/extensions",
            "@dirigible/git",
            "@dirigible/indexing",
            "@dirigible/job",
            "@dirigible/kafka",
            "@dirigible/log",
            "@dirigible/mail",
            "@dirigible/messaging",
            "@dirigible/mongodb",
            "@dirigible/net",
            "@dirigible/pdf",
            "@dirigible/platform",
            "@dirigible/qldb",
            "@dirigible/rabbitmq",
            "@dirigible/redis",
            "@dirigible/user",
            "@dirigible/template",
            "@dirigible/utils"
    );

    static boolean isPureEsmModule(String module) {
        return PURE_ESM_MODULES.stream().anyMatch(module::startsWith);
    }
}
