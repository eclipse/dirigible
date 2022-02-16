package org.eclipse.dirigible.core.git;

public class OriginUrls {
    private final String fetchUrl;
    private final String pushUrl;

    public OriginUrls(String fetchUrl, String pushUrl) {
        this.fetchUrl = fetchUrl;
        this.pushUrl = pushUrl;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }
}
