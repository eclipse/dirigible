package org.eclipse.dirigible.components.base.tenant;

@FunctionalInterface
public interface CallableResultAndNoException<Result> {

    Result call();
}
