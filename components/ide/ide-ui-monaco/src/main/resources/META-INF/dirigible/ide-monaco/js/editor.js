let messageHub = new FramesMessageHub();
let csrfToken;
let _dirty = false;
let lastSavedVersionId;

let modulesSuggestions = [];
let codeCompletionSuggestions = {};
let codeCompletionAssignments = {};
let _editor;
let resourceApiUrl;
let editorUrl;
let gitApiUrl;
let loadingOverview = document.getElementById('loadingOverview');
let loadingMessage = document.getElementById('loadingMessage');
let _toggleAutoFormattingActionRegistration = null;
let lineDecorations = [];
let useParameters = false; // Temp boolean used for transitioning to new parameter method.
let parameters = {
    resourceType: "",
    contentType: "",
    readOnly: false,
    gitName: "",
    file: ""
};
let sourceBeingChangedProgramatically = false;

/*eslint-disable no-extend-native */
String.prototype.replaceAll = function (search, replacement) {
    let target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

let computeDiff = new Worker("js/workers/computeDiff.js");
computeDiff.onmessage = function (event) {
    lineDecorations = _editor.deltaDecorations(lineDecorations, event.data);;
};

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
            xhr.open("GET", "/services/js/ide-monaco/api/fileTypes.js");
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
                                git: fileObject.original || "", // File is not in git
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
                        if (xhr.responseText)
                            reject(JSON.parse(xhr.responseText));
                        else reject(`HTTP ${xhr.status} - Error loading '${parameters.file}'`);
                        messageHub.post({
                            message: `Error loading '${parameters.file}'`
                        }, 'ide.status.error');
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
                fetch(resourceApiUrl + fileName, {
                    method: 'PUT',
                    body: text,
                    headers: {
                        'X-Requested-With': 'Fetch',
                        'X-CSRF-Token': csrfToken,
                        'Dirigible-Editor': 'Monaco',
                        'Content-Type': 'text/plain'
                    }
                }).then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText);
                    }

                    resolve(fileName);
                    let fileDescriptor = {
                        name: fileName.substring(fileName.lastIndexOf('/') + 1),
                        path: fileName.substring(fileName.indexOf('/', 1)),
                        contentType: parameters.contentType,
                        workspace: fileName.substring(1, fileName.indexOf('/', 1)),
                    };
                    if (parameters.gitName) {
                        if (lineDecorations.length)
                            fileDescriptor.status = 'modified';
                        else fileDescriptor.status = 'unmodified';
                    }
                    messageHub.post({ resourcePath: fileName, isDirty: false }, 'ide-core.setEditorDirty');
                    messageHub.post(fileDescriptor, 'ide.file.saved');
                    messageHub.post({
                        message: `File '${fileName}' saved`
                    }, 'ide.status.message');
                    if (isTypeScriptFile(fileName)) {
                        messageHub.post({ fileName }, 'ide.ts.reload');
                    }
                }).catch(ex => {
                    reject(ex.message);
                    messageHub.post({
                        message: `Error saving '${fileName}'`
                    }, 'ide.status.error');
                    // messageHub.post({ data: { file: fileName, error: ex.message } }, 'editor.file.save.failed');
                });
            } else {
                reject('file query parameter is not present in the URL');
            }
        });
    };
};

function isTypeScriptFile(fileName) {
    return fileName && fileName.endsWith(".ts");
}

function getViewParameters() {
    if (window.frameElement.hasAttribute("data-parameters")) {
        let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
        parameters.resourceType = params["resourceType"] || "/services/ide/workspaces";
        parameters.contentType = params["contentType"] || "";
        parameters.readOnly = params["readOnly"] || false;
        parameters.gitName = params["gitName"] || "";
        parameters.file = params["file"] || "";
        useParameters = true;
    }
}

