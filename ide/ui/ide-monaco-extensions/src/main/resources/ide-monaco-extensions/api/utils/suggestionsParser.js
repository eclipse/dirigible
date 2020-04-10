/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var contentManager = require("repository/v4/content");
var acorn = require("acornjs/acorn");

String.prototype.replaceAll = function(search, replacement) {
    return this.replace(new RegExp(search, 'g'), replacement);
};

exports.parse = function(moduleName) {
    var content = contentManager.getText(moduleName + ".js");
    var comments = [];
    var nodes = acorn.parse(content, {
        onComment: comments,
        ranges: true
    });

    var functionDeclarations = nodes.body
        .filter(e => e.type === "FunctionDeclaration")
        .map(function(element) {
            let name = element.id.name;
            let functions = element.body.body
                .filter(e => e.type === "ExpressionStatement")
                .map(e => extractExpression(e, comments))
                .filter(e => e !== null);
            return {
                name: name,
                functions: functions
            }
        });

    var result = nodes.body
        .filter(e => e.type === "ExpressionStatement")
        .map(function(element) {
            return extractExpression(element, comments, functionDeclarations);
        }).filter(e => e !== null);

    return result;
}

function extractExpression(element, comments, functionDeclarations) {
    let expression = element.expression;
    if (expression && expression.type === "AssignmentExpression" && expression.operator === "=") {
        let left = expression.left;
        let right = expression.right;
        if (right.type === "FunctionExpression") {
            let properties = right.params.map(e => e.name);
            let name = left.property.name + "(" + properties.join(", ") + ")"; 
            let documentation = extractDocumentation(comments, element, name);
            documentation = formatDocumentation(documentation, name, true);
            let bodyExpressions = right.body.body;
            let returnStatement = bodyExpressions.filter(e => e.type === "ReturnStatement")[0];
            let returnType = null;
            if (functionDeclarations && returnStatement) {
                if (returnStatement.argument.type === "NewExpression") {
                    returnType = returnStatement.argument.callee.name;
                    returnType = functionDeclarations.filter(e => e.name === returnType)[0];
                } else if (returnStatement.argument.type === "Identifier") {
                    let returnIdentifierName = returnStatement.argument.name;
                    let returnIdentifierType = bodyExpressions
                        .filter(e => e.type === "VariableDeclaration")
                        .map(e => e.declarations[0])
                        .filter(e => e && e.init && e.init.type === "NewExpression")
                        .map(function(e) {
                            return {
                                name: e.id.name,
                                type: e.init.callee.name
                            }
                        })
                        .filter(e => e.name === returnIdentifierName)
                        .map(e => e.type)[0];
                    if (returnIdentifierType) {
                        returnType = functionDeclarations.filter(e => e.name === returnIdentifierType)[0]
                    }
                }
            }
            return {
                name: name,
                documentation: documentation,
                returnType: returnType,
                isFunction: true
            };
        } else if (right.type === "Literal") {
            let name = left.property.name;
            let documentation = extractDocumentation(comments, element, name);
            documentation = formatDocumentation(documentation, name, false);
            return {
                name: name,
                documentation: documentation,
                isProperty: true
            };
        }
    }
    return null;
}

function extractDocumentation(comments, element, defaultDocumentation) {
    let documentation = comments.filter(function(comment) {
        if (comment.type === "Block") {
            let diff = element.start - comment.end;
            return  diff > 0 && diff <= 10;
        }
        return false;
    })[0];  
    return documentation ? documentation.value : defaultDocumentation;
}

function formatDocumentation(documentation, expression, isFunction) {
    return [
        "```javascript",
        (isFunction ? "function " : "") + expression,
        "```",
        "",
        "---",
        documentation.replaceAll("\\*", "")
    ].join("\n");
}
