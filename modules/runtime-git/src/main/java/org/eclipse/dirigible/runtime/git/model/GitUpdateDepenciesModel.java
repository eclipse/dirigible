package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

public class GitUpdateDepenciesModel extends BaseGitProjectModel {

	@ApiModelProperty(value = "Whether to publish the project(s) after update of dependencies", example = "true")
	private boolean publish;

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

}
