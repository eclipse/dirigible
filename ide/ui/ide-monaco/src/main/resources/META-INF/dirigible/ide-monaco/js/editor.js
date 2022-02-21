let messageHub = new FramesMessageHub();
let csrfToken;
let dirty = false;

let modulesSuggestions = [];
let codeCompletionSuggestions = {};
let codeCompletionAssignments = {};
let _editor;
let resourceApiUrl;
let editorUrl;
let gitApiUrl;
let loadingOverview = document.getElementsByClassName('loading-overview')[0];
let loadingMessage = document.getElementsByClassName('loading-message')[0];
let lineDecorations = [];
let useParameters = false; // Temp boolean used for transitioning to new parameter method.
let parameters = {
    resourceType: "",
    contentType: "",
    readOnly: false,
    gitName: "",
    file: ""
};

const computeNewLines = (oldText, newText, isWhitespaceIgnored = true) => {
    if (oldText[oldText.length - 1] !== "\n" || newText[newText.length - 1] !== "\n") {
        oldText += "\n";
        newText += "\n"
    }
    let lineDiff;
    if (isWhitespaceIgnored) {
        lineDiff = Diff.diffTrimmedLines(oldText, newText)
    } else {
        lineDiff = Diff.diffLines(oldText, newText)
    }
    let addedCount = 0;
    let addedLines = [];
    lineDiff.forEach(part => {
        let { added, removed, value } = part;
        let count = value.split("\n").length - 1;
        if (!added && !removed) {
            addedCount += count
        }
        else
            if (added) {
                for (let i = 0; i < count; i++) {
                    addedLines.push(addedCount + i + 1)
                }
                addedCount += count
            }
    });
    return addedLines
};

