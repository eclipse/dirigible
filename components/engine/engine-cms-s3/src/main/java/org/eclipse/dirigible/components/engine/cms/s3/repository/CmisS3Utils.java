package org.eclipse.dirigible.components.engine.cms.s3.repository;

import org.eclipse.dirigible.repository.api.IRepository;

import java.util.Objects;

public class CmisS3Utils {
    public static String ROOT = "/";

    public static String findCurrentFile(String folderPath) {
        if (Objects.equals(folderPath, ROOT)) {
            return ROOT;
        }

        String[] parts = folderPath.split(IRepository.SEPARATOR);
        return parts[parts.length - 1];
    }

    public static String findCurrentFolder(String folderPath) {
        if (Objects.equals(folderPath, ROOT)) {
            return ROOT;
        }

        String[] parts = folderPath.split(IRepository.SEPARATOR);
        return parts[parts.length - 1] + IRepository.SEPARATOR;
    }

    public static String findParentFolder(String folderPath) {
        if (Objects.equals(folderPath, ROOT)) {
            return null;
        }

        String[] parts = folderPath.split(IRepository.SEPARATOR);
        if (parts.length >= 3) {
            int secondToLastIndex = parts.length - 2;
            return parts[secondToLastIndex] + IRepository.SEPARATOR;
        } else {
            return IRepository.SEPARATOR;
        }
    }

    public static int pathSegmentsLength(String folderPath) {
        if (Objects.equals(folderPath, ROOT)) {
            return 0;
        }

        String[] parts = folderPath.split(IRepository.SEPARATOR);
        return parts.length;
    }
}
