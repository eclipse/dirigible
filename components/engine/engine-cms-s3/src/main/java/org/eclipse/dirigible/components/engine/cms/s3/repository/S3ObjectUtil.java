package org.eclipse.dirigible.components.engine.cms.s3.repository;

import org.eclipse.dirigible.repository.api.IRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

class S3ObjectUtil {
    static class S3ObjectDescriptor {
        private final String name;
        private final boolean folder;
        private final boolean file;

        S3ObjectDescriptor(boolean folder, String name) {
            this.folder = folder;
            this.file = !folder;
            this.name = name;
        }

        public boolean isFolder() {
            return folder;
        }

        public boolean isFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "S3ObjectDescriptor{" + "name='" + name + '\'' + ", folder=" + folder + ", file=" + file + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            S3ObjectDescriptor that = (S3ObjectDescriptor) o;
            return folder == that.folder && file == that.file && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, folder, file);
        }
    }

    static Set<S3ObjectDescriptor> getDirectChildren(String rootPath, List<String> objectKeys) {
        return getDirectChildren(rootPath, new HashSet<>(objectKeys));
    }

    static Set<S3ObjectDescriptor> getDirectChildren(String rootPath, Set<String> objectKeys) {
        Set<S3ObjectDescriptor> descriptors = new HashSet<>();

        for (String objectKey : objectKeys) {
            String relativePath = objectKey.startsWith(rootPath) ? objectKey.replaceFirst(Pattern.quote(rootPath), "") : objectKey;

            if (isObjectInSubdir(relativePath)) {
                String childFolderName = extractFirstSegment(relativePath);
                childFolderName = "".equals(childFolderName) ? IRepository.SEPARATOR : childFolderName;
                S3ObjectDescriptor descriptor = new S3ObjectDescriptor(true, childFolderName);
                descriptors.add(descriptor);
            } else {
                String fileName = extractFirstSegment(relativePath);
                S3ObjectDescriptor descriptor = new S3ObjectDescriptor(false, fileName);
                descriptors.add(descriptor);
            }
        }

        return descriptors;
    }

    private static boolean isObjectInSubdir(String path) {
        return path.split(IRepository.SEPARATOR).length > 1;
    }

    private static String extractFirstSegment(String relativePath) {
        String[] parts = relativePath.split("(?<=/)");
        return parts[0];
    }

    private static boolean isFolder(String objectKey) {
        return objectKey.endsWith(IRepository.SEPARATOR);
    }

}