function setResourceApiUrl() {
    gitApiUrl = "/services/ide/git";
    editorUrl = new URL(window.location.href);
    getViewParameters();
    let rtype;
    if (useParameters)
        rtype = parameters.resourceType;
    else
        rtype = editorUrl.searchParams.get('rtype');
    if (rtype === "workspace") resourceApiUrl = "/services/ide/workspaces";
    else if (rtype === "repository") resourceApiUrl = "/services/core/repository";
    else if (rtype === "registry") resourceApiUrl = "/services/core/registry";
    else resourceApiUrl = "/services/ide/workspaces";
}

function closeEditor() {
    messageHub.post({ resourcePath: parameters.file }, 'ide-core.closeEditor');
}

function createEditorInstance(fileName, readOnly = false) {
    return new Promise((resolve, reject) => {
        setTimeout(function () {
            try {
                let containerEl = document.getElementById('embeddedEditor');
                if (containerEl.childElementCount > 0) {
                    for (let i = 0; i < containerEl.childElementCount; i++)
                        containerEl.removeChild(containerEl.children.item(i));
                }
                const editorConfig = {
                    value: '',
                    automaticLayout: true,
                    readOnly: readOnly,
                };
                if (isTypeScriptFile(fileName)) {
                    editorConfig.language = 'typescript';
                }

                let editor = monaco.editor.create(containerEl, editorConfig);
                resolve(editor);
                window.onresize = function () {
                    editor.layout();
                };
                if (loadingOverview) loadingOverview.classList.add("dg-hidden");
            } catch (err) {
                reject(err);
            }
        });
    });
};

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
            if (loadingOverview) loadingOverview.classList.remove("dg-hidden");
            if (isAutoFormattingEnabledForCurrentFile()) {
                editor.getAction('editor.action.formatDocument').run().then(() => {
                    saveFileContent(editor);
                });
            }
            else {
                saveFileContent(editor);
            }
        }
    };
};

function saveFileContent(editor) {
    const fileIO = new FileIO();
    fileIO.saveText(editor.getModel().getValue()).then(() => {
        lastSavedVersionId = editor.getModel().getAlternativeVersionId();
        _dirty = false;
    });
    if (loadingOverview) loadingOverview.classList.add("dg-hidden");
}

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
        run: function () {
            callCloseEditor();
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
            messageHub.post({ resourcePath: fileIO.resolveFileName() }, 'ide-core.closeOtherEditors');
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

function createToggleAutoFormattingAction() {
    return {
        // An unique identifier of the contributed action.
        id: 'dirigible-toggle-auto-formatting',

        // A label of the action that will be presented to the user.
        label: isAutoFormattingEnabledForCurrentFile() ? "Disable Auto-Formatting" : "Enable Auto-Formatting",

        // An optional array of keybindings for the action.
        keybindings: [
            monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyD
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
            let fileName = fileIO.resolveFileName();

            let filesWithDisabledFormattingListJson = window.localStorage.getItem('DIRIGIBLE.filesWithDisabledFormattingList');
            let filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);

            let jsonString = null;

            if (filesWithDisabledFormattingList) {
                if (filesWithDisabledFormattingList.includes(fileName)) {
                    let removed = filesWithDisabledFormattingList.filter(entry => entry !== fileName);
                    jsonString = JSON.stringify(removed);
                    console.log("Re-enabled auto formatting for file: " + fileName);
                }
                else {
                    filesWithDisabledFormattingList.push(fileName);
                    jsonString = JSON.stringify(filesWithDisabledFormattingList);
                    console.log("Disabled auto formatting for file: " + fileName);
                }
            }
            else {
                let initialFilesWithDisabledFormattingList = new Array(fileName);
                jsonString = JSON.stringify(initialFilesWithDisabledFormattingList);
                console.log("Disabled auto formatting for file: " + fileName);
            }

            window.localStorage.setItem('DIRIGIBLE.filesWithDisabledFormattingList', jsonString);
            updateAutoFormattingAction(editor);
        }
    };
};

function updateAutoFormattingAction(editor) {
    _toggleAutoFormattingActionRegistration.dispose();
    _toggleAutoFormattingActionRegistration = editor.addAction(createToggleAutoFormattingAction());
}

