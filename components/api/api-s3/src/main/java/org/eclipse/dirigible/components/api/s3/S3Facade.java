/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.s3;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
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
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

/**
 * The Class S3Facade.
 */
@Component
public class S3Facade implements InitializingBean {
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(S3Facade.class);

    /** The instance. */
    private static S3Facade INSTANCE;

    /** The Constant AWS_ACCESS_KEY_ID. */
    private static final String AWS_ACCESS_KEY_ID = Configuration.get("AWS_ACCESS_KEY_ID");

    /** The Constant AWS_DEFAULT_REGION. */
    private static final Region AWS_DEFAULT_REGION = Region.of(Configuration.get("AWS_DEFAULT_REGION", "eu-central-1"));

    /** The Constant AWS_SECRET_ACCESS_KEY. */
    private static final String AWS_SECRET_ACCESS_KEY = Configuration.get("AWS_SECRET_ACCESS_KEY");

    /** The Constant DIRIGIBLE_S3_PROVIDER. */
    private static final String DIRIGIBLE_S3_PROVIDER = Configuration.get("DIRIGIBLE_S3_PROVIDER", "aws");

    /** The Constant LOCALSTACK_URI. */
    private static final String DEFAULT_LOCALSTACK_URI = "https://s3.localhost.localstack.cloud:4566";

