package org.eclipse.dirigible.components.ide.workspace.domain;

/**
 * The Class WorkspaceFromToPair.
 */
public class WorkspaceFromToPair {

    /** The from. */
    private String from;

    /** The to. */
    private String to;

    /**
     * Instantiates a new workspace from to pair.
     *
     * @param from the from
     * @param to the to
     */
    public WorkspaceFromToPair(String from, String to) {
        super();
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets the to.
     *
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the to.
     *
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }



}