/*eslint-disable no-extend-native */
String.prototype.replaceAll = function (search, replacement) {
    let target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

function getNewLines(oldText, newText, isWhitespaceIgnored = true) {
    if (
        oldText[oldText.length - 1] !== "\n" ||
        newText[newText.length - 1] !== "\n"
    ) {
        oldText += "\n";
        newText += "\n";
    }
    let lineDiff;
    if (isWhitespaceIgnored) {
        lineDiff = Diff.diffTrimmedLines(oldText, newText);
    } else {
        lineDiff = Diff.diffLines(oldText, newText);
    }
    let addedCount = 0;
    let addedLines = [];
    let removedLines = [];
    lineDiff.forEach((part) => {
        let { added, removed, value } = part;
        let count = value.split("\n").length - 1;
        if (!added && !removed) {
            addedCount += count;
        } else if (added) {
            for (let i = 0; i < count; i++) {
                addedLines.push(addedCount + i + 1);
            }
            addedCount += count;
        } else if (removed && !addedLines.includes(addedCount + count)) {
            removedLines.push(addedCount);
        }
    });
    return { updated: addedLines, removed: removedLines };
};

function highlight_changed(lines, editor) {
    let new_decorations = [];
    lines.updated.forEach((line) => {
        new_decorations.push({
            range: new monaco.Range(line, 1, line, 1),
            options: {
                isWholeLine: true,
                linesDecorationsClassName: 'modified-line' + (
                    lines.removed.includes(line) ? ' deleted-line' : '')
            },
        });
    });
    lines.removed.forEach((line) => {
        if (!lines.updated.includes(line + 1))
            new_decorations.push({
                range: new monaco.Range(line, 1, line, 1),
                options: {
                    isWholeLine: true,
                    linesDecorationsClassName: 'deleted-line'
                },
            });
    });
    return editor.deltaDecorations(lineDecorations, new_decorations);
}

function FileIO() {

    this.isReadOnly = function () {
        if (useParameters) return parameters.readOnly || false;
        else return editorUrl.searchParams.get('readOnly') || false;
    };
    this.resolveGitProjectName = function () {
        if (useParameters) return parameters.gitName;
        return editorUrl.searchParams.get('gitName');
    };
    this.resolveFileName = function () {
        if (useParameters) {
            this.readOnly = parameters.readOnly || false;
            return parameters.file;
        }
        else {
            this.readOnly = editorUrl.searchParams.get('readOnly') || false;
            return editorUrl.searchParams.get('file');
        }
    };

    this.getFileType = function (fileName) {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.open("GET", "/services/v4/js/ide-monaco/api/fileTypes.js");
            xhr.onload = () => {
                if (xhr.status === 200) {
                    let fileTypes = JSON.parse(xhr.responseText);

                    let fileType = "text";
                    if (fileName) {
                        for (let fileExtension in fileTypes) {
                            if (fileName.endsWith(fileExtension)) {
                                fileType = fileTypes[fileExtension];
                            }
                        }
                    }
                    if (fileName && fileName.indexOf(".") === -1 && fileName.toLowerCase().indexOf("dockerfile") > 0) {
                        fileType = "dockerfile";
                    }

                    resolve(fileType);
                } else {
                    reject(`HTTP ${xhr.status} - ${xhr.statusText}`)
                }
                csrfToken = xhr.getResponseHeader("x-csrf-token");
            };
            xhr.onerror = () => reject(`HTTP ${xhr.status} - ${xhr.statusText}`);
            xhr.send();
        });
    };

    this.loadText = function (file) {
        return new Promise((resolve, reject) => {
            if (file) {
                let project = this.resolveGitProjectName();
                let url;
                if (project) {
                    let workspace = file.replace('\\', '/').split('/')[1];
                    url = `${gitApiUrl}/${workspace}/${project}/diff?path=${file.replace(`/${workspace}/`, '')}`;
                } else {
                    url = resourceApiUrl + file;
                }
                const xhr = new XMLHttpRequest();
                xhr.open("GET", url);
                xhr.setRequestHeader("X-CSRF-Token", "Fetch");
                xhr.setRequestHeader('Dirigible-Editor', 'Monaco');
                xhr.onload = () => {
                    if (xhr.status === 200) {
                        if (project) {
                            let fileObject = JSON.parse(xhr.responseText);
                            resolve({
                                isGit: true,
                                git: fileObject.original || "", // Means it`s not in git
                                modified: fileObject.modified,
                            });
                        } else {
                            resolve({
                                isGit: false,
                                git: "",
                                modified: xhr.responseText,
                            });
                        }
                    } else {
                        reject(`HTTP ${xhr.status} - ${xhr.statusText}`)
                    }
                    csrfToken = xhr.getResponseHeader("x-csrf-token");
                };
                xhr.onerror = () => reject(`HTTP ${xhr.status} - ${xhr.statusText}`);
                xhr.send();
            } else {
                reject(`HTTP ${400} - 'file' parameter cannot be '${file}'`);
            }
        });
    };

    this.saveText = function (text, fileName) {
        return new Promise((resolve, reject) => {
            fileName = fileName || this.resolveFileName();
            if (fileName) {
                const xhr = new XMLHttpRequest();
                xhr.open('PUT', resourceApiUrl + fileName);
                xhr.setRequestHeader('X-Requested-With', 'Fetch');
                xhr.setRequestHeader('X-CSRF-Token', csrfToken);
                xhr.setRequestHeader('Dirigible-Editor', 'Monaco');
                xhr.onload = () => resolve(fileName);
                xhr.onerror = () => reject(xhr.statusText);
                xhr.send(text);
                messageHub.post({ data: fileName }, 'editor.file.saved');
                messageHub.post({
                    data: {
                        path: fileName
                    }
                }, 'workspace.file.selected');
                messageHub.post({ data: 'File [' + fileName + '] saved.' }, 'status.message');
                dirty = false;
            } else {
                reject('file query parameter is not present in the URL');
            }
        });
    };
};

function getViewParameters() {
    if (window.frameElement.hasAttribute("data-parameters")) {
        let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
        parameters.resourceType = params["resourceType"] || "/services/v4/ide/workspaces";
        parameters.contentType = params["contentType"] || "";
        parameters.readOnly = params["readOnly"] || false;
        parameters.gitName = params["gitName"] || "";
        parameters.file = params["file"] || "";
        useParameters = true;
    }
}

function setResourceApiUrl() {
    gitApiUrl = "/services/v4/ide/git";
    editorUrl = new URL(window.location.href);
    getViewParameters();
    let rtype;
    if (useParameters)
        rtype = parameters.resourceType;
    else
        rtype = editorUrl.searchParams.get('rtype');
    if (rtype === "workspace") resourceApiUrl = "/services/v4/ide/workspaces";
    else if (rtype === "repository") resourceApiUrl = "/services/v4/core/repository";
    else if (rtype === "registry") resourceApiUrl = "/services/v4/core/registry";
    else resourceApiUrl = "/services/v4/ide/workspaces";
}

