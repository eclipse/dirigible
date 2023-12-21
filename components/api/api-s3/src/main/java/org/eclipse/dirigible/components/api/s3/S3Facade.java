package org.eclipse.dirigible.components.api.s3;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class S3Facade implements InitializingBean {
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(S3Facade.class);
    private static S3Facade INSTANCE;

    private static final String BUCKET = Configuration.get("BUCKET");
    private static final String AWS_ACCESS_KEY_ID = Configuration.get("AWS_ACCESS_KEY_ID");
    private static final Region AWS_DEFAULT_REGION = Region.of(Configuration.get("AWS_DEFAULT_REGION"));
    private static final Region AWS_REGION = Region.of(Configuration.get("AWS_DEFAULT_REGION"));
    private static final String AWS_SECRET_ACCESS_KEY = Configuration.get("AWS_SECRET_ACCESS_KEY");
    private static final String BUCKET_URL_PATH = Configuration.get("BUCKET_URL_PATH");

    private static S3Client s3;

    static {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        if (Configuration.get("DIRIGIBLE_S3_PROVIDER").equals("aws")) {
            s3 = S3Client.builder()
                    .region(AWS_DEFAULT_REGION)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        } else if (Configuration.get("DIRIGIBLE_S3_PROVIDER").equals("localstack")) {
            s3 = S3Client.builder()
                    .region(AWS_DEFAULT_REGION)
                    .endpointOverride(URI.create("https://s3.localhost.localstack.cloud:4566"))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    public static S3Facade get() {
        return INSTANCE;
    }

    public static void put(String name, byte[] input, String contentType) {
        PutObjectRequest objectRequest;
        if (name.endsWith("/")) {
            objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(name)
                    .build();
        } else {
            objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(name)
                    .contentType(contentType)
                    .build();
        }
        s3.putObject(objectRequest, RequestBody.fromBytes(input));
    }

    public static void uploadDirectory(String sourceDirectory) {
        S3TransferManager transferManager = S3TransferManager.create();

        DirectoryUpload directoryUpload =
                transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(BUCKET)
                        .build());

        directoryUpload.completionFuture().join();
    }

    public static void delete(String name) {

        if (name.endsWith("/")) {
            deleteFolder(name);
        } else {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(name)
                    .build();

            s3.deleteObject(objectRequest);
        }
    }

    public static byte[] get(String name) throws IOException {

        ResponseInputStream<GetObjectResponse> response =
                s3.getObject(GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(name)
                        .build());

        return response.readAllBytes();
    }

    public static void update(String name, byte[] inputStream) throws IOException {
        // will upload the updated object to S3, overwriting the existing object
        // TODO fix update
        put(name, inputStream, "");
    }

    public static List<S3Object> listObjects(String path) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix("/".equals(path) ? null : path)
                .build();

        ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);

        List<S3Object> contents = listObjectsV2Response.contents();

        logger.info("Number of objects in the bucket: " + contents.stream().count());
        return contents;
    }

    public static void deleteFolder(String prefix) {
        S3Client s3Client = S3Client.builder().region(AWS_DEFAULT_REGION).build();
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(BUCKET).prefix(prefix).build();
        ListObjectsV2Iterable list = s3Client.listObjectsV2Paginator(request);

        List<ObjectIdentifier> objectIdentifiers = list
                .stream()
                .flatMap(r -> r.contents().stream())
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .collect(Collectors.toList());

        if (objectIdentifiers.isEmpty()) return;
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest
                .builder()
                .bucket(BUCKET)
                .delete(Delete.builder().objects(objectIdentifiers).build())
                .build();
        s3Client.deleteObjects(deleteObjectsRequest);
    }

    public static boolean exists(String keyName) {
        if (keyName.startsWith("/")) {
            keyName = keyName.substring(1);
        }
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                    .key(keyName)
                    .bucket(BUCKET)
                    .build();

            HeadObjectResponse objectHead = s3.headObject(objectRequest);
            String type = objectHead.contentType();
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    public static String getObjectContentType(String keyName) {
        if (keyName.startsWith("/")) {
            keyName = keyName.substring(1);
        }
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                    .key(keyName)
                    .bucket(BUCKET)
                    .build();

            HeadObjectResponse objectHead = s3.headObject(objectRequest);

            Map<String, String> as = objectHead.metadata();
            return objectHead.contentType();
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            return "";
        }
    }
}