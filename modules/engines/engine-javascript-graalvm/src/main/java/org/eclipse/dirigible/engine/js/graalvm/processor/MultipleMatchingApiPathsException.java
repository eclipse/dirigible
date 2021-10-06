package org.eclipse.dirigible.engine.js.graalvm.processor;

class MultipleMatchingApiPathsException extends RuntimeException {
    public MultipleMatchingApiPathsException(String errorMessage) {
        super(errorMessage);
    }
}
