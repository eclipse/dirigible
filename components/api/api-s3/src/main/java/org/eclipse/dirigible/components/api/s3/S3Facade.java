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
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;


@Component
public class S3Facade implements InitializingBean {
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(S3Facade.class);
    private static S3Facade INSTANCE;
    private static final String BUCKET = Configuration.get("BUCKET_URL_PATH", "");
    private static final Region REGION = Region.of(Configuration.get("REGION", ""));
    private static final String ACCESS_KEY_ID = Configuration.get("ACCESS_KEY_ID", "");
    private static final String SECRET_ACCESS_KEY = Configuration.get("SECRET_ACCESS_KEY", "");
    private static S3Client s3;
    static {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        s3 = S3Client.builder()
                     .region(REGION)
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

    public static void put(String name, InputStream inputStream) throws IOException {

        byte[] contentBytes = IoUtils.toByteArray(inputStream);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(name)
                .build();

        s3.putObject(objectRequest, RequestBody.fromBytes(contentBytes));
    }

    public void uploadDirectory(String sourceDirectory) {
        S3TransferManager transferManager = S3TransferManager.create();
        DirectoryUpload directoryUpload =
                transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(BUCKET)
                        .build());

        directoryUpload.completionFuture().join();
    }

    public static void delete(String name) {

        DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(name)
                .build();

        s3.deleteObject(objectRequest);
    }

    public static byte[] get(String name) throws IOException {

        ResponseInputStream<GetObjectResponse> response =
                s3.getObject(GetObjectRequest.builder()
                                             .bucket(BUCKET)
                                             .key(name)
                                             .build());

        return response.readAllBytes();
    }

    public static void update(String name, InputStream inputStream) throws IOException {
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


}
