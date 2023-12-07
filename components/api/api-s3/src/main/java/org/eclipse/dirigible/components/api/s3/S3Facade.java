package org.eclipse.dirigible.components.api.s3;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static software.amazon.awssdk.regions.Region.EU_NORTH_1;


@Component
public class S3Facade {
    private static final String BUCKET = "testbuckets3mina";
//    public static final String ACCESS_KEY_ID = "AKIAYUZ6SK3AGWNHSAPE";
//    public static final String SECRET_ACCESS_KEY = "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";
//    public static final String BUCKET_URL_PATH = "https://testbuckets3mina.s3.eu-north-1.amazonaws.com/";
    private static final Region region = EU_NORTH_1;

    private static AwsBasicCredentials awsCredentials;
    private static S3Client s3;

    public S3Facade() {
    }

    public S3Facade(AwsBasicCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
        s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(this.awsCredentials))
                .build();
    }

    public S3Facade(S3Client s3) {
        this.s3 = s3;
    }

    public static void create(String name, byte[] content) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(name)
                .build();

        s3.putObject(objectRequest, RequestBody.fromBytes(content));
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

    public static void update(String name, byte[] content) {
        // will upload the updated object to S3, overwriting the existing object
        create(name, content);
    }
}
