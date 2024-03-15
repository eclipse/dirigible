const messageHub = new FramesMessageHub();
let csrfToken;
let resourceApiUrl;
let lineDecorations = [];

const workspaceAPI = "/services/ide/workspaces";
const repositoryAPI = "/services/core/repository";
const registryAPI = "/services/core/registry";

function setResourceApiUrl() {
    const editorUrl = new URL(window.location.href);
    new ViewParameters();
    let resourceType;
    if (ViewParameters.useParameters) {
        resourceType = ViewParameters.parameters.resourceType;
    } else {
        resourceType = editorUrl.searchParams.get('rtype');
    }
    if (resourceType === "workspace") {
        resourceApiUrl = "/services/ide/workspaces";
    } else if (resourceType === "repository") {
        resourceApiUrl = "/services/core/repository";
    } else if (resourceType === "registry") {
        resourceApiUrl = "/services/core/registry";
    } else {
        resourceApiUrl = "/services/ide/workspaces";
    }
}

// @ts-ignore
require.config({
    paths: {
        'vs': '/webjars/monaco-editor/min/vs',
        'parser': 'js/parser'
    }
});

// @ts-ignore
require(['vs/editor/editor.main', 'parser/acorn-loose'], async function (monaco, acornLoose) {
    try {
        const fileIO = new FileIO();
        const fileName = fileIO.resolveFileName();
        const readOnly = fileIO.isReadOnly();
        const fileType = await fileIO.getFileType(fileName);
        const fileObject = await fileIO.loadText(fileName);

        const dirigibleEditor = new DirigibleEditor(monaco, acornLoose, fileName, readOnly, fileType, fileObject);
        await dirigibleEditor.init();
        dirigibleEditor.configureMonaco();
        dirigibleEditor.subscribeEvents();

    } catch (e) {
        console.error(e);
        DirigibleEditor.closeEditor();
    }
});

class Utils {

    static logMessage(message) {
        messageHub.post({
            message: message
        }, 'ide.status.message');
    }

    static logErrorMessage(errorMessage) {
        console.error(errorMessage);
        messageHub.post({
            message: errorMessage
        }, 'ide.status.error');
    }

    static setEditorDirty(fileName, isDirty) {
        messageHub.post({
            resourcePath: fileName,
            isDirty: isDirty
        }, 'ide-core.setEditorDirty');
    }
}

class ViewParameters {

    static useParameters = false;
    static parameters = {
        resourceType: "",
        contentType: "",
        readOnly: false,
        gitName: "",
        file: ""
    };

    constructor() {
        if (window.frameElement && window.frameElement.hasAttribute("data-parameters")) {
            const dataParameters = window.frameElement.getAttribute("data-parameters");
            if (dataParameters) {
                const params = JSON.parse(dataParameters);
                ViewParameters.parameters.resourceType = params["resourceType"] || "/services/ide/workspaces";
                ViewParameters.parameters.contentType = params["contentType"] || "";
                ViewParameters.parameters.readOnly = params["readOnly"] || false;
                ViewParameters.parameters.gitName = params["gitName"] || "";
                ViewParameters.parameters.file = params["file"] || "";
                ViewParameters.useParameters = true;
            }
        }
    }
}

class FileIO {

    static editorUrl = new URL(window.location.href);

    constructor() {
        new ViewParameters();
    }

    isReadOnly() {
        if (ViewParameters.useParameters) {
            return ViewParameters.parameters.readOnly || false;
        }
        return FileIO.editorUrl.searchParams.get('readOnly') || false;
    }

    resolveGitProjectName() {
        return ViewParameters.useParameters ? ViewParameters.parameters.gitName : FileIO.editorUrl.searchParams.get('gitName')
    }

    resolveFileName() {
        if (ViewParameters.useParameters) {
            this.readOnly = ViewParameters.parameters.readOnly || false;
            return ViewParameters.parameters.file;
        }
        this.readOnly = FileIO.editorUrl.searchParams.get('readOnly') || false;
        return FileIO.editorUrl.searchParams.get('file');
    };

