// deprecated: included to the templates themselves
exports.hasConflictingParameters = function(id, count, metadata) {
    if(id !== null && count !== null){
    	printError(javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED, 1, "Precondition failed: conflicting parameters - id, count");
        return true;
    }
    if(id !== null && metadata !== null){
    	printError(javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED, 2, "Precondition failed: conflicting parameters - id, metadata");
        return true;
    }
    return false;
}

// check whether the parameter exists 
exports.isInputParameterValid = function(paramName) {
    var param = request.getParameter(paramName);
    if(param === null || param === undefined){
    	printError(javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED, 3, "Expected parameter is missing: " + paramName);
        return false;
    }
    return true;
}

// print error
exports.printError = function(httpCode, errCode, errMessage, errContext) {
    var body = {'err': {'code': errCode, 'message': errMessage}};
    response.setStatus(httpCode);
    response.setHeader("Content-Type", "application/json");
    response.getWriter().print(JSON.stringify(body));
    out.println(JSON.stringify(body));
    if (errContext !== null) {
    	out.println(JSON.stringify(errContext));
    }
}
