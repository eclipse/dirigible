package org.eclipse.dirigible.components.ide.git.domain;

/**
 * The Class GitUrlInput.
 */
public class GitUrlInput {

    /** The url. */
    private String url;

    /**
     * Instantiates a new git url input.
     */
    public GitUrlInput() {
        super();
    }

    /**
     * Instantiates a new git url input.
     *
     * @param url the url
     */
    public GitUrlInput(String url) {
        super();
        this.url = url;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