    async getFileType(fileName) {
        const response = await fetch('/services/js/ide-monaco/api/fileTypes.js');
        csrfToken = response.headers.get("x-csrf-token");
        if (response.ok) {
            const fileTypes = await response.json();
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
            return fileType;
        }
        Utils.logErrorMessage(`Unable to determine the fileType of [${fileName}], HTTP: ${response.status}, ${response.statusText}]`);
    };

    async loadText(fileName) {
        try {
            fileName = fileName || this.resolveFileName();
            if (!fileName) {
                throw new Error(`Unable to load file [${fileName}], file query parameter is not present in the URL`);
            }

            const gitProject = this.resolveGitProjectName();
            let resourceUrl = resourceApiUrl + fileName;
            let gitResourceUrl;
            if (gitProject) {
                const gitFolder = "/.git/";
                const isGitFolderLocation = fileName.indexOf(gitFolder) > 0;
                if (isGitFolderLocation) {
                    fileName = fileName.substring(fileName.indexOf(gitFolder) + gitFolder.length);
                }
                const workspace = fileName.replace('\\', '/').split('/')[1];

                let path;
                if (isGitFolderLocation) {
                    path = fileName.substring(fileName.indexOf(gitProject) + gitProject.length + 1);
                    resourceUrl = `${resourceApiUrl}/${workspace}/${path}`;
                } else {
                    path = fileName.substring(fileName.indexOf(`/${workspace}/`) + `/${workspace}/`.length);
                }
                gitResourceUrl = `/services/ide/git/${workspace}/${gitProject}/diff?path=${path}`;
            }

            const response = await fetch(gitProject ? gitResourceUrl : resourceUrl, {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'Fetch',
                    'Dirigible-Editor': 'Monaco',
                }
            });

            if (!response.ok) {
                throw new Error(`Unable to load [${fileName}, HTTP: ${response.status}, ${response.statusText}]`);
            }

            csrfToken = response.headers.get("x-csrf-token");

            const fileMetadata = {
                isGit: false,
                git: '',
                modified: '',
                workspace: '',
                project: '',
                filePath: '',
                sourceCode: '',
                importedFilesNames: [],
            };
            debugger
            if (gitProject) {
                const fileObject = await response.json();
                fileMetadata.isGit = true;
                fileMetadata.git = fileObject.original || ""; // File is not in git
                fileMetadata.modified = fileObject.modified;
                fileMetadata.sourceCode = fileObject.modified;
            } else {
                const fileContent = await response.text();
                fileMetadata.modified = fileContent;
                fileMetadata.sourceCode = fileContent;
            }
            if (TypeScriptUtils.isTypeScriptFile(fileName)) {

                // Find all ES6 import statements and their modules
                const importRegex = /import\s+(?:\{(?:\s*\w+\s*,?)*\}|(?:\*\s+as\s+\w+)|\w+)\s+from\s+['"]([^'"]+)['"]/g;

                let match;
                while ((match = importRegex.exec(fileMetadata.sourceCode)) !== null) {
                    const modulePath = match[1];
                    if (!modulePath.startsWith('sdk/')) {
                        // @ts-ignore
                        fileMetadata.importedFilesNames.push(modulePath);
                    }
                }
            }
            return fileMetadata;
        } catch (e) {
            // @ts-ignore
            Utils.logErrorMessage(e.message);
            throw e;
        }
    };

    async saveText(text, fileName) {
        try {
            fileName = fileName || this.resolveFileName();
            if (!fileName) {
                throw new Error(`Unable to save file [${fileName}], file query parameter is not present in the URL`);
            }

            const response = await fetch(resourceApiUrl + fileName, {
                method: 'PUT',
                body: text,
                headers: {
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken,
                    'Dirigible-Editor': 'Monaco',
                    'Content-Type': 'text/plain'
                }
            });

            if (!response.ok) {
                throw new Error(`Unable to save [${fileName}, HTTP: ${response.status}, ${response.statusText}]`);
            }

            Utils.setEditorDirty(fileName, false);

            messageHub.post({
                name: fileName.substring(fileName.lastIndexOf('/') + 1),
                path: fileName.substring(fileName.indexOf('/', 1)),
                contentType: ViewParameters.parameters.contentType,
                workspace: fileName.substring(1, fileName.indexOf('/', 1)),
                status: ViewParameters.parameters.gitName && lineDecorations.length ? 'modified' : 'unmodified'
            }, 'ide.file.saved');

            Utils.logMessage(`File '${fileName}' saved`);

            if (TypeScriptUtils.isTypeScriptFile(fileName)) {
                messageHub.post({
                    fileName
                }, 'ide.ts.reload');
            }
        } catch (e) {
            // @ts-ignore
            Utils.logErrorMessage(e.message);
            throw e;
        }
    };
}

