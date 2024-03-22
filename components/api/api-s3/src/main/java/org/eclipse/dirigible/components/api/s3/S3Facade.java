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

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class S3Facade.
 */
@Component
public class S3Facade implements InitializingBean {
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(S3Facade.class);
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
    private static final String PATH_SEPARATOR = "/";
    private static final String ROOT_PATH = "/";
    private static final String FOLDER_SUFFIX = "/";
    /** The instance. */
    private static S3Facade INSTANCE;
    private static TenantContext tenantContext;
    private static Tenant defaultTenant;
    /** The s 3. */
    private S3Client s3;

    S3Facade(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        S3Facade.tenantContext = tenantContext;
        S3Facade.defaultTenant = defaultTenant;
    }

    /**
     * After properties set.
     */
    @Override
    public void afterPropertiesSet() {
        INSTANCE = this;
    }

    /**
     * Upload directory.
     *
     * @param sourceDirectory the source directory
     */
    public static void uploadDirectory(String sourceDirectory) {
        String bucket = getBucketName();
        S3TransferManager transferManager = S3TransferManager.create();

        DirectoryUpload directoryUpload = transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                                                                                                .source(Paths.get(sourceDirectory))
                                                                                                .bucket(bucket)
                                                                                                .build());

        directoryUpload.completionFuture()
                       .join();
    }

    private static String getBucketName() {
        return Configuration.get("DIRIGIBLE_S3_BUCKET", "cmis-bucket");
    }

    /**
     * Delete.
     *
     * @param name the name
     */
    public static void delete(String name) {
        String tenantName = toTenantPath(name);
        String bucket = getBucketName();
        if (isFolderPath(tenantName)) {
            deleteFolder(tenantName);
        } else {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                                                                   .bucket(bucket)
                                                                   .key(tenantName)
                                                                   .build();

            S3Facade.get()
                    .getS3Client()
                    .deleteObject(objectRequest);
        }
    }

    public static String toTenantPath(String path) {
        String tenantId = tenantContext.isInitialized() ? tenantContext.getCurrentTenant()
                                                                       .getId() : defaultTenant.getId();
        String prefix = tenantId + PATH_SEPARATOR;
        if (ROOT_PATH.equals(path)) {
            return prefix;
        }

        if (path.startsWith(prefix)) {
            return path;
        }

        String tenantPath = prefix + (path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path);
        logger.debug("Path [{}] is resolved to [{}]", path, tenantPath);
        return tenantPath;
    }

    private static boolean isFolderPath(String path) {
        return path.endsWith(FOLDER_SUFFIX);
    }

    /**
     * Delete folder.
     *
     * @param prefix the prefix
     */
    public static void deleteFolder(String prefix) {
        String tenantPrefix = toTenantPath(prefix);
        String bucket = getBucketName();
        try (S3Client s3Client = S3Client.builder()
                                         .region(AWS_DEFAULT_REGION)
                                         .build()) {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                                                               .bucket(bucket)
                                                               .prefix(tenantPrefix)
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
                                                                            .bucket(bucket)
                                                                            .delete(Delete.builder()
                                                                                          .objects(objectIdentifiers)
                                                                                          .build())
                                                                            .build();
            s3Client.deleteObjects(deleteObjectsRequest);
        }
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
     * Gets the S3Facade.
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
     * Initializes the client.
     */
    private void initClient() {
        String bucket = getBucketName();
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
            createBucket(bucket);
        }
    }

    private void createBucket(String bucket) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                                                                     .bucket(bucket)
                                                                     .build();

        try {
            s3.createBucket(createBucketRequest);
        } catch (BucketAlreadyOwnedByYouException ex) {
            logger.debug("Bucket: [" + bucket + "] already created", ex);
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
        String bucket = getBucketName();

        String tenantName = toTenantPath(name);
        ResponseInputStream<GetObjectResponse> response = S3Facade.get()
                                                                  .getS3Client()
                                                                  .getObject(GetObjectRequest.builder()
                                                                                             .bucket(bucket)
                                                                                             .key(tenantName)
                                                                                             .build());

        return response.readAllBytes();
    }

    /**
     * Update.
     *
     * @param name the name
     * @param inputStream the input stream
     */
    public static void update(String name, byte[] inputStream) {
        // will upload the updated object to S3, overwriting the existing object
        // TODO fix update
        put(name, inputStream, "");
    }

    /**
     * Put.
     *
     * @param name the name
     * @param input the input
     * @param contentType the content type
     */
    public static void put(String name, byte[] input, String contentType) {
        String tenantName = toTenantPath(name);
        String bucket = getBucketName();
        PutObjectRequest objectRequest;
        if (isFolderPath(tenantName)) {
            objectRequest = PutObjectRequest.builder()
                                            .bucket(bucket)
                                            .key(tenantName)
                                            .build();
        } else {
            objectRequest = PutObjectRequest.builder()
                                            .bucket(bucket)
                                            .key(tenantName)
                                            .contentType(contentType)
                                            .build();
        }
        S3Facade.get()
                .getS3Client()
                .putObject(objectRequest, RequestBody.fromBytes(input));
    }

    /**
     * List objects.
     *
     * @param path the path
     * @return the list
     */
    public static List<S3Object> listObjects(String path) {
        String tenantPath = toTenantPath(path);

        String bucket = getBucketName();
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                                                                        .bucket(bucket)
                                                                        .prefix(tenantPath)
                                                                        .build();

        ListObjectsV2Response listObjectsV2Response = S3Facade.get()
                                                              .getS3Client()
                                                              .listObjectsV2(listObjectsV2Request);

        List<S3Object> contents = listObjectsV2Response.contents();

        logger.info("Number of objects in the bucket: [{}]", contents.size());
        return contents;
    }

    /**
     * Exists.
     *
     * @param keyName the key name
     * @return true, if successful
     */
    public static boolean exists(String keyName) {
        String tenantKeyName = toTenantPath(keyName);
        String bucket = getBucketName();
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                                               .key(tenantKeyName)
                                                               .bucket(bucket)
                                                               .build();

            HeadObjectResponse objectHead = S3Facade.get()
                                                    .getS3Client()
                                                    .headObject(objectRequest);
            objectHead.contentType();
            return true;
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ex) {
            logger.debug("[{}] already exists", tenantKeyName, ex);
            return true;
        } catch (NoSuchKeyException ex) {
            logger.debug("[{}] is missing", tenantKeyName, ex);
            return false;
        } catch (S3Exception ex) {
            logger.warn("Returning false for [{}]", tenantKeyName, ex);
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
        String bucket = getBucketName();
        String tenantKeyName = toTenantPath(keyName);
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                                               .key(tenantKeyName)
                                                               .bucket(bucket)
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
     * Sets the s3 client.
     *
     * @param s3 the new s3 client
     */
    public void setS3Client(S3Client s3) {
        INSTANCE.s3 = s3;
    }
}
