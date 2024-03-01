package org.eclipse.dirigible.components.base.tenant;

@FunctionalInterface
public interface CallableNoResultAndException {

    void call() throws Exception;
}