class EditorActionsProvider {

    static _toggleAutoFormattingActionRegistration = undefined;
    static _toggleAutoRevealActionRegistration = undefined;
    static _autoRevealActionEnabled = true;

    static isAutoFormattingEnabledForCurrentFile() {
        const fileIO = new FileIO();
        const fileName = fileIO.resolveFileName();
        const filesWithDisabledFormattingListJson = window.localStorage.getItem('DIRIGIBLE.filesWithDisabledFormattingList');
        let filesWithDisabledFormattingList = undefined;
        if (filesWithDisabledFormattingListJson) {
            filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);
        }

        return !filesWithDisabledFormattingList || !filesWithDisabledFormattingList.includes(fileName);
    }

    static isAutoRevealEnabled() {
        const autoRevealEnabled = window.localStorage.getItem('DIRIGIBLE.autoRevealActionEnabled');
        return autoRevealEnabled === null || autoRevealEnabled === 'true';
    }

    createSaveAction() {
        const loadingMessage = document.getElementById('loadingMessage');
        return {
            id: 'dirigible-files-save',
            label: 'Save',
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: async function (editor) {
                if (loadingMessage) {
                    loadingMessage.innerText = 'Saving...';
                }
                if (DirigibleEditor.loadingOverview) {
                    DirigibleEditor.loadingOverview.classList.remove("dg-hidden");
                }
                if (EditorActionsProvider.isAutoFormattingEnabledForCurrentFile()) {
                    await editor.getAction('editor.action.formatDocument').run().then(async () => {
                        await DirigibleEditor.saveFileContent(editor);
                    });
                } else {
                    await DirigibleEditor.saveFileContent(editor);
                }
            }
        };
    }

    createSearchAction() {
        return {
            id: 'dirigible-search',
            label: 'Search',
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyF
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                messageHub.post({
                    viewId: "search"
                }, 'ide-core.openView');
            }
        };
    }

    createCloseAction() {
        return {
            id: 'dirigible-close',
            label: 'Close',
            keybindings: [
                monaco.KeyMod.Alt | monaco.KeyCode.KeyW
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                DirigibleEditor.closeEditor();
            }
        };
    }

    createCloseOthersAction() {
        return {
            id: 'dirigible-close-others',
            label: 'Close Others',
            keybindings: [
                monaco.KeyMod.Alt | monaco.KeyMod.WinCtrl | monaco.KeyMod.Shift | monaco.KeyCode.KeyW
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                const fileIO = new FileIO();
                messageHub.post({ resourcePath: fileIO.resolveFileName() }, 'ide-core.closeOtherEditors');
            }
        };
    }

    createCloseAllAction() {
        return {
            id: 'dirigible-close-all',
            label: 'Close All',
            keybindings: [
                monaco.KeyMod.Alt | monaco.KeyMod.Shift | monaco.KeyCode.KeyW
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                messageHub.post('', 'ide-core.closeAllEditors');
            }
        };
    }

    createToggleAutoFormattingAction() {
        return {
            id: 'dirigible-toggle-auto-formatting',
            label: EditorActionsProvider.isAutoFormattingEnabledForCurrentFile() ? "Disable Auto-Formatting" : "Enable Auto-Formatting",
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyD
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function (editor) {
                let fileIO = new FileIO();
                let fileName = fileIO.resolveFileName();

                const filesWithDisabledFormattingListJson = window.localStorage.getItem('DIRIGIBLE.filesWithDisabledFormattingList');
                let filesWithDisabledFormattingList = undefined;
                if (filesWithDisabledFormattingListJson) {
                    filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);
                }

                let jsonString = null;

                if (filesWithDisabledFormattingList) {
                    if (filesWithDisabledFormattingList.includes(fileName)) {
                        const removed = filesWithDisabledFormattingList.filter(entry => entry !== fileName);
                        jsonString = JSON.stringify(removed);
                        console.log("Re-enabled auto formatting for file: " + fileName);
                    } else {
                        filesWithDisabledFormattingList.push(fileName);
                        jsonString = JSON.stringify(filesWithDisabledFormattingList);
                        console.log("Disabled auto formatting for file: " + fileName);
                    }
                } else {
                    let initialFilesWithDisabledFormattingList = new Array(fileName);
                    jsonString = JSON.stringify(initialFilesWithDisabledFormattingList);
                    console.log("Disabled auto formatting for file: " + fileName);
                }

                window.localStorage.setItem('DIRIGIBLE.filesWithDisabledFormattingList', jsonString);

                if (EditorActionsProvider._toggleAutoFormattingActionRegistration) {
                    // @ts-ignore
                    EditorActionsProvider._toggleAutoFormattingActionRegistration.dispose();
                    EditorActionsProvider._toggleAutoFormattingActionRegistration = editor.addAction(new EditorActionsProvider().createToggleAutoFormattingAction());
                }
            }
        };
    }

    createToggleAutoRevealAction() {
        return {
            id: 'dirigible-toggle-auto-reveal',
            label: EditorActionsProvider._autoRevealActionEnabled ? "Disable Auto-Reveal Active File" : "Enable Auto-Reveal Active File",
            keybindings: [],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function (editor) {
                EditorActionsProvider._autoRevealActionEnabled = !EditorActionsProvider._autoRevealActionEnabled;
                window.localStorage.setItem('DIRIGIBLE.autoRevealActionEnabled', `${EditorActionsProvider._autoRevealActionEnabled}`);

                if (EditorActionsProvider._toggleAutoRevealActionRegistration) {
                    // @ts-ignore
                    EditorActionsProvider._toggleAutoRevealActionRegistration.dispose();
                    EditorActionsProvider._toggleAutoRevealActionRegistration = editor.addAction(new EditorActionsProvider().createToggleAutoRevealAction());
                }
            }
        };
    }
}

