package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.websocket.Session;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.ResultResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.URIUtils;

import com.google.gson.Gson;

public class GetResourceContentHandler implements MessageHandler {

	private static final Gson GSON = new Gson();
	private static final String IMAGE_EXTS = "png|svg|jpg|jpeg"; // TODO: more

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		final Integer id = request.getId();
		final String url = (String) request.getParams().get("url");
		final Map<String, Object> result = new HashMap<String, Object>();
		String pageContent;
		if (this.isImage(url)) {
			final URLConnection connection = new URL(url).openConnection();
			final InputStream inputStream = connection.getInputStream();
			result.put("base64Encoded", true);
			pageContent = this.getEncodedImage(inputStream);
		} else {
			result.put("base64Encoded", false);
			ScriptRepository repository = ScriptRepository.getInstance();
			pageContent = repository.getSourceFor(repository.getScriptIdByURL(url));
		}
		result.put("content", pageContent);

		final ResultResponse content = new ResultResponse(id, result);
		MessageDispatcher.sendMessage(GSON.toJson(content), session);
	}

	private boolean isImage(final String url) {
		final String extension = URIUtils.getURIextension(url);
		return Pattern.compile(IMAGE_EXTS).matcher(extension).find();
	}

	private String getEncodedImage(final InputStream stream) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		final byte[] buffer = new byte[1024];
		int count = 0;

		while ((count = stream.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}

		final byte[] fileContent = baos.toByteArray();
		final byte[] encoded = Base64.encodeBase64(fileContent);
		return new String(encoded);
	}
}
