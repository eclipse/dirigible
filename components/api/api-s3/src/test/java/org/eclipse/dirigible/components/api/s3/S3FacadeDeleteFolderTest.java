/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.dirigible.components.engine.cms.TenantPathResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

/**
 * S3 itself refuses a DeleteObjects request of more than 1,000 keys, and LocalStack does not, so
 * the batching and the per-key error check are verified here against a mocked client.
 */
class S3FacadeDeleteFolderTest {

    private final S3Client client = mock(S3Client.class);

    @BeforeEach
    void wire() {
        TenantPathResolver resolver = mock(TenantPathResolver.class);
        when(resolver.resolve(any())).thenAnswer(invocation -> invocation.getArgument(0));
        new S3Facade(resolver).setS3Client(client);
    }

    @Test
    void theKeysAreDeletedInBatchesOfAtMostOneThousand() {
        listing(2500);
        when(client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(DeleteObjectsResponse.builder()
                                                                                                    .build());

        S3Facade.deleteFolder("acme/");

        ArgumentCaptor<DeleteObjectsRequest> sent = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(client, times(3)).deleteObjects(sent.capture());
        assertEquals(List.of(1000, 1000, 500), sent.getAllValues()
                                                   .stream()
                                                   .map(request -> request.delete()
                                                                          .objects()
                                                                          .size())
                                                   .toList());
    }

    @Test
    void aKeyS3CouldNotDeleteStopsTheRunAndIsReported() {
        listing(1500);
        S3Error denied = S3Error.builder()
                                .key("acme/7")
                                .code("AccessDenied")
                                .message("Access Denied")
                                .build();
        when(client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(DeleteObjectsResponse.builder()
                                                                                                    .errors(denied)
                                                                                                    .build());

        IllegalStateException failed = assertThrows(IllegalStateException.class, () -> S3Facade.deleteFolder("acme/"));

        assertTrue(failed.getMessage()
                         .contains("acme/7"),
                failed.getMessage());
        verify(client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void anEmptyListingSendsNoDelete() {
        listing(0);

        S3Facade.deleteFolder("acme/");

        verify(client, never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    private void listing(int objects) {
        List<S3Object> contents = IntStream.range(0, objects)
                                           .mapToObj(i -> S3Object.builder()
                                                                  .key("acme/" + i)
                                                                  .build())
                                           .toList();
        ListObjectsV2Response page = ListObjectsV2Response.builder()
                                                          .contents(contents)
                                                          .isTruncated(false)
                                                          .build();
        when(client.listObjectsV2Paginator(any(ListObjectsV2Request.class))).thenAnswer(
                invocation -> new ListObjectsV2Iterable(client, invocation.getArgument(0)));
        when(client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(page);
    }

}