class DirigibleEditor {

    static dirty = false;
    static lastSavedVersionId = undefined;
    static loadingOverview = document.getElementById('loadingOverview');
    static sourceBeingChangedProgramatically = false;
    static computeDiff = new Worker("js/workers/computeDiff.js");

    static isDirty(model) {
        return DirigibleEditor.lastSavedVersionId !== model.getAlternativeVersionId();
    }

    static closeEditor() {
        messageHub.post({ resourcePath: ViewParameters.parameters.file }, 'ide-core.closeEditor');
    }

    static async saveFileContent(editor) {
        const fileIO = new FileIO();
        await fileIO.saveText(editor.getModel().getValue()).then(() => {
            DirigibleEditor.lastSavedVersionId = editor.getModel().getAlternativeVersionId();
            DirigibleEditor.dirty = false;
        });
        if (DirigibleEditor.loadingOverview) {
            DirigibleEditor.loadingOverview.classList.add("dg-hidden")
        };
    }

    constructor(monaco, acornLoose, fileName, readOnly, fileType, fileObject) {
        this.monaco = monaco;
        this.acornLoose = acornLoose;
        this.fileName = fileName;
        this.readOnly = readOnly;
        this.fileType = fileType;
        this.fileObject = fileObject;
    }

    async init() {
        const fileIO = new FileIO();

        this.editor = await this.createEditorInstance();

        if (this.fileName) {
            const fileType = this.fileType;

            if (TypeScriptUtils.isTypeScriptFile(this.fileName)) {
                const loadImportedFiles = (isReload, importedFiles) => {
                    for (const importedFile of importedFiles) {
                        fileIO.loadText(importedFile)
                            .then((importedFile) => {
                                const uri = new this.monaco.Uri().with({ path: `/${importedFile.workspace}/${importedFile.project}/${importedFile.filePath}` });
                                if (isReload) {
                                    const model = this.monaco.editor.getModel(uri);
                                    model.setValue(importedFile.sourceCode);
                                } else {
                                    this.monaco.editor.createModel(importedFile.sourceCode, fileType, uri);
                                }
                                if (importedFile.importedFilesNames?.length > 0) {
                                    loadImportedFiles(isReload, importedFile.importedFilesNames);
                                }
                            })
                            .catch((status) => {
                                console.error(status);
                            })
                    }
                };
                loadImportedFiles(false, this.fileObject.importedFilesNames);
                // messageHub.subscribe(() => {
                //     loadImportedFiles(true, fileMetadata.importedFilesNames);
                // }, "ide.ts.reload")
            }

            const mainFileUri = new this.monaco.Uri().with({ path: this.fileName });
            const model = this.monaco.editor.createModel(this.fileObject.modified, this.fileType || 'text', mainFileUri);
            DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();

            messageHub.subscribe((changed) => {
                if (changed.fileName === this.fileName) {
                    return;
                }
                DirigibleEditor.sourceBeingChangedProgramatically = true;
                model.setValue(model.getValue());
                DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                DirigibleEditor.sourceBeingChangedProgramatically = false;
            }, "ide.ts.reload")


            this.editor.setModel(model);

            await TypeScriptUtils.loadDTS(this.monaco);
        }
    }

