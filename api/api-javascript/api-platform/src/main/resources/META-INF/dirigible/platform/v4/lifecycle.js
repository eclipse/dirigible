exports.publish = function(user, workspace, project) {
    if (!project) {
        project = "*";
    }
    return org.eclipse.dirigible.api.v3.platform.LifecycleFacade.publish(user, workspace, project);
};

exports.unpublish = function(user, workspace, project) {
    if (!project) {
        project = "*";
    }
    return org.eclipse.dirigible.api.v3.platform.LifecycleFacade.unpublish(user, workspace, project);
};