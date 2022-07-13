package org.eclipse.dirigible.graalium.core.graal.modules.downloadable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class DownloadableModuleResolver {

    private final Path dependenciesCachePath;

    public DownloadableModuleResolver(Path dependenciesCachePath) {
        dependenciesCachePath.toFile().mkdirs();
        this.dependenciesCachePath = dependenciesCachePath;
    }

    public Path resolve(URI uri) {
        Path maybeCachePath = tryGetCachePath(uri);
        return maybeCachePath != null ? maybeCachePath : downloadDependency(uri);
    }

    private Path tryGetCachePath(URI uri) {
        String base64DependencyName = getBase64FromURI(uri);
        Path expectedDependencyPath = dependenciesCachePath.resolve(base64DependencyName);
        File expectedDependencyFile = expectedDependencyPath.toFile();

        if (expectedDependencyFile.exists()) {
            return expectedDependencyPath;
        }

        return null;
    }

    private Path downloadDependency(URI uri) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .build();

        try {
        	BodyHandler<byte[]> responseBodyHandler = HttpResponse.BodyHandlers.ofByteArray();
        	HttpResponse<byte[]> response = client.send(request, responseBodyHandler);
            Path dependencyFilePath = getDependencyFilePathForOutput(uri);
            byte[] downloadedBytes = response.body();
            Files.write(dependencyFilePath, downloadedBytes);
            return dependencyFilePath;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getDependencyFilePathForOutput(URI dependencyUri) {
        String base64DependencyName = getBase64FromURI(dependencyUri);
        return dependenciesCachePath.resolve(base64DependencyName);
    }

    private static String getBase64FromURI(URI uri) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytes = uri.toString().getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }
}