    async createEditorInstance() {
        const fileName = this.fileName;
        const readOnly = this.readOnly;
        const fileObject = this.fileObject;
        let to = 0;

        const editor = await new Promise((resolve, reject) => {
            setTimeout(function () {
                try {
                    let containerEl = document.getElementById('embeddedEditor');
                    if (containerEl && containerEl.childElementCount > 0) {
                        for (let i = 0; i < containerEl.childElementCount; i++)
                            // @ts-ignore
                            containerEl.removeChild(containerEl.children.item(i));
                    }
                    const editorConfig = {
                        value: '',
                        automaticLayout: true,
                        readOnly: readOnly,
                    };
                    if (TypeScriptUtils.isTypeScriptFile(fileName)) {
                        // @ts-ignore
                        editorConfig.language = 'typescript';
                    }

                    const editor = this.monaco.editor.create(containerEl, editorConfig);
                    resolve(editor);
                    window.onresize = function () {
                        editor.layout();
                    };
                    if (DirigibleEditor.loadingOverview) {
                        DirigibleEditor.loadingOverview.classList.add("dg-hidden")
                    };
                } catch (err) {
                    reject(err);
                }
            });
        });

        editor.onDidChangeModel(function () {
            if (fileObject.isGit) {
                DirigibleEditor.computeDiff.postMessage({
                    oldText: fileObject.git,
                    newText: fileObject.modified
                });
            }
        });

        editor.onDidChangeModelContent(function (e) {
            if (DirigibleEditor.sourceBeingChangedProgramatically) {
                return;
            }

            if (fileObject.isGit && e.changes) {
                if (to) {
                    clearTimeout(to);
                }
                to = setTimeout(function () {
                    DirigibleEditor.computeDiff.postMessage({
                        oldText: fileObject.git,
                        newText: editor.getValue()
                    });
                }, 200);
            }

            const dirty = DirigibleEditor.isDirty(editor.getModel());
            if (dirty !== DirigibleEditor.dirty) {
                DirigibleEditor.dirty = dirty;
                Utils.setEditorDirty(fileName, dirty);
            }
        });

        editor.onDidFocusEditorText(function () {
            messageHub.post({ resourcePath: fileName }, 'ide-core.setFocusedEditor');
            if (EditorActionsProvider.isAutoRevealEnabled()) {
                setTimeout(() => {
                    messageHub.post({
                        data: {
                            filePath: fileName
                        }
                    }, 'projects.tree.select');
                }, 100);
            }
        });

        editor.onDidChangeCursorPosition(function (e) {
            messageHub.post(
                {
                    text: `Line ${e.position.lineNumber}, Column ${e.position.column}`
                },
                'ide.status.caret',
            );
        });

        const editorActionsProvider = new EditorActionsProvider();
        if (!this.readOnly) {
            editor.addAction(editorActionsProvider.createSaveAction());
        }
        editor.addAction(editorActionsProvider.createSearchAction());
        editor.addAction(editorActionsProvider.createCloseAction());
        editor.addAction(editorActionsProvider.createCloseOthersAction());
        editor.addAction(editorActionsProvider.createCloseAllAction());
        EditorActionsProvider._toggleAutoFormattingActionRegistration = editor.addAction(editorActionsProvider.createToggleAutoFormattingAction());
        EditorActionsProvider._toggleAutoRevealActionRegistration = editor.addAction(editorActionsProvider.createToggleAutoRevealAction());

        DirigibleEditor.computeDiff.onmessage = function (event) {
            lineDecorations = editor.deltaDecorations(lineDecorations, event.data);
        };
        return editor;
    }

