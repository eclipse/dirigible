package org.eclipse.dirigible.engine.js.graalvm.execution.js.dependencies;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

public class DependencyProvider {
    private static final Path DEPENDENCIES_CACHE_PATH = Path.of("/Users", "c5326377", "work", "dirigible", "dependencies");

    static {
        DEPENDENCIES_CACHE_PATH.toFile().mkdirs();
    }

    public Path provideDependency(URI uri) {
        Path maybeCachePath = tryGetCachePath(uri);
        return maybeCachePath != null ? maybeCachePath : downloadDependency(uri);
    }

    @Nullable
    private Path tryGetCachePath(URI uri) {
        String base64DependencyName = getBase64FromURI(uri);
        Path expectedDependencyPath = DEPENDENCIES_CACHE_PATH.resolve(base64DependencyName);
        File expectedDependencyFile = expectedDependencyPath.toFile();

        if (expectedDependencyFile.exists()) {
            return expectedDependencyPath;
        }

        return null;
    }

    private Path downloadDependency(URI uri) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        try (Response response = client.newCall(request).execute()) {
            File dependencyFile = getDependencyFileForOutput(uri);
            InputStream downloadedStream = response.body().byteStream();
            FileUtils.copyInputStreamToFile(downloadedStream, dependencyFile);
            return dependencyFile.toPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File getDependencyFileForOutput(URI dependencyUri) {
        String base64DependencyName = getBase64FromURI(dependencyUri);
        File dependencyFile = DEPENDENCIES_CACHE_PATH.resolve(base64DependencyName).toFile();

        return dependencyFile;
    }

    private static String getBase64FromURI(URI uri) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytes = uri.toString().getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }
}
