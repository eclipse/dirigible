#!/usr/bin/env bash
#
# Copyright (c) 2010-2026 Eclipse Dirigible contributors
#
# All rights reserved. This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v20.html
#
# SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
#
# Smoke test of the Java edition (#8011, #8026): with no JavaScript engine on board, its IDE generates an
# intent application and publishes it, and the application runs - its generated Java API and its
# application shell.
#
# Usage: smoke-test.sh <java-edition-executable-jar>
#
# DIRIGIBLE_SERVER_PORT (default 8080) and the other DIRIGIBLE_* variables of the environment are
# passed to the Java edition, so a second instance can run beside another one.

set -euo pipefail

JAVA_JAR=$(cd "$(dirname "${1:?the Java edition executable jar}")" && pwd)/$(basename "$1")
PORT=${DIRIGIBLE_SERVER_PORT:-8080}
BASE="http://localhost:${PORT}"
AUTH="admin:admin"
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
INTENT="${SCRIPT_DIR}/../application/src/test/resources/regeneration-sample/app.intent"
PROJECT="java-edition-smoke"
WORKSPACE="workspace"

WORK=$(mktemp -d)
APP_PID=""
# The platform keeps its databases, CMS and index under ./target relative to the working directory:
# run everything from the scratch folder, never from the caller's checkout.
cd "${WORK}"

cleanup() {
    if [ -n "${APP_PID}" ] && kill -0 "${APP_PID}" 2>/dev/null; then
        kill "${APP_PID}"
        wait "${APP_PID}" 2>/dev/null || true
    fi
    rm -rf "${WORK}"
}
trap cleanup EXIT

fail() {
    echo "FAILED: $*" >&2
    if [ -f "${WORK}/app.log" ]; then
        echo "--- last lines of the Java edition log:" >&2
        tail -n 80 "${WORK}/app.log" >&2
    fi
    exit 1
}

# Waits until the URL answers 200, up to the given number of seconds.
await_ok() {
    local url=$1 seconds=$2 code=""
    for _ in $(seq 1 "${seconds}"); do
        code=$(curl -s -o /dev/null -w '%{http_code}' -u "${AUTH}" "${url}" || true)
        [ "${code}" = "200" ] && return 0
        kill -0 "${APP_PID}" 2>/dev/null || fail "the Java edition exited while waiting for ${url}"
        sleep 1
    done
    fail "${url} answered ${code} after ${seconds}s"
}

echo "== 1. The Java edition carries the IDE and no JavaScript stack"
LIBS=$(unzip -Z1 "${JAVA_JAR}" 'BOOT-INF/lib/*')
FORBIDDEN='BOOT-INF/lib/(js-language|truffle-|polyglot-|icu4j-|dirigible-engine-graalium|dirigible-components-(engine-javascript|engine-typescript|engine-di|engine-template-javascript|api-modules-javascript))'
if grep -qE "${FORBIDDEN}" <<<"${LIBS}"; then
    grep -E "${FORBIDDEN}" <<<"${LIBS}" >&2
    fail "the Java edition bundles the libraries listed above"
fi
for module in ui-shell-ide ui-editor-intent engine-intent ide-template; do
    grep -qE "BOOT-INF/lib/dirigible-components-${module}-" <<<"${LIBS}" || fail "the Java edition does not bundle ${module}"
done
echo "   $(wc -l <<<"${LIBS}" | tr -d ' ') libraries, the IDE among them, none of them JavaScript or GraalVM"

echo "== 2. Boot it"
DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER="${WORK}/target" \
    java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens java.base/java.nio=ALL-UNNAMED -jar "${JAVA_JAR}" > "${WORK}/app.log" 2>&1 &
APP_PID=$!
await_ok "${BASE}/actuator/health/readiness" 900
echo "   ready"

echo "== 3. The IDE opens"
await_ok "${BASE}/services/web/shell-ide/" 60
curl -sf -u "${AUTH}" "${BASE}/services/platform/extensions/perspectives" | grep -q '"workbench"' \
    || fail "the IDE perspectives do not include the Workbench"
curl -sf -u "${AUTH}" "${BASE}/services/platform/extensions/editors" | grep -q 'editor-intent' \
    || fail "the IDE editors do not include the Intent Editor"
echo "   shell, perspectives and editors"

echo "== 4. The IDE generates the intent application and publishes it"
curl -s -o /dev/null -u "${AUTH}" -X POST "${BASE}/services/ide/workspaces/${WORKSPACE}"
curl -sf -o /dev/null -u "${AUTH}" -X POST "${BASE}/services/ide/workspaces/${WORKSPACE}/${PROJECT}" \
    || fail "creating the project"
