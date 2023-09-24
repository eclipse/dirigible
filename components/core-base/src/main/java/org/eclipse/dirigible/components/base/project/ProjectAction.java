package org.eclipse.dirigible.components.base.project;

/**
 * The Class ProjectAction.
 */
public class ProjectAction {
	
	/** The name. */
    private String name;
	
    /** The command. */
    private String command;
    
    /** The publish. */
	private boolean publish;
    
    /**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
    
    /**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
     * Checks if is publish.
     *
     * @return true, if is publish
     */
    public boolean isPublish() {
		return publish;
	}
    
    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
		this.name = name;
	}
    
    /**
     * Sets the command.
     *
     * @param command the new command
     */
    public void setCommand(String command) {
		this.command = command;
	}
    
    /**
     * Sets the publish.
     *
     * @param publish the new publish
     */
    public void setPublish(boolean publish) {
		this.publish = publish;
	}

}