    /** The s 3. */
    private S3Client s3;

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;

    }

    /**
     * Gets the.
     *
     * @return the s3 facade
     */
    public static S3Facade get() {
        if (INSTANCE.s3 == null) {
            INSTANCE.initClient();
        }
        return INSTANCE;
    }

    /**
     * Gets the s3 client.
     *
     * @return the s3 client
     */
    private S3Client getS3Client() {
        return INSTANCE.s3;
    }

    /**
     * Sets the s3 client.
     *
     * @param s3 the new s3 client
     */
    public void setS3Client(S3Client s3) {
        INSTANCE.s3 = s3;
    }

    /**
     * Put.
     *
     * @param name the name
     * @param input the input
     * @param contentType the content type
     */
    public static void put(String name, byte[] input, String contentType) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
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
        S3Facade.get()
                .getS3Client()
                .putObject(objectRequest, RequestBody.fromBytes(input));
    }

    /**
     * Upload directory.
     *
     * @param sourceDirectory the source directory
     */
    public static void uploadDirectory(String sourceDirectory) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        S3TransferManager transferManager = S3TransferManager.create();

        DirectoryUpload directoryUpload = transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                                                                                                .source(Paths.get(sourceDirectory))
                                                                                                .bucket(BUCKET)
                                                                                                .build());

        directoryUpload.completionFuture()
                       .join();
    }

    /**
     * Delete.
     *
     * @param name the name
     */
    public static void delete(String name) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        if (name.endsWith("/")) {
            deleteFolder(name);
        } else {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                                                                   .bucket(BUCKET)
                                                                   .key(name)
                                                                   .build();

            S3Facade.get()
                    .getS3Client()
                    .deleteObject(objectRequest);
        }
    }

    /**
     * Gets the.
     *
     * @param name the name
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] get(String name) throws IOException {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        ResponseInputStream<GetObjectResponse> response = S3Facade.get()
                                                                  .getS3Client()
                                                                  .getObject(GetObjectRequest.builder()
                                                                                             .bucket(BUCKET)
                                                                                             .key(name)
                                                                                             .build());

        return response.readAllBytes();
    }

    /**
     * Update.
     *
     * @param name the name
     * @param inputStream the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void update(String name, byte[] inputStream) throws IOException {
        // will upload the updated object to S3, overwriting the existing object
        // TODO fix update
        put(name, inputStream, "");
    }

    /**
     * List objects.
     *
     * @param path the path
     * @return the list
     */
    public static List<S3Object> listObjects(String path) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                                                                        .bucket(BUCKET)
                                                                        .prefix("/".equals(path) ? null : path)
                                                                        .build();

        ListObjectsV2Response listObjectsV2Response = S3Facade.get()
                                                              .getS3Client()
                                                              .listObjectsV2(listObjectsV2Request);

        List<S3Object> contents = listObjectsV2Response.contents();

        logger.info("Number of objects in the bucket: " + contents.stream()
                                                                  .count());
        return contents;
    }

    /**
     * Delete folder.
     *
     * @param prefix the prefix
     */
    public static void deleteFolder(String prefix) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        S3Client s3Client = S3Client.builder()
                                    .region(AWS_DEFAULT_REGION)
                                    .build();
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                                                           .bucket(BUCKET)
                                                           .prefix(prefix)
                                                           .build();
        ListObjectsV2Iterable list = s3Client.listObjectsV2Paginator(request);

        List<ObjectIdentifier> objectIdentifiers = list.stream()
                                                       .flatMap(r -> r.contents()
                                                                      .stream())
                                                       .map(o -> ObjectIdentifier.builder()
                                                                                 .key(o.key())
                                                                                 .build())
                                                       .collect(Collectors.toList());

        if (objectIdentifiers.isEmpty())
            return;
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                                                                        .bucket(BUCKET)
                                                                        .delete(Delete.builder()
                                                                                      .objects(objectIdentifiers)
                                                                                      .build())
                                                                        .build();
        s3Client.deleteObjects(deleteObjectsRequest);
    }

    /**
     * Exists.
     *
     * @param keyName the key name
     * @return true, if successful
     */
    public static boolean exists(String keyName) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        if (keyName.startsWith("/")) {
            keyName = keyName.substring(1);
        }
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                                               .key(keyName)
                                                               .bucket(BUCKET)
                                                               .build();

            HeadObjectResponse objectHead = S3Facade.get()
                                                    .getS3Client()
                                                    .headObject(objectRequest);
            objectHead.contentType();
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    /**
     * Gets the object content type.
     *
     * @param keyName the key name
     * @return the object content type
     */
    public static String getObjectContentType(String keyName) {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        if (keyName.startsWith("/")) {
            keyName = keyName.substring(1);
        }
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                                               .key(keyName)
                                                               .bucket(BUCKET)
                                                               .build();

            HeadObjectResponse objectHead = S3Facade.get()
                                                    .getS3Client()
                                                    .headObject(objectRequest);
            return objectHead.contentType();
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails()
                          .errorMessage());
            return "";
        }
    }

    /**
     * Sets the client for test container.
     *
     * @param localstackUri the new client for test container
     */
    public static void setClientForTestContainer(URI localstackUri, String bucket) {
        S3Facade.get()
                .setS3Client(S3Client.builder()
                                     .region(AWS_DEFAULT_REGION)
                                     .endpointOverride(localstackUri)
                                     .build());
        S3Facade.get()
                .createBucket(bucket);
    }

    /**
     * Initializes the client.
     */
    private void initClient() {
        String BUCKET = Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
        if (AWS_ACCESS_KEY_ID == null || AWS_SECRET_ACCESS_KEY == null) {
            logger.warn("AWS_ACCESS_KEY_ID or AWS_SECRET_ACCESS_KEY not set");
        } else {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
            if (DIRIGIBLE_S3_PROVIDER.equals("aws")) {
                s3 = S3Client.builder()
                             .region(AWS_DEFAULT_REGION)
                             .credentialsProvider(StaticCredentialsProvider.create(credentials))
                             .build();
            } else if (DIRIGIBLE_S3_PROVIDER.equals("localstack")) {
                s3 = S3Client.builder()
                             .region(AWS_DEFAULT_REGION)
                             .endpointOverride(URI.create(DEFAULT_LOCALSTACK_URI))
                             .credentialsProvider(StaticCredentialsProvider.create(credentials))
                             .build();
            } else {
                return;
            }
            createBucket(BUCKET);
        }
    }

    private void createBucket(String bucket) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                                                                     .bucket(bucket)
                                                                     .build();

        try {
            s3.createBucket(createBucketRequest);
        } catch (BucketAlreadyOwnedByYouException ignored) {
            logger.info("Bucket: " + bucket + "already created");
        }
    }
}
