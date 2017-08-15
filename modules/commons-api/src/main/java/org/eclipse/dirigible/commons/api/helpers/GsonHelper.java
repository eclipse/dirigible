package org.eclipse.dirigible.commons.api.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class GsonHelper {

	public static transient Gson GSON = new Gson();

	public static JsonParser PARSER = new JsonParser();

}
