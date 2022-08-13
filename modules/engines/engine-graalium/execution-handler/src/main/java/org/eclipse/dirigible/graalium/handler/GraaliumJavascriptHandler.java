package org.eclipse.dirigible.graalium.handler;

import org.eclipse.dirigible.engine.js.service.JavascriptHandler;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The Class GraaliumJavascriptHandler.
 */
public class GraaliumJavascriptHandler implements JavascriptHandler {

    /** The dirigible source provider. */
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    /**
     * Handle request.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the object
     */
    @Override
    public Object handleRequest(String projectName, String projectFilePath, String projectFilePathParam, boolean debug) {
        try {
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
            }

            String maybeJSCode = dirigibleSourceProvider.getSource(projectName, projectFilePath);
            if (maybeJSCode == null) {
                throw new IOException("JavaScript source code for project name '" + projectName + "' and file name '" + projectFilePath + " could not be found");
            }

            Path jsCodePath = dirigibleSourceProvider.getAbsoluteSourcePath(projectName, projectFilePath);
            Value value = new DirigibleJavascriptCodeRunner(debug).run(jsCodePath);
            if (value.isBoolean()) {
            	return value.asBoolean();
            } else if (value.isDate()) {
            	return value.asDate();
            } else if (value.isDuration()) {
            	return value.asDuration();
            } else if (value.isNull()) {
            	return null;
            } else if (value.isNumber()) {
            	if (value.fitsInDouble()) {
            		return value.asDouble();
            	} else if (value.fitsInFloat()) {
            		return value.asFloat();
            	} else if (value.fitsInLong()) {
            		return value.asLong();
            	} else if (value.fitsInInt()) {
            		return value.asInt();
            	} else if (value.fitsInShort()) {
            		return value.asShort();
            	} else if (value.fitsInByte()) {
            		return value.asByte();
            	}
            } else if (value.isString()) {
            	return value.asString();
            } else if (value.isTime()) {
            	return value.asTime();
            } else if (value.isTimeZone()) {
            	return value.asTimeZone();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
