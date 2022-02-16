package org.eclipse.dirigible.engine.js.graalvm.execution.js.result;

import org.graalvm.polyglot.Value;

public class CodeRunResult {
    private final Value result;
    private final CodeRunException error;

    public CodeRunResult(Value result, CodeRunException error) {
        this.result = result;
        this.error = error;
    }

    public Value getResult() {
        return result;
    }

    public CodeRunException getError() {
        return error;
    }
}