function createEditorInstance(readOnly = false) {
    return new Promise((resolve, reject) => {
        setTimeout(function () {
            try {
                let containerEl = document.getElementById('embeddedEditor');
                if (containerEl.childElementCount > 0) {
                    for (let i = 0; i < containerEl.childElementCount; i++)
                        containerEl.removeChild(containerEl.children.item(i));
                }
                let editor = monaco.editor.create(containerEl, {
                    value: "let x = 0;",
                    automaticLayout: true,
                    readOnly: readOnly,
                });
                resolve(editor);
                window.onresize = function () {
                    editor.layout();
                };
                if (loadingOverview) loadingOverview.classList.add("hide");
            } catch (err) {
                reject(err);
            }
        });
    });
};

function eslintVerify(code){
    const config = {
        "env": {
           "browser": true,
           "node": true
        },
        "parserOptions": {
           "sourceType": "module",
           "ecmaVersion": 2018,
           "ecmaFeatures": {
               "impliedStrict": true
           }
        },
        "rules": {
           "valid-jsdoc": 1,
           "block-scoped-var": 2,
           "curly": 2,
           "default-case": 2,
           "dot-location": [2, "property"],
           "eqeqeq": 2,
           "no-else-return": 1,
           "no-eval": 2,
           "no-loop-func": 1,
           "no-multi-spaces": 1,
           "no-param-reassign": 2,
           "no-unused-expressions": 1,
           "no-unused-vars": 2,
           "no-warning-comments": 1,
           "no-with": 2,
           "strict": 1,
           "no-shadow": 1,
           "no-undef": 2,
           "no-undefined": 2,
           "no-use-before-define": 2,
           "no-sync": 1,
           "array-bracket-spacing": 2,
           "block-spacing": 2,
           "brace-style": [2, "1tbs"],
           "comma-spacing": 2,
           "comma-style": 2,
           "computed-property-spacing": 2,
           "eol-last": 1,
           "func-call-spacing": 2,
           "indent": ["error", "tab"],
           "key-spacing": 2,
           "keyword-spacing": 2,
           "line-comment-position": 1,
           "linebreak-style": 2,
           "lines-around-comment": 2,
           "lines-between-class-members": 2,
           "new-parens": 2,
           "no-array-constructor": 2,
           "no-whitespace-before-property": 2,
           "object-curly-newline": [2, {
               "consistent": true
           }],
           "object-curly-spacing": 2,
           "quotes": 1,
           "semi": 2,
           "space-before-blocks": 2,
           "space-before-function-paren": [2, {
               "anonymous": "always",
               "named": "never",
               "asyncArrow": "always"
           }],
           "space-in-parens": 2,
           "space-infix-ops": 2,
           "space-unary-ops": 2,
           "spaced-comment": 1,
           "switch-colon-spacing": 2,
           "arrow-spacing": 2,
           "prefer-const": 1,
           "prefer-destructuring": 1,
           "prefer-rest-params": 1,
           "prefer-spread": 1,
           "prefer-template": 1,
           "require-await": 2,
           "rest-spread-spacing": 2,
           "template-curly-spacing": 2
        }
    };
    console.log(eslintBundle);
    const result = eslintBundle.runEslint(code, config);
    console.error(result);
}

function createSaveAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-files-save',

        // A label of the action that will be presented to the user.
        label: 'Save',

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS
        ],

        // A precondition for this action.
        precondition: null,

        // A rule to evaluate on top of the precondition in order to dispatch the keybindings.
        keybindingContext: null,

        contextMenuGroupId: 'fileIO',

        contextMenuOrder: 1.5,

        // Method that will be executed when the action is triggered.
        // @param editor The editor instance is passed in as a convinience
        run: function (editor) {
            loadingMessage.innerText = 'Saving...';
            if (loadingOverview) loadingOverview.classList.remove("hide");
            editor.getAction('editor.action.formatDocument').run().then(() => {
                let fileIO = new FileIO();
                fileIO.saveText(editor.getModel().getValue());
                eslintVerify(editor.getModel().getValue());
                if (loadingOverview) loadingOverview.classList.add("hide");
            });
        }
    };
};

function createSearchAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-search',

        // A label of the action that will be presented to the user.
        label: 'Search',

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyF
        ],

        // A precondition for this action.
        precondition: null,

        // A rule to evaluate on top of the precondition in order to dispatch the keybindings.
        keybindingContext: null,

        contextMenuGroupId: 'fileIO',

        contextMenuOrder: 1.5,

        // Method that will be executed when the action is triggered.
        // @param editor The editor instance is passed in as a convinience
        run: function (editor) {
            messageHub.post({
                viewId: "search"
            }, 'ide-core.openView');
        }
    };
};

function createCloseAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-close',

        // A label of the action that will be presented to the user.
        label: 'Close',

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.Alt | monaco.KeyCode.KeyW
        ],

        // A precondition for this action.
        precondition: null,

        // A rule to evaluate on top of the precondition in order to dispatch the keybindings.
        keybindingContext: null,

        contextMenuGroupId: 'fileIO',

        contextMenuOrder: 1.5,

        // Method that will be executed when the action is triggered.
        // @param editor The editor instance is passed in as a convinience
        run: function (editor) {
            let fileIO = new FileIO();
            messageHub.post({ fileName: fileIO.resolveFileName() }, 'ide-core.closeEditor');
        }
    };
};

function createCloseOthersAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-close-others',

        // A label of the action that will be presented to the user.
        label: 'Close Others',

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.Alt | monaco.KeyMod.WinCtrl | monaco.KeyMod.Shift | monaco.KeyCode.KeyW
        ],

        // A precondition for this action.
        precondition: null,

        // A rule to evaluate on top of the precondition in order to dispatch the keybindings.
        keybindingContext: null,

        contextMenuGroupId: 'fileIO',

        contextMenuOrder: 1.5,

        // Method that will be executed when the action is triggered.
        // @param editor The editor instance is passed in as a convinience
        run: function (editor) {
            let fileIO = new FileIO();
            messageHub.post({ fileName: fileIO.resolveFileName() }, 'ide-core.closeOtherEditors');
        }
    };
};

function createCloseAllAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-close-all',

        // A label of the action that will be presented to the user.
        label: 'Close All',

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.Alt | monaco.KeyMod.Shift | monaco.KeyCode.KeyW
        ],

        // A precondition for this action.
        precondition: null,

        // A rule to evaluate on top of the precondition in order to dispatch the keybindings.
        keybindingContext: null,

        contextMenuGroupId: 'fileIO',

        contextMenuOrder: 1.5,

        // Method that will be executed when the action is triggered.
        // @param editor The editor instance is passed in as a convinience
        run: function (editor) {
            messageHub.post('', 'ide-core.closeAllEditors');
        }
    };
};

function loadModuleSuggestions(modulesSuggestions) {
    let xhrModules = new XMLHttpRequest();
    xhrModules.open('GET', '/services/v4/js/ide-monaco-extensions/api/modules.js');
    xhrModules.setRequestHeader('X-CSRF-Token', 'Fetch');
    xhrModules.onload = function (xhrModules) {
        let modules = JSON.parse(xhrModules.target.responseText);
        modules.forEach(e => modulesSuggestions.push(e));
    };
    xhrModules.send();
}

function loadDTS() {
    let cachedDts = window.sessionStorage.getItem('dtsContent');
    if (cachedDts) {
        monaco.languages.typescript.javascriptDefaults.addExtraLib(cachedDts, "")
    } else {
        let xhrModules = new XMLHttpRequest();
        xhrModules.open('GET', '/services/v4/js/ide-monaco-extensions/api/dts.js');
        xhrModules.setRequestHeader('X-CSRF-Token', 'Fetch');
        xhrModules.onload = function (xhrModules) {
            let dtsContent = xhrModules.target.responseText;
            monaco.languages.typescript.javascriptDefaults.addExtraLib(dtsContent, "")
            window.sessionStorage.setItem('dtsContent', dtsContent);
        };
        xhrModules.send();
    }
}