curl -sf -o /dev/null -u "${AUTH}" -X POST -H 'Content-Type: text/plain' --data-binary "@${INTENT}" \
    "${BASE}/services/ide/workspaces/${WORKSPACE}/${PROJECT}/app.intent" || fail "writing app.intent"
GENERATION=$(curl -sf -u "${AUTH}" -X POST \
    "${BASE}/services/ide/intent/generate?workspace=${WORKSPACE}&project=${PROJECT}&path=app.intent") \
    || fail "the intent Generate"
if grep -qE '"generated" *: *false' <<<"${GENERATION}"; then
    echo "${GENERATION}" >&2
    fail "a code generation of the intent Generate failed"
fi
PROJECT_DIR=$(find "${WORK}/target" -type d -path "*/users/admin/${WORKSPACE}/${PROJECT}" | head -n 1)
[ -n "${PROJECT_DIR}" ] || fail "the project is not in the workspace"
CONTROLLER=$(cd "${PROJECT_DIR}" && find gen -path '*/api/*' -name 'TicketController.java' | head -n 1)
[ -n "${CONTROLLER}" ] || fail "the generation wrote no TicketController"
CONTROLLER_URL="${BASE}/services/java/${PROJECT}/${CONTROLLER%.java}"
APP_INDEX=$(cd "${PROJECT_DIR}" && find gen -maxdepth 2 -name index.html | head -n 1)
[ -n "${APP_INDEX}" ] || fail "the generation wrote no application page"
curl -sf -o /dev/null -u "${AUTH}" -X POST "${BASE}/services/ide/publisher/${WORKSPACE}/${PROJECT}" \
    || fail "publishing the project"
echo "   generated $(find "${PROJECT_DIR}" -type f | wc -l | tr -d ' ') files and published them"

echo "== 5. The generated Java API answers"
await_ok "${CONTROLLER_URL}" 600
curl -sf -u "${AUTH}" -H 'Content-Type: application/json' -X POST -d '{"Subject":"Smoke ticket"}' "${CONTROLLER_URL}" > /dev/null \
    || fail "creating a ticket through ${CONTROLLER_URL}"
curl -sf -u "${AUTH}" "${CONTROLLER_URL}" | grep -q 'Smoke ticket' || fail "the created ticket is not listed"
echo "   ${CONTROLLER_URL}: create and list"

echo "== 6. The application shell bootstraps"
await_ok "${BASE}/services/web/${PROJECT}/${APP_INDEX}" 60
# Every library the application page loads ships in this edition - a webjar that only a JavaScript
# module declared would answer 404 here.
for webjar in $(curl -sf -u "${AUTH}" "${BASE}/services/web/${PROJECT}/${APP_INDEX}" \
    | grep -oE '(src|href)="/webjars/[^"]+"' | sed -E 's/^(src|href)="//; s/"$//' | sort -u); do
    await_ok "${BASE}${webjar}" 10
done
for path in /services/platform/branding.js /services/platform/user-name /services/platform/languages \
    /services/platform/extensions/shells "/services/platform/extensions/locales?langs=en-US"; do
    await_ok "${BASE}${path}" 60
done
curl -sf -u "${AUTH}" "${BASE}/services/platform/extensions/perspectives?extensionPoints=application-perspectives" \
    | grep -q "${PROJECT}" || fail "the application's perspectives are not aggregated"
echo "   page, shell services and the application's perspectives"

echo "== 7. Nothing asked for a script engine"
if grep -qE 'ScriptEngineUnavailable|no script engine' "${WORK}/app.log"; then
    grep -E 'ScriptEngineUnavailable|no script engine' "${WORK}/app.log" | head -n 5 >&2
    fail "something needed the JavaScript engine"
fi

echo "== 8. The edition's own identity and provided-BOM are the ones in use"
curl -sf -u "${AUTH}" "${BASE}/services/core/version" | grep -q '"productType" *: *"java"' \
    || fail "/services/core/version does not report the java product type"
BOM_ENTRIES=$(unzip -p "${JAVA_JAR}" META-INF/dirigible-provided-bom.xml | grep -c '<dependency>')
grep -q "the platform provides \[${BOM_ENTRIES}\] artifact" "${WORK}/app.log" \
    || { grep 'Provided-BOM' "${WORK}/app.log" >&2; fail "the provided-BOM in use is not the edition's own (${BOM_ENTRIES} entries)"; }
echo "   product type java, provided-BOM of ${BOM_ENTRIES} artifacts"

echo "PASSED: the Java edition generates, publishes and runs the intent application"