    configureMonaco() {
        setTheme();
        this.monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
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

        this.monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
            noSemanticValidation: false,
            noSyntaxValidation: false,
            noSuggestionDiagnostics: false,
            diagnosticCodesToIgnore: [
                6196, // declared but never used - class
                1219, // Experimental support for decorators
            ]
        });

        this.monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
            target: this.monaco.languages.typescript.ScriptTarget.ESNext,
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
            module: (this.fileName?.endsWith(".mjs") === true) ? this.monaco.languages.typescript.ModuleKind.ESNext : this.monaco.languages.typescript.ModuleKind.CommonJS,
            moduleResolution: this.monaco.languages.typescript.ModuleResolutionKind.NodeJs,
            resolveJsonModule: true,
            jsx: (this.fileName?.endsWith(".jsx") === true) ? "react" : undefined
        });

        this.monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
            target: this.monaco.languages.typescript.ScriptTarget.ESNext,
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
            module: this.monaco.languages.typescript.ModuleKind.ESNext,
            moduleResolution: this.monaco.languages.typescript.ModuleResolutionKind.NodeJs,
            esModuleInterop: true,
            resolveJsonModule: true,
            jsx: (this.fileName?.endsWith(".tsx") === true) ? "react" : undefined
        });

        this.monaco.languages.typescript.javascriptDefaults.addExtraLib('/** Loads external module: \n\n> ```\nlet res = require("http/v8/response");\nres.println("Hello World!");``` */ var require = function(moduleName: string) {return new Module();};', 'js:require.js');

        this.monaco.languages.typescript.javascriptDefaults.addExtraLib('/** $. XSJS API */ var $: any;', 'ts:$.js');

        this.monaco.languages.html.registerHTMLLanguageService('xml', {}, { documentFormattingEdits: true });

        this.monaco.languages.html.htmlDefaults.setOptions({
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

        this.monaco.editor.defineTheme('quartz-dark', {
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

        this.monaco.editor.setTheme(monacoTheme);

        messageHub.subscribe(function () {
            setTheme(false);
            monaco.editor.setTheme(monacoTheme);
        }, 'ide.themeChange');
    }

    subscribeEvents() {
        const fileIO = new FileIO();
        const editor = this.editor;

        messageHub.subscribe(async function (msg) {
            const file = msg.data && typeof msg.data === 'object' && msg.data.file;
            if (file && file !== fileIO.resolveFileName()) {
                return;
            }

            const model = editor.getModel();
            if (DirigibleEditor.isDirty(model)) {
                await fileIO.saveText(model.getValue()).then(() => {
                    DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                    DirigibleEditor.dirty = false;
                });
            }
        }, "editor.file.save");

        messageHub.subscribe(async function () {
            const model = editor.getModel();
            if (DirigibleEditor.isDirty(model)) {
                await fileIO.saveText(model.getValue());
                DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                DirigibleEditor.dirty = false;
            }
        }, "editor.file.save.all");

        messageHub.subscribe(function (event) {
            const file = event.resourcePath;
            if (file === fileName) {
                new ViewParameters();
            }
        }, "core.editors.reloadParams");

        messageHub.subscribe(function (msg) {
            const file = msg.resourcePath;
            if (file !== fileName) {
                return;
            }
            editor.focus();
        }, "ide-core.setEditorFocusGain");
    }
}

class TypeScriptUtils {

    static isTypeScriptFile(fileName) {
        return fileName && fileName.endsWith(".ts");
    }

    static async loadDTS(monaco) {
        const res = await fetch('/services/js/all-dts');
        const allDts = await res.json();
        for (const dts of allDts) {
            monaco.languages.typescript.javascriptDefaults.addExtraLib(dts.content, dts.filePath);
            monaco.languages.typescript.typescriptDefaults.addExtraLib(dts.content, dts.filePath);
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
                // @ts-ignore
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
}

setResourceApiUrl();