function loadSuggestions(moduleName, suggestions) {
    if (moduleName.split("/").length <= 1) {
        return;
    }

    if (suggestions[moduleName]) {
        return;
    }

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/services/v4/js/ide-monaco-extensions/api/suggestions.js?moduleName=' + moduleName);
    xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
    xhr.onload = function (xhr) {
        if (xhr.target.status === 200) {
            let loadedSuggestions = JSON.parse(xhr.target.responseText);
            suggestions[moduleName] = loadedSuggestions;
        }
    };
    xhr.send();
}

function getModuleImports(text) {
    let moduleImports = text.match(/(var|let)\s[a-zA-Z0-9_-]+\s?=\s?require\(('|")[a-zA-Z0-9_.-//-]+('|")\)/gm);

    if (!moduleImports) {
        moduleImports = [];
    }

    moduleImports = moduleImports.map(function (text) {
        let split = text.split("=");
        let keyWord = split[0].replace("var ", "").replace("let ", "").trim();
        let module = split[1].replace("require(", "").replace(")", "").replaceAll("\"", "").replaceAll("'", "").trim();
        return {
            keyWord: keyWord,
            module: module
        };
    });

    return moduleImports;
}

function traverseAssignment(assignment, assignmentInfo) {
    if (assignment.parentModule) {
        traverseAssignment(assignment.parentModule, assignmentInfo);
    }
    if (assignment.parentModule && !assignment.parentModule.parentModule) {
        assignmentInfo.parentModule = assignment.parentModule
    }
    if (assignment.method) {
        assignmentInfo.methods.push(assignment.method);
    }
}

(function init() {
    setResourceApiUrl();
    require.config({
        paths: {
            'vs': '/webjars/monaco-editor/0.33.0/min/vs',
            'parser': 'js/parser'
        }
    });

    loadModuleSuggestions(modulesSuggestions);

    //@ts-ignore
    require(['vs/editor/editor.main', 'parser/acorn-loose'], function (monaco, acornLoose) {
        let fileIO = new FileIO();
        let fileName = fileIO.resolveFileName();
        let readOnly = fileIO.isReadOnly();
        let _fileObject;
        let _fileType;

        fileIO.getFileType(fileName)
            .then((fileType) => {

                _fileType = fileType;

                fileIO.loadText(fileName)
                    .then((fileObject) => {
                        _fileObject = fileObject;
                        return createEditorInstance(readOnly);
                    })
                    .catch((status) => {
                        if (fileName) messageHub.post({ fileName: fileName }, 'ide-core.closeEditor');
                        console.error(status);
                    })
                    .then((editor) => {
                        _editor = editor;
                        return _fileObject.modified;
                    })
                    .then((fileText) => {
                        if (fileName) {
                            let fileType = _fileType;

                            let moduleImports = getModuleImports(fileText);
                            codeCompletionAssignments = parseAssignments(acornLoose, fileText);

                            moduleImports.forEach(e => loadSuggestions(e.module, codeCompletionSuggestions));

                            messageHub.subscribe(function () {
                                if (dirty) {
                                    fileIO.saveText(_editor.getModel().getValue());
                                }
                            }, "workbench.editor.save");

                            _editor.onDidChangeModel(function () {
                                if (_fileObject.isGit) {
                                    lineDecorations = highlight_changed(
                                        getNewLines(_fileObject.git, fileText),
                                        _editor
                                    );
                                }
                            });
                            let model = monaco.editor.createModel(fileText, fileType || 'text');
                            _editor.setModel(model);
                            if (!readOnly) {
                                _editor.addAction(createSaveAction());
                            }
                            _editor.addAction(createSearchAction());
                            _editor.addAction(createCloseAction());
                            _editor.addAction(createCloseOthersAction());
                            _editor.addAction(createCloseAllAction());
                            _editor.onDidChangeCursorPosition(function (e) {
                                let caretInfo = "Line " + e.position.lineNumber + " : Column " + e.position.column;
                                messageHub.post({ data: caretInfo }, 'status.caret');
                            });
                            _editor.onDidChangeModelContent(function (e) {
                                if (e.changes && e.changes[0].text === ".") {
                                    codeCompletionAssignments = parseAssignments(acornLoose, _editor.getValue());
                                }
                                if (_fileObject.isGit && e.changes) {
                                    let content = _editor.getValue();
                                    lineDecorations = highlight_changed(
                                        getNewLines(_fileObject.git, content),
                                        _editor
                                    );
                                }
                                let newModuleImports = getModuleImports(_editor.getValue());
                                if (e && !dirty) {
                                    dirty = true;
                                    messageHub.post({ data: fileName }, 'editor.file.dirty');
                                }
                                newModuleImports.forEach(function (module) {
                                    if (module.module.split("/").length > 0) {
                                        let newModule = moduleImports.filter(e => e.keyWord === module.keyWord && e.module === module.module)[0];
                                        let moduleChanged = moduleImports.filter(e => e.keyWord === module.keyWord && e.module !== module.module)[0];
                                        let keyWordChanged = moduleImports.filter(e => e.keyWord !== module.keyWord && e.module === module.module)[0];
                                        if (!newModule) {
                                            loadSuggestions(module.module, codeCompletionSuggestions);
                                            moduleImports.push(module);
                                        } else if (moduleChanged) {
                                            moduleChanged.module = module.module;
                                            loadSuggestions(module.module, codeCompletionSuggestions);
                                        } else if (keyWordChanged) {
                                            keyWordChanged.keyWord = module.keyWord;
                                        }
                                    }
                                });
                            });

                            monaco.languages.typescript.javascriptDefaults.addExtraLib('/** Loads external module: \n\n> ```\nlet res = require("http/v4/response");\nres.println("Hello World!");``` */ var require = function(moduleName: string) {return new Module();};', 'js:require.js');
                            monaco.languages.typescript.javascriptDefaults.addExtraLib('/** $. XSJS API */ var $: any;', 'ts:$.js');
                            loadDTS();

                            monaco.languages.registerCompletionItemProvider('javascript', {
                                triggerCharacters: ["\"", "'"],
                                provideCompletionItems: function (model, position) {
                                    let token = model.getValueInRange({
                                        startLineNumber: position.lineNumber,
                                        startColumn: 1,
                                        endLineNumber: position.lineNumber,
                                        endColumn: position.column
                                    })
                                    if (token.indexOf('require("') < 0 && token.indexOf('require(\'') < 0
                                        && token.indexOf('from "') < 0 && token.indexOf('from \'') < 0) {
                                        return { suggestions: [] };
                                    }
                                    let wordPosition = model.getWordUntilPosition(position);
                                    let word = wordPosition.word;
                                    let range = {
                                        startLineNumber: position.lineNumber,
                                        endLineNumber: position.lineNumber,
                                        startColumn: wordPosition.startColumn,
                                        endColumn: wordPosition.endColumn
                                    };
                                    return {
                                        suggestions: modulesSuggestions
                                            .filter(function (e) {
                                                if (word.length > 0) {
                                                    return e.name.toLowerCase().indexOf(word.toLowerCase()) >= 0;
                                                }
                                                return true;
                                            }).map(function (e) {
                                                return {
                                                    label: e.name,
                                                    kind: monaco.languages.CompletionItemKind.Module,
                                                    documentation: e.documentation,
                                                    detail: e.description,
                                                    insertText: e.name,
                                                    range: range
                                                }
                                            })
                                    };
                                }
                            });
                            monaco.languages.registerCompletionItemProvider('javascript', {
                                triggerCharacters: ["."],
                                provideCompletionItems: function (model, position) {
                                    let token = model.getValueInRange({
                                        startLineNumber: position.lineNumber,
                                        startColumn: 1,
                                        endLineNumber: position.lineNumber,
                                        endColumn: position.column
                                    })

                                    let moduleImport = moduleImports.filter(e => token.match(new RegExp(e.keyWord + "." + "([a-zA-Z0-9]+)?", "g")))[0];
                                    // let afterDotToken = token.substring(token.indexOf(".") + 1);
                                    let tokenParts = token.split(".");
                                    let moduleName = moduleImport ? moduleImport.module : null;
                                    if (tokenParts != null && tokenParts.length > 2) {
                                        moduleName = null;
                                    }
                                    let nestedObjectKeyword = null;
                                    if (!moduleName) {
                                        let nestedKeyword = token.split(" ").filter(e => e.indexOf(".") > 0)[0]
                                        if (nestedKeyword) {
                                            nestedObjectKeyword = nestedKeyword.split(".")[0];
                                        }
                                    }
                                    if (!moduleName && !nestedObjectKeyword) {
                                        return { suggestions: [] };
                                    }
                                    let wordPosition = model.getWordUntilPosition(position);
                                    let word = wordPosition.word;
                                    let range = {
                                        startLineNumber: position.lineNumber,
                                        endLineNumber: position.lineNumber,
                                        startColumn: wordPosition.startColumn,
                                        endColumn: wordPosition.endColumn
                                    };
                                    let suggestions = [];
                                    let moduleSuggestions = codeCompletionSuggestions[moduleName];
                                    if (moduleSuggestions) {
                                        Object.keys(moduleSuggestions["exports"]).forEach(suggestionName => {
                                            let suggestion = moduleSuggestions["exports"][suggestionName];
                                            suggestion.name = suggestion.definition;
                                            suggestions.push(suggestion);
                                        });
                                    } else if (nestedObjectKeyword) {
                                        let assignment = codeCompletionAssignments[nestedObjectKeyword];
                                        if (assignment) {
                                            let assignmentInfo = {
                                                parentModule: null,
                                                methods: []
                                            };
                                            traverseAssignment(assignment, assignmentInfo);

                                            let parentObject = "exports";
                                            for (let i = 0; i < assignmentInfo.methods.length; i++) {
                                                parentObject = codeCompletionSuggestions[assignmentInfo.parentModule][parentObject][assignmentInfo.methods[i]].returnType;
                                            }

                                            let moduleSuggestions = codeCompletionSuggestions[assignmentInfo.parentModule];
                                            Object.keys(moduleSuggestions[parentObject]).forEach(suggestionName => {
                                                let suggestion = moduleSuggestions[parentObject][suggestionName];
                                                suggestion.name = suggestion.definition;
                                                suggestions.push(suggestion);
                                            });
                                        }
                                    }
                                    return {
                                        suggestions: suggestions
                                            .filter(function (e) {
                                                if (word.length > 0) {
                                                    return e.name.toLowerCase().startsWith(word.toLowerCase());
                                                }
                                                return true;
                                            })
                                            .map(function (e) {
                                                return {
                                                    label: e.name,
                                                    kind: e.isFunction ? monaco.languages.CompletionItemKind.Function : monaco.languages.CompletionItemKind.Field,
                                                    documentation: {
                                                        value: e.documentation
                                                    },
                                                    detail: e.isFunction ? "function " + e.name : e.name,
                                                    insertText: e.name,
                                                    range: range
                                                }
                                            })
                                    };
                                }
                            });
                        }
                    });
            });

        monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
            noSemanticValidation: false,
            noSyntaxValidation: false,
            noSuggestionDiagnostics: false,
            diagnosticCodesToIgnore: [
                2307,
                2304, // Cannot find name 'exports'.(2304)
                2683, // 'this' implicitly has type 'any' because it does not have a type annotation.(2683)
                7005, // Variable 'ctx' implicitly has an 'any' type.(7005)
                7006, // Parameter 'ctx' implicitly has an 'any' type.(7006),
                7009, // 'new' expression, whose target lacks a construct signature, implicitly has an 'any' type.(7009)
                7034, // Variable 'ctx' implicitly has type 'any' in some locations where its type cannot be determined.(7034)
            ]
        });
        monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
            target: monaco.languages.typescript.ScriptTarget.ES2020,
            strict: true,
            strictNullChecks: true,
            strictPropertyInitialization: true,
            alwaysStrict: true,
            allowNonTsExtensions: true,
            allowUnreachableCode: false,
            allowUnusedLabels: false,
            noUnusedParameters: true,
            noUnusedLocals: true,
            checkJs: true,
            noFallthroughCasesInSwitch: true
        });
        monaco.languages.html.registerHTMLLanguageService('xml', {}, { documentFormattingEdits: true });
        monaco.languages.html.htmlDefaults.setOptions({
            format: {
                tabSize: 2,
                insertSpaces: true,
                endWithNewline: true,
                indentHandlebars: true,
                indentInnerHtml: true,
                wrapLineLength: 120,
                wrapAttributes: "auto",
                extraLiners: "head, body, /html",
                maxPreserveNewLines: null
            }
        });
        cssFormatMonaco(monaco, {
            indent_size: 2,
            newline_between_rules: false,
            end_with_newline: true,
            indent_with_tabs: false,
            space_around_combinator: true
        });
        monaco.editor.setTheme(monacoTheme);
    });
})();
