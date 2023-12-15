package org.eclipse.dirigible.components.api.s3;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class S3Facade implements InitializingBean {
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(S3Facade.class);
    private static S3Facade INSTANCE;

//    private static final String BUCKET = "cmis-bucket";
//    private static final Region AWS_DEFAULT_REGION = Region.EU_CENTRAL_1;
//    private static final Region AWS_REGION = Region.EU_CENTRAL_1;
//    private static final String AWS_ACCESS_KEY_ID = "AKIA5QLC4KLZEU447GW4";
//    private static final String AWS_SECRET_ACCESS_KEY = "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";
//    private static final String BUCKET_URL_PATH = "https://cmis-bucket.s3.eu-central-1.amazonaws.com/";

        private static final String BUCKET = Configuration.get("BUCKET");
        private static final String AWS_ACCESS_KEY_ID = Configuration.get("AWS_ACCESS_KEY_ID");
        private static final Region AWS_DEFAULT_REGION = Region.of(Configuration.get("AWS_DEFAULT_REGION"));
        private static final Region AWS_REGION = Region.of(Configuration.get("AWS_DEFAULT_REGION"));
        private static final String AWS_SECRET_ACCESS_KEY = Configuration.get("AWS_SECRET_ACCESS_KEY");
        private static final String BUCKET_URL_PATH = Configuration.get("BUCKET_URL_PATH");

    private static S3Client s3;
    static {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        s3 = S3Client.builder()
                     .region(AWS_DEFAULT_REGION)
                     .credentialsProvider(StaticCredentialsProvider.create(credentials))
                     .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    public static S3Facade get(){
        return INSTANCE;
    }

    public static void put(String name, byte[] input) throws IOException {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(name)
                .build();

        s3.putObject(objectRequest, RequestBody.fromBytes(input));
    }

    public static void uploadDirectory(String sourceDirectory) {
//        S3AsyncClient s3AsyncClient =
//                S3AsyncClient.crtBuilder()
//                             .credentialsProvider(StaticCredentialsProvider.create(
//                                     AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)))
//                             .region(AWS_DEFAULT_REGION)
//                             .targetThroughputInGbps(20.0)
//                             .minimumPartSizeInBytes(8 * MB)
//                             .build();
//
//        S3TransferManager transferManager =
//                S3TransferManager.builder()
//                                 .s3Client(s3AsyncClient)
//                                 .build();

        S3TransferManager transferManager = S3TransferManager.create();

        DirectoryUpload directoryUpload =
                transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(BUCKET)
                        .build());

        directoryUpload.completionFuture().join();
    }

    public static void delete(String name) {

        if(name.endsWith("/")){
            deleteFolder(name);
        }else {
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
        put(name, inputStream);
    }

    public static List<S3Object> listObjects() {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
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

    public static boolean exists(String name) {
        List<S3Object> bucketObjects = listObjects();

        for (S3Object bucketObject : bucketObjects) {
            if (bucketObject.key().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
