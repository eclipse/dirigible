package org.eclipse.dirigible.components.websockets.message;

/**
 * The Class OutputMessage.
 */
public class OutputMessage {

    /** The from. */
    private String from;
    
    /** The text. */
    private String text;
    
    /** The time. */
    private String time;

    /**
     * Instantiates a new output message.
     *
     * @param from the from
     * @param text the text
     * @param time the time
     */
    public OutputMessage(final String from, final String text, final String time) {

        this.from = from;
        this.text = text;
        this.time = time;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }
}
