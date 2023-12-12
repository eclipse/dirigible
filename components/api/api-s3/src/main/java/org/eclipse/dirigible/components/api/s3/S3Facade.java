package org.eclipse.dirigible.components.api.s3;

import org.eclipse.dirigible.commons.config.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

import static software.amazon.awssdk.regions.Region.EU_NORTH_1;


@Component
public class S3Facade {
    private static final String BUCKET = "testbuckets3mina";
//    public static final String ACCESS_KEY_ID = "AKIAYUZ6SK3AGWNHSAPE";
//    public static final String SECRET_ACCESS_KEY = "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";
//    public static final String BUCKET_URL_PATH = "https://testbuckets3mina.s3.eu-north-1.amazonaws.com/";
    private static final Region region = EU_NORTH_1;

//    private static AwsBasicCredentials awsCredentials;
    private static S3Client s3;


    public S3Facade() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                Configuration.get("ACCESS_KEY_ID", ""),
                Configuration.get("SECRET_ACCESS_KEY", "")
        );
        new S3Facade(credentials);
    }

    private S3Facade(AwsBasicCredentials awsCredentials) {
        s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public S3Facade(S3Client s3) {
        this.s3 = s3;
    }

    public static void put (String name, InputStream inputStream) throws IOException {

        inputStream.read();

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

    public static byte[] get(String name) {

        ResponseBytes<GetObjectResponse> response = s3.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(name)
                .build());

        return response.asByteArray();
    }

    public static void update(String name, InputStream inputStream) throws IOException {
        // will upload the updated object to S3, overwriting the existing object
        put(name, inputStream);
    }

    public static List<S3Object> listObjects(){
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                                                                        .bucket(BUCKET)
                                                                        .build();
        ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);

        List<S3Object> contents = listObjectsV2Response.contents();

        System.out.println("Number of objects in the bucket: " + contents.stream().count());
        return contents;
    }
}