function isAutoFormattingEnabledForCurrentFile() {
    let fileIO = new FileIO();
    let fileName = fileIO.resolveFileName();
    let filesWithDisabledFormattingListJson = window.localStorage.getItem('DIRIGIBLE.filesWithDisabledFormattingList');
    let filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);

    if (filesWithDisabledFormattingList && filesWithDisabledFormattingList.includes(fileName)) {
        return false;
    }
    else {
        return true;
    }
}

function loadModuleSuggestions(modulesSuggestions) {
    let xhrModules = new XMLHttpRequest();
    xhrModules.open('GET', '/services/js/ide-monaco-extensions/api/modules.js');
    xhrModules.setRequestHeader('X-CSRF-Token', 'Fetch');
    xhrModules.onload = function (xhrModules) {
        let modules = JSON.parse(xhrModules.target.responseText);
        modules.forEach(e => modulesSuggestions.push(e));
    };
    xhrModules.onerror = function (error) {
        console.error('Error loading module suggestions', error);
        messageHub.post({
            message: 'Error loading module suggestions'
        }, 'ide.status.error');
    };
    xhrModules.send();
}

async function loadDTS() {
    const res = await fetch('/services/js/all-dts');
    const allDts = await res.json();
    for (const dts of allDts) {
        monaco.languages.typescript.javascriptDefaults.addExtraLib(dts.content, dts.filePath);
        monaco.languages.typescript.typescriptDefaults.addExtraLib(dts.content, dts.filePath);
        modulesSuggestions.push({ name: dts.moduleName });
    }

    let cachedDts = window.sessionStorage.getItem('dtsContent');
    if (cachedDts) {
        monaco.languages.typescript.javascriptDefaults.addExtraLib(cachedDts, "");
        monaco.languages.typescript.typescriptDefaults.addExtraLib(cachedDts, "");
    } else {
        let xhrModules = new XMLHttpRequest();
        xhrModules.open('GET', '/services/js/ide-monaco-extensions/api/dts.js');
        xhrModules.setRequestHeader('X-CSRF-Token', 'Fetch');
        xhrModules.onload = function (xhrModules) {
            let dtsContent = xhrModules.target.responseText;
            monaco.languages.typescript.javascriptDefaults.addExtraLib(dtsContent, "");
            monaco.languages.typescript.typescriptDefaults.addExtraLib(dtsContent, "");
            window.sessionStorage.setItem('dtsContent', dtsContent);
        };
        xhrModules.onerror = function (error) {
            console.error('Error loading DTS', error);
            messageHub.post({
                message: 'Error loading DTS'
            }, 'ide.status.error');
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
    xhr.open('GET', '/services/js/ide-monaco-extensions/api/suggestions.js?moduleName=' + moduleName);
    xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
    xhr.onload = function (xhr) {
        if (xhr.target.status === 200 && xhr.target.responseText) {
            let loadedSuggestions = JSON.parse(xhr.target.responseText);
            suggestions[moduleName] = loadedSuggestions;
        }
    };
    xhr.onerror = function (error) {
        console.error('Error loading suggestions', error);
        messageHub.post({
            message: 'Error loading suggestions'
        }, 'ide.status.error');
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

function isDirty(model) {
    return lastSavedVersionId !== model.getAlternativeVersionId();
}

(function init() {
    setTheme();
    setResourceApiUrl();
    require.config({
        paths: {
            'vs': '/webjars/monaco-editor/min/vs',
            'parser': 'js/parser'
        }
    });

    loadModuleSuggestions(modulesSuggestions);

    //@ts-ignore
    require(['vs/editor/editor.main', 'parser/acorn-loose'], function (monaco, acornLoose) {
        messageHub.subscribe(function () {
            setTheme(false);
            monaco.editor.setTheme(monacoTheme);
        }, 'ide.themeChange');

        const fileIO = new FileIO();
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
                        return createEditorInstance(fileName, readOnly);
                    })
                    .catch((status) => {
                        console.error(status);
                        closeEditor();
                    })
                    .then((editor) => {
                        _editor = editor;
                        return _fileObject.modified;
                    })
                    .then((fileTextOrJson) => {
                        if (fileName) {
                            let fileType = _fileType;

                            const fileText = isTypeScriptFile(fileName) ? JSON.parse(fileTextOrJson).sourceCode : fileTextOrJson;

                            let moduleImports = getModuleImports(fileText);
                            codeCompletionAssignments = parseAssignments(acornLoose, fileText);

                            moduleImports.forEach(e => loadSuggestions(e.module, codeCompletionSuggestions));

                            messageHub.subscribe(function (msg) {
                                let file = msg.data && typeof msg.data === 'object' && msg.data.file;
                                if (file && file !== fileName)
                                    return;

                                let model = _editor.getModel();
                                if (isDirty(model)) {
                                    fileIO.saveText(model.getValue()).then(() => {
                                        lastSavedVersionId = model.getAlternativeVersionId();
                                        _dirty = false;
                                    });
                                }
                            }, "editor.file.save");

                            messageHub.subscribe(function (event) {
                                let file = event.resourcePath;
                                if (file === fileName) {
                                    getViewParameters();
                                    fileName = fileIO.resolveFileName();
                                }
                            }, "core.editors.reloadParams");

                            messageHub.subscribe(function () {
                                let model = _editor.getModel();
                                if (isDirty(model)) {
                                    fileIO.saveText(model.getValue()).then(() => {
                                        lastSavedVersionId = model.getAlternativeVersionId();
                                        _dirty = false;
                                    });
                                }
                            }, "editor.file.save.all");

                            messageHub.subscribe(function (msg) {
                                let file = msg.resourcePath;
                                if (file !== fileName)
                                    return;
                                _editor.focus();
                            }, "ide-core.setEditorFocusGain");

                            _editor.onDidFocusEditorText(function () {
                                messageHub.post({ resourcePath: fileName }, 'ide-core.setFocusedEditor');
                            });

                            _editor.onDidChangeModel(function () {
                                if (_fileObject.isGit) {
                                    computeDiff.postMessage(
                                        {
                                            oldText: _fileObject.git,
                                            newText: fileText
                                        }
                                    );
                                }
                            });

                            if (isTypeScriptFile(fileName)) {
                                const importedFiles = JSON.parse(fileTextOrJson).importedFilesNames;
                                const loadImportedFiles = (isReload) => {
                                    for (const importedFile of importedFiles) {
                                        fileIO.loadText(importedFile)
                                            .then((fileObject) => {
                                                const importedFile = JSON.parse(fileObject.modified);
                                                const uri = new monaco.Uri().with({ path: `/${importedFile.workspace}/${importedFile.project}/${importedFile.filePath}` });
                                                if (isReload) {
                                                    const model = monaco.editor.getModel(uri);
                                                    model.setValue(importedFile.sourceCode);
                                                } else {
                                                    monaco.editor.createModel(importedFile.sourceCode, fileType, uri);
                                                }
                                            })
                                            .catch((status) => {
                                                console.error(status);
                                            })
                                    }
                                };
                                loadImportedFiles(false);
                                messageHub.subscribe(() => {
                                    loadImportedFiles(true);
                                }, "ide.ts.reload")
                            }

                            const mainFileUri = new monaco.Uri().with({ path: fileName });
                            let model = monaco.editor.createModel(fileText, fileType || 'text', mainFileUri);
                            lastSavedVersionId = model.getAlternativeVersionId();

                            messageHub.subscribe((changed) => {
                                if (changed.fileName === fileName) return;
                                sourceBeingChangedProgramatically = true;
                                model.setValue(model.getValue());
                                lastSavedVersionId = model.getAlternativeVersionId();
                                sourceBeingChangedProgramatically = false;
                            }, "ide.ts.reload")


                            _editor.setModel(model);
                            if (!readOnly) {
                                _editor.addAction(createSaveAction());
                            }
                            _editor.addAction(createSearchAction());
                            _editor.addAction(createCloseAction());
                            _editor.addAction(createCloseOthersAction());
                            _editor.addAction(createCloseAllAction());
                            _toggleAutoFormattingActionRegistration = _editor.addAction(createToggleAutoFormattingAction());
                            _editor.onDidChangeCursorPosition(function (e) {
                                messageHub.post(
                                    {
                                        text: `Line ${e.position.lineNumber} : Column ${e.position.column}`
                                    },
                                    'ide.status.caret',
                                );
                            });
                            let to = 0;
                            _editor.onDidChangeModelContent(function (e) {
                                if (sourceBeingChangedProgramatically) return;

                                if (e.changes && e.changes[0].text === ".") {
                                    codeCompletionAssignments = parseAssignments(acornLoose, _editor.getValue());
                                }

                                if (_fileObject.isGit && e.changes) {
                                    if (to) { clearTimeout(to); }
                                    to = setTimeout(function () {
                                        computeDiff.postMessage(
                                            {
                                                oldText: _fileObject.git,
                                                newText: _editor.getValue()
                                            }
                                        );
                                    }, 200);

                                }
                                let newModuleImports = getModuleImports(_editor.getValue());
                                let dirty = isDirty(_editor.getModel());
                                if (dirty !== _dirty) {
                                    _dirty = dirty;
                                    messageHub.post({ resourcePath: fileName, isDirty: dirty }, 'ide-core.setEditorDirty');
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

                            monaco.languages.typescript.javascriptDefaults.addExtraLib('/** Loads external module: \n\n> ```\nlet res = require("http/v8/response");\nres.println("Hello World!");``` */ var require = function(moduleName: string) {return new Module();};', 'js:require.js');
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
                2792, // Cannot find module - for local module imports
                6196, // declared but never used - class
                1219, // Experimental support for decorators
                2307,
                2304, // Cannot find name 'exports'.(2304)
                2683, // 'this' implicitly has type 'any' because it does not have a type annotation.(2683)
                7005, // Variable 'ctx' implicitly has an 'any' type.(7005)
                7006, // Parameter 'ctx' implicitly has an 'any' type.(7006),
                7009, // 'new' expression, whose target lacks a construct signature, implicitly has an 'any' type.(7009)
                7034, // Variable 'ctx' implicitly has type 'any' in some locations where its type cannot be determined.(7034)
            ]
        });

        monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
            noSemanticValidation: false,
            noSyntaxValidation: false,
            noSuggestionDiagnostics: false,
            diagnosticCodesToIgnore: [
                6196, // declared but never used - class
                1219, // Experimental support for decorators
            ]
        });

        monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
            target: monaco.languages.typescript.ScriptTarget.ESNext,
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
            noFallthroughCasesInSwitch: true,
            module: (fileName?.endsWith(".mjs") === true) ? monaco.languages.typescript.ModuleKind.ESNext : monaco.languages.typescript.ModuleKind.CommonJS
        });
        monaco.languages.typescript.javascriptDefaults.getCompilerOptions().moduleResolution = monaco.languages.typescript.ModuleResolutionKind.NodeJs;
        monaco.languages.typescript.typescriptDefaults.getCompilerOptions().moduleResolution = monaco.languages.typescript.ModuleResolutionKind.NodeJs;
        monaco.languages.typescript.typescriptDefaults.getCompilerOptions().jsx = (fileName?.endsWith(".tsx") === true) ? "react" : undefined;
        monaco.languages.typescript.javascriptDefaults.getCompilerOptions().jsx = (fileName?.endsWith(".jsx") === true) ? "react" : undefined,

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
        monaco.editor.defineTheme('quartz-dark', {
            base: 'vs-dark',
            inherit: true,
            rules: [{ background: '1c2228' }],
            colors: {
                'editor.background': '#1c2228',
                'breadcrumb.background': '#1c2228',
                'minimap.background': '#1c2228',
                'editorGutter.background': '#1c2228',
                'editorMarkerNavigation.background': '#1c2228',
                'input.background': '#29313a',
                'input.border': '#8696a9',
                'editorWidget.background': '#1c2228',
                'editorWidget.border': '#495767',
                'editorSuggestWidget.background': '#29313a',
                'dropdown.background': '#29313a',
            }
        });
        monaco.editor.setTheme(monacoTheme);
    });
})();
