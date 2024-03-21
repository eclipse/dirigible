package org.eclipse.dirigible.components.engine.cms.s3.repository;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class S3ObjectUtilTest {

    @Test
    void testGetDirectChildren() {
        Set<String> objectKeys = Set.of(//
                "/tenant1/folder1/folder2/file2.txt", //
                "/tenant1//folder1/folder2/anotherfile.txt", //
                "/tenant1/file0.txt", //
                "/tenant1/file0_2.txt");
        Set<S3ObjectUtil.S3ObjectDescriptor> descriptors = S3ObjectUtil.getDirectChildren("/tenant1/", objectKeys);

        S3ObjectUtil.S3ObjectDescriptor[] expectedDescriptors = new S3ObjectUtil.S3ObjectDescriptor[] {//
                new S3ObjectUtil.S3ObjectDescriptor(true, "folder1/"), //
                new S3ObjectUtil.S3ObjectDescriptor(true, "/"), //
                new S3ObjectUtil.S3ObjectDescriptor(false, "file0_2.txt"), //
                new S3ObjectUtil.S3ObjectDescriptor(false, "file0.txt")};
        assertThat(descriptors).contains(expectedDescriptors);
    }

    @Test
    void testGetDirectChildren2() {
        Set<String> objectKeys = Set.of(//
                "/148e34d8-47ff-4e53-9f74-f0100202fdbf//def.txt", //
                "READERS.csv", //
                "defaultTenant//def.txt", //
                "file0.txt");
        Set<S3ObjectUtil.S3ObjectDescriptor> descriptors = S3ObjectUtil.getDirectChildren("/", objectKeys);

        S3ObjectUtil.S3ObjectDescriptor[] expectedDescriptors = new S3ObjectUtil.S3ObjectDescriptor[] {//
                new S3ObjectUtil.S3ObjectDescriptor(true, "148e34d8-47ff-4e53-9f74-f0100202fdbf/"), //
                new S3ObjectUtil.S3ObjectDescriptor(false, "READERS.csv"), //
                new S3ObjectUtil.S3ObjectDescriptor(true, "defaultTenant/"), //
                new S3ObjectUtil.S3ObjectDescriptor(false, "file0.txt")};
        assertThat(descriptors).contains(expectedDescriptors);
    }
}
