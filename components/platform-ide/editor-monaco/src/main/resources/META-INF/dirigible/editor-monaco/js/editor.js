const WORKSPACE_API = "/services/ide/workspaces";
const REPOSITORY_API = "/services/core/repository";
const REGISTRY_API = "/services/core/repository/registry/public";

const themingApi = new ThemingApi();
const statusBarApi = new StatusBarApi();
const layoutApi = new LayoutApi();
const workspaceApi = new WorkspaceApi();

let csrfToken;
let lineDecorations = [];

let monacoTheme = 'vs-light';
let headElement = document.getElementsByTagName('head')[0];

let autoThemeListener = false;

function setTheme(theme, monaco) {
    if (!theme) theme = themingApi.getSavedTheme();
    if (theme.links) {
        let themeLinks = headElement.querySelectorAll("link[data-type='theme']");
        for (let i = 0; i < themeLinks.length; i++) {
            headElement.removeChild(themeLinks[i]);
        }
        for (let i = 0; i < theme.links.length; i++) {
            const link = document.createElement('link');
            link.type = 'text/css';
            link.href = theme.links[i];
            link.rel = 'stylesheet';
            link.setAttribute("data-type", "theme");
            headElement.appendChild(link);
        }
    }
    if (theme.type === 'light') {
        monacoTheme = 'vs-light';
        if (monaco) monaco.editor.setTheme(monacoTheme);
        autoThemeListener = false;
    } else if (theme.type === 'dark') {
        monacoTheme = 'blimpkit-dark';
        if (monaco) monaco.editor.setTheme(monacoTheme);
        autoThemeListener = false;
    } else {
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            monacoTheme = 'blimpkit-dark';
        } else monacoTheme = 'vs-light';
        if (monaco) monaco.editor.setTheme(monacoTheme);
        autoThemeListener = true;
    }
}

setTheme();

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
        const fileName = fileIO.resolvePath();
        const readOnly = fileIO.isReadOnly();
        const fileType = await fileIO.getFileType(fileName);
        const isTemplate = fileIO.isFileTemplate(editorParameters.resourcePath);
        const fileObject = await fileIO.loadText(editorParameters.resourcePath, true);

        const dirigibleEditor = new DirigibleEditor(monaco, acornLoose, fileName, readOnly, fileType, isTemplate, fileObject);
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
        statusBarApi.showMessage(message);
    }

    static logErrorMessage(errorMessage) {
        console.error(errorMessage);
        statusBarApi.showError(errorMessage);
    }

    static setEditorDirty(dirty) {
        workspaceApi.setFileDirty({
            path: editorParameters.path,
            workspace: editorParameters.workspace,
            dirty: dirty,
        });
    }
}

function getViewParameters() {
    const dataParameters = window.frameElement?.getAttribute("data-parameters");
    if (dataParameters) {
        const params = JSON.parse(dataParameters);
        const resourcePath = params["file"] || "";
        const path = resourcePath.substring(resourcePath.indexOf('/', resourcePath.indexOf('/') + 1), resourcePath.length)
        return {
            resourceType: params["resourceType"] || "/services/ide/workspaces",
            contentType: params["contentType"] || "",
            readOnly: params["readOnly"] || false,
            gitName: params["gitName"] || "",
            resourcePath: resourcePath,
            workspace: resourcePath.substring(1, resourcePath.length - path.length),
            path: path,
        }
    }
    return {};
}

let editorParameters = getViewParameters();

class FileIO {

    static EDITOR_URL = new URL(window.location.href);

    isReadOnly() {
        return editorParameters.readOnly || false;
    }

    resolveResourcePath() {
        return editorParameters.resourcePath;
    }

    resolvePath() {
        return editorParameters.path;
    }

    resolveWorkspace() {
        return editorParameters.workspace;
    }

    resolveProjectName(filePath) {
        let path = this.resolveResourcePath();
        if (path && filePath.startsWith('../')) {
            const fileNameTokens = path.split('/');
            const relativeTokens = filePath.split('../');
            for (let i = 0; i < relativeTokens.length; i++) {
                fileNameTokens.pop();
            }
            path = fileNameTokens.join('/') + '/' + relativeTokens.filter(e => e != '').join('/');
        }
        if (path) {
            return path.replaceAll('\\', '/').split('/')[2]
        }
        return '';
    }

    resolveRelativePath(basePath, relativePath) {
        if (relativePath.startsWith('../')) {
            const fileNameTokens = basePath.split('/');
            const relativeTokens = relativePath.split('../');
            for (let i = 0; i < relativeTokens.length; i++) {
                fileNameTokens.pop();
            }
            basePath = fileNameTokens.join('/') + '/' + relativeTokens.filter(e => e != '').join('/');
        } else if (relativePath.startsWith('./')) {
            const fileNameTokens = basePath.split('/');
            fileNameTokens.pop();
            const relativeTokens = relativePath.split('./');
            basePath = fileNameTokens.join('/') + '/' + relativeTokens.filter(e => e != '').join('/');
        } else if (TypeScriptUtils.isGlobalImport(relativePath)) {
            return relativePath;
        }
        return basePath.replaceAll('\\', '/').split('/').join('/');
    }

    resolveFilePath(filePath) {
        let path = this.resolveResourcePath();
        if (path && filePath.startsWith('../')) {
            const fileNameTokens = path.split('/');
            const relativeTokens = filePath.split('../');
            for (let i = 0; i < relativeTokens.length; i++) {
                fileNameTokens.pop();
            }
            path = fileNameTokens.join('/') + '/' + relativeTokens.filter(e => e != '').join('/');
        } else if (path && filePath.startsWith('./')) {
            const fileNameTokens = path.split('/');
            fileNameTokens.pop();
            const relativeTokens = filePath.split('./');
            path = fileNameTokens.join('/') + '/' + relativeTokens.filter(e => e != '').join('/');
        } else if (TypeScriptUtils.isGlobalImport(filePath)) {
            return filePath;
        }
        if (path) {
            return path.replaceAll('\\', '/').split('/').slice(2).join('/');
        }
        return '';
    }

    isFileTemplate(fileName) {
        return fileName.endsWith('.template');
    };

    async getFileType(fileName) {
        const response = await fetch('/services/js/editor-monaco/api/fileTypes.js');
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

    async loadText(path, loadGitMetadata = false) {
        try {
            path = path ?? this.resolveResourcePath();
            if (!path) {
                throw new Error(`Unable to load file [${path}], file query parameter is not present in the URL`);
            }
            const workspace = this.resolveWorkspace();
            const projectName = this.resolveProjectName(path);
            const filePath = this.resolveFilePath(path);

            const requestMetadata = this.#buildFileRequestMetadata(path, workspace, filePath, loadGitMetadata);
            let response;

            try {
                response = await fetch(requestMetadata.url, {
                    method: 'GET',
                    headers: {
                        'X-Requested-With': 'Fetch',
                        'Dirigible-Editor': 'Monaco',
                    }
                });
                if (!response.ok) {
                    throw new Error(`Unable to load [${path}, HTTP: ${response.status}, ${response.statusText}]`);
                }
            } catch (e) {
                // Fallback to file in Registry
                response = await fetch(this.#buildRegistryUrl(filePath), {
                    method: 'GET',
                    headers: {
                        'X-Requested-With': 'Fetch',
                        'Dirigible-Editor': 'Monaco',
                    }
                });
                requestMetadata.isGitProject = false;
            }
            if (!response.ok) {
                throw new Error(`Unable to load [${path}, HTTP: ${response.status}, ${response.statusText}]`);
            }

            csrfToken = response.headers.get("x-csrf-token");

            const fileMetadata = {
                workspace: workspace,
                project: projectName,
                filePath: filePath,
                isGit: requestMetadata.isGitProject,
                git: '',
                modified: '',
                sourceCode: '',
                importedFilesNames: [],
            };

            if (requestMetadata.isGitProject) {
                const fileObject = await response.json();
                fileMetadata.git = fileObject.original ?? ""; // File is not in git
                fileMetadata.modified = fileObject.modified;
                fileMetadata.sourceCode = fileObject.modified;
            } else {
                const fileContent = await response.text();
                fileMetadata.modified = fileContent;
                fileMetadata.sourceCode = fileContent;
            }
            if (TypeScriptUtils.isTypeScriptFile(path)) {
                const importedFilesNames = TypeScriptUtils.getImportedModuleFiles(fileMetadata.sourceCode);
                // @ts-ignore
                fileMetadata.importedFilesNames.push(...importedFilesNames);
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
            fileName = fileName || this.resolveResourcePath();
            if (!fileName) {
                throw new Error(`Unable to save file [${fileName}], file query parameter is not present in the URL`);
            }

            const response = await fetch(`${WORKSPACE_API}${fileName}`, {
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

            Utils.setEditorDirty(false);

            workspaceApi.announceFileSaved({
                path: editorParameters.path,
                workspace: editorParameters.workspace,
                contentType: editorParameters.contentType,
                status: editorParameters.gitName && lineDecorations.length ? 'modified' : 'unmodified'
            });

            Utils.logMessage(`File '${fileName}' saved`);

            if (TypeScriptUtils.isTypeScriptFile(fileName)) {
                workspaceApi.postMessage({
                    topic: 'monaco.ts.reload',
                    data: fileName
                });
            }
        } catch (e) {
            // @ts-ignore
            Utils.logErrorMessage(e.message);
            throw e;
        }
    };

    /**
     * "workspace", "repository" or "registry"
     */
    #getResourceType() {
        return editorParameters.resourceType;
    }

    #resolveGitProjectName() {
        return editorParameters.gitName;
    }

    #buildRegistryUrl(filePath) {
        return `${REGISTRY_API}${filePath.startsWith('/') ? '' : '/'}${filePath}`;
    }

    #buildRepositoryUrl(path) {
        return `${REPOSITORY_API}${path.startsWith('/') ? '' : '/'}${path}`;
    }

    #buildWorkspaceUrl(workspace, filePath) {
        return `${WORKSPACE_API}/${workspace}/${filePath}`;
    }

    #buildFileRequestMetadata(path, workspace, filePath, loadGitMetadata) {
        let url;
        let isGitProject = false;

        switch (this.#getResourceType()) {
            case "registry":
                url = this.#buildRegistryUrl(filePath);
                break;
            case "repository":
                url = this.#buildRepositoryUrl(path);
                break;
            default:
                url = this.#buildWorkspaceUrl(workspace, filePath);
        }

        const gitProject = this.#resolveGitProjectName();
        if (loadGitMetadata && gitProject) {
            const gitFolder = "/.git/";
            const isGitFolderLocation = path.indexOf(gitFolder) > 0;
            if (isGitFolderLocation) {
                path = path.substring(path.indexOf(gitFolder) + gitFolder.length);
            }

            let diffPath;
            if (isGitFolderLocation) {
                diffPath = path.substring(path.indexOf(gitProject) + gitProject.length + 1);
            } else {
                diffPath = path.substring(path.indexOf(`/${workspace}/`) + `/${workspace}/`.length);
            }
            url = `/services/ide/git/${workspace}/${gitProject}/diff?path=${diffPath}`;
            isGitProject = true;
        }
        return {
            url: url,
            isGitProject: isGitProject,
        };
    }
}

class EditorActionsProvider {
    static #autoFormatExcludedKey = `${brandingInfo.keyPrefix}.code-editor.autoFormat.excluded`;
    static _toggleAutoFormattingActionRegistration = undefined;

    static isAutoRevealEnabled() {
        const autoRevealEnabled = window.localStorage.getItem(`${brandingInfo.keyPrefix}.code-editor.autoReveal`);
        return autoRevealEnabled === null || autoRevealEnabled === 'true';
    }

    static isAutoFormattingEnabled() {
        const autoFormat = window.localStorage.getItem(`${brandingInfo.keyPrefix}.code-editor.autoFormat`);
        return autoFormat === null || autoFormat === 'true';
    }

    static createSaveAction(formatEnabled = true) {
        const loadingMessage = document.getElementById('loadingMessage');
        return {
            id: 'code-editor-files-save',
            label: 'Save',
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function (editor) {
                if (loadingMessage) {
                    loadingMessage.innerText = 'Saving...';
                }
                if (DirigibleEditor.loadingOverview) {
                    DirigibleEditor.loadingOverview.classList.remove("bk-hidden");
                }
                if (formatEnabled && EditorActionsProvider.isAutoFormattingEnabled() && EditorActionsProvider.#isAutoFormattingEnabledForCurrentFile()) {
                    editor.getAction('editor.action.formatDocument').run().then(() => {
                        DirigibleEditor.saveFileContent(editor);
                    });
                } else {
                    DirigibleEditor.saveFileContent(editor);
                }
            }
        };
    }

    static createSearchAction() {
        return {
            id: 'code-editor-search',
            label: 'Search',
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyF
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                layoutApi.openView({ id: 'search' });
            }
        };
    }

    static createToggleAutoFormattingAction() {
        return {
            id: 'code-editor-toggle-auto-formatting',
            label: EditorActionsProvider.#isAutoFormattingEnabledForCurrentFile() ? "Disable Auto-Formatting" : "Enable Auto-Formatting",
            keybindings: [
                monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyD
            ],
            precondition: null,
            keybindingContext: null,
            contextMenuGroupId: 'fileIO',
            contextMenuOrder: 1.5,
            run: function () {
                const fileIO = new FileIO();
                const fileName = fileIO.resolveResourcePath();

                const filesWithDisabledFormattingListJson = window.localStorage.getItem(EditorActionsProvider.#autoFormatExcludedKey);
                let filesWithDisabledFormattingList = undefined;
                if (filesWithDisabledFormattingListJson) {
                    filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);
                }

                let jsonString = null;

                if (filesWithDisabledFormattingList) {
                    if (filesWithDisabledFormattingList.includes(fileName)) {
                        const removed = filesWithDisabledFormattingList.filter(entry => entry !== fileName);
                        jsonString = JSON.stringify(removed);
                    } else {
                        filesWithDisabledFormattingList.push(fileName);
                        jsonString = JSON.stringify(filesWithDisabledFormattingList);
                    }
                } else {
                    let initialFilesWithDisabledFormattingList = new Array(fileName);
                    jsonString = JSON.stringify(initialFilesWithDisabledFormattingList);
                }

                window.localStorage.setItem(EditorActionsProvider.#autoFormatExcludedKey, jsonString);
                layoutApi.postMessage({ topic: 'code-editor.settings.update', data: { fileName: fileName } });
            }
        };
    }

    static #isAutoFormattingEnabledForCurrentFile() {
        const fileIO = new FileIO();
        const fileName = fileIO.resolveResourcePath();
        const filesWithDisabledFormattingListJson = window.localStorage.getItem(EditorActionsProvider.#autoFormatExcludedKey);
        let filesWithDisabledFormattingList = undefined;
        if (filesWithDisabledFormattingListJson) {
            filesWithDisabledFormattingList = JSON.parse(filesWithDisabledFormattingListJson);
        }

        return !filesWithDisabledFormattingList || !filesWithDisabledFormattingList.includes(fileName);
    }
}

class DirigibleEditor {

    static dirty = false;
    static lastSavedVersionId = undefined;
    static loadingOverview = document.getElementById('loadingOverview');
    static sourceBeingChangedProgramatically = false;
    static computeDiff = new Worker("js/workers/computeDiff.js");

    static closeEditor() {
        workspaceApi.closeFile({
            path: editorParameters.path,
            workspace: editorParameters.workspace,
        });
    }

    static saveFileContent(editor) {
        const fileIO = new FileIO();
        fileIO.saveText(editor.getModel().getValue()).then(() => {
            DirigibleEditor.lastSavedVersionId = editor.getModel().getAlternativeVersionId();
            DirigibleEditor.dirty = false;
        });
        if (DirigibleEditor.loadingOverview) {
            DirigibleEditor.loadingOverview.classList.add("dg-hidden")
        };
    }

    static #isDirty(model) {
        return DirigibleEditor.lastSavedVersionId !== model.getAlternativeVersionId();
    }

    constructor(monaco, acornLoose, fileName, readOnly, fileType, isTemplate, fileObject) {
        this.monaco = monaco;
        this.acornLoose = acornLoose;
        this.fileName = fileName;
        this.readOnly = readOnly;
        this.fileType = fileType;
        this.isTemplate = isTemplate;
        this.fileObject = fileObject;
    }

    async init() {
        if (!this.fileName) {
            return
        }

        this.editor = await this.#createEditorInstance();

        if (TypeScriptUtils.isTypeScriptFile(this.fileName)) {
            TypeScriptUtils.loadImportedFiles(this.monaco, this.fileObject.importedFilesNames);
        }

        const mainFileUri = new this.monaco.Uri().with({ path: this.fileName });
        const model = this.monaco.editor.createModel(this.fileObject.modified, this.fileType || 'text', mainFileUri);
        DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();

        this.editor.setModel(model);

        await TypeScriptUtils.loadDTS(this.monaco);
    }

    async #createEditorInstance() {
        const fileName = this.fileName;
        const readOnly = this.readOnly;
        const fileObject = this.fileObject;
        let diffTimeoutId;
        let importTimeoutId;

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
                        DirigibleEditor.loadingOverview.classList.add("bk-hidden")
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
                if (diffTimeoutId) {
                    clearTimeout(diffTimeoutId);
                }
                diffTimeoutId = setTimeout(function () {
                    DirigibleEditor.computeDiff.postMessage({
                        oldText: fileObject.git,
                        newText: editor.getValue()
                    });
                }, 200);
            }

            if (e.changes) {
                if (importTimeoutId) {
                    clearTimeout(importTimeoutId);
                }
                importTimeoutId = setTimeout(async function () {
                    const importedModuleFiles = TypeScriptUtils.getImportedModuleFiles(editor.getModel().getValue());
                    const newImportedModules = [];
                    for (const module of importedModuleFiles) {
                        let found = false;
                        for (const importedModule of fileObject.importedFilesNames) {
                            if (module === importedModule) {
                                found = true;
                            }
                        }
                        if (!found) {
                            newImportedModules.push(module);
                        }
                    }
                    if (newImportedModules.length > 0) {
                        TypeScriptUtils.loadImportedFiles(monaco, newImportedModules);
                        fileObject.importedFilesNames.push(...newImportedModules);
                    }
                }, 1000);
            }

            const dirty = DirigibleEditor.#isDirty(editor.getModel());
            if (dirty !== DirigibleEditor.dirty) {
                DirigibleEditor.dirty = dirty;
                Utils.setEditorDirty(fileName, dirty);
            }
        });

        editor.onDidFocusEditorText(function () {
            workspaceApi.openFile({
                path: editorParameters.path,
                workspace: editorParameters.workspace,
            });
            if (EditorActionsProvider.isAutoRevealEnabled()) {
                setTimeout(() => {
                    layoutApi.postMessage({
                        topic: 'projects.tree.select',
                        data: {
                            filePath: editorParameters.resourcePath
                        }
                    });
                }, 100);
            }
        });

        editor.onDidChangeCursorPosition(function (e) {
            statusBarApi.showLabel(`Line ${e.position.lineNumber}, Column ${e.position.column}`);
        });

        if (!this.readOnly) {
            editor.addAction(EditorActionsProvider.createSaveAction(!this.isTemplate));
        }
        editor.addAction(EditorActionsProvider.createSearchAction());
        if (!this.isTemplate) {
            EditorActionsProvider._toggleAutoFormattingActionRegistration = editor.addAction(EditorActionsProvider.createToggleAutoFormattingAction());
            layoutApi.addMessageListener({
                topic: 'code-editor.settings.update', handler: (data) => {
                    if (data.fileName && data.fileName === fileName && EditorActionsProvider._toggleAutoFormattingActionRegistration) {
                        // @ts-ignore
                        EditorActionsProvider._toggleAutoFormattingActionRegistration.dispose();
                        EditorActionsProvider._toggleAutoFormattingActionRegistration = editor.addAction(EditorActionsProvider.createToggleAutoFormattingAction());
                    }
                }
            });
        }

        DirigibleEditor.computeDiff.onmessage = function (event) {
            lineDecorations = editor.deltaDecorations(lineDecorations, event.data);
        };
        return editor;
    }

    configureMonaco() {
        const fileObject = this.fileObject;

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
                wrapLineLength: 240,
                wrapAttributes: "auto",
                extraLiners: "head, body, /html",
                maxPreserveNewLines: null
            }
        });

        this.monaco.editor.defineTheme('blimpkit-dark', {
            base: 'vs-dark',
            inherit: true,
            rules: [{ background: '1d1d1d' }],
            colors: {
                'editor.background': '#1d1d1d',
                'breadcrumb.background': '#1d1d1d',
                'minimap.background': '#1d1d1d',
                'editorGutter.background': '#1d1d1d',
                'editorMarkerNavigation.background': '#1d1d1d',
                'input.background': '#242424',
                'input.border': '#4e4e4e',
                'editorWidget.background': '#1d1d1d',
                'editorWidget.border': '#313131',
                'editorSuggestWidget.background': '#262626',
                'dropdown.background': '#262626',
            }
        });

        this.monaco.editor.setTheme(monacoTheme);
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', event => {
            if (autoThemeListener) {
                if (event.matches) monacoTheme = 'blimpkit-dark';
                else monacoTheme = 'vs-light';
                this.monaco.editor.setTheme(monacoTheme);
            }
        });

        themingApi.onThemeChange((theme) => {
            setTheme(theme, this.monaco);
        });

        const getTypeScriptFileImport = (model, position, fileObject) => {
            const lineContent = model.getLineContent(position.lineNumber);
            for (const next of fileObject.importedFilesNames) {
                if (lineContent.includes(next.replace(".ts", ""))) {
                    return new FileIO().resolveFilePath(next);
                }
            }
            return undefined;
        }

        this.monaco.languages.registerImplementationProvider('typescript', {
            provideImplementation: function (model, position) {
                const filePath = getTypeScriptFileImport(model, position, fileObject);
                if (filePath) {
                    workspaceApi.openFile({
                        path: filePath,
                        workspace: new FileIO().resolveWorkspace(),
                        contentType: "typescript",
                    });
                }
            }
        });

        this.monaco.languages.registerDefinitionProvider('typescript', {
            provideDefinition: function (model, position) {
                const filePath = getTypeScriptFileImport(model, position, fileObject);
                if (filePath) {
                    return [{
                        uri: model.uri,
                        range: new monaco.Range(position.lineNumber, position.column, position.lineNumber, position.column),
                    }];
                }
            }
        });
    }

    subscribeEvents() {
        const fileIO = new FileIO();
        const editor = this.editor;
        const monaco = this.monaco;
        const fileObject = this.fileObject;

        workspaceApi.onSaveFile(async function (data) {
            if (data.file && data.file === fileIO.resolveResourcePath()) {
                const model = editor.getModel();
                if (DirigibleEditor.#isDirty(model)) {
                    fileIO.saveText(model.getValue()).then(() => {
                        DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                        DirigibleEditor.dirty = false;
                    });
                }
            }
        });

        workspaceApi.onSaveAll(async function () {
            const model = editor.getModel();
            if (DirigibleEditor.#isDirty(model)) {
                fileIO.saveText(model.getValue());
                DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                DirigibleEditor.dirty = false;
            }
        });

        workspaceApi.onReloadEditorParams((data) => {
            if (data.workspace === editorParameters.workspace && data.path === editorParameters.path) {
                editorParameters = getViewParameters();
            }
        });

        layoutApi.onFocusView((data) => {
            if (data.params && data.params.file === fileIO.resolveResourcePath()) {
                editor.focus();
                if (EditorActionsProvider.isAutoRevealEnabled()) {
                    layoutApi.postMessage({
                        topic: 'projects.tree.select',
                        data: { filePath: data.id }
                    });
                }
            }
        });

        workspaceApi.addMessageListener({
            topic: 'monaco.ts.reload',
            handler: (fileData) => {
                if (fileData === editorParameters.resourcePath) {
                    return;
                }
                DirigibleEditor.sourceBeingChangedProgramatically = true;
                const model = editor.getModel();
                model.setValue(model.getValue());
                DirigibleEditor.lastSavedVersionId = model.getAlternativeVersionId();
                DirigibleEditor.sourceBeingChangedProgramatically = false;

                TypeScriptUtils.loadImportedFiles(monaco, fileObject.importedFilesNames, true);
            },
        });
    }
}

class TypeScriptUtils {

    // Find all ES6 import statements and their modules
    static #IMPORT_REGEX = /import\s+(?:\{(?:\s*\w?\s*,?)*\}|(?:\*\s+as\s+\w+)|\w+)\s+from\s+['"]([^'"]+)['"]/g;

    static #IMPORTED_FILES = new Set();

    static isTypeScriptFile(fileName) {
        return fileName && fileName.endsWith(".ts");
    }

    static getImportedModuleFiles(content) {
        const importedModules = [];

        let match;
        while ((match = TypeScriptUtils.#IMPORT_REGEX.exec(content)) !== null) {
            let modulePath = match[1];
            if (!modulePath.startsWith('sdk/')) {
                if (!modulePath.endsWith(".json")) {
                    modulePath += ".ts";
                }
                importedModules.push(modulePath);
            }
        }
        return importedModules;
    }

    static isGlobalImport(path) {
        return !path.startsWith("/") && !path.startsWith("./") && !path.startsWith("../") && !path.startsWith("sdk/")
    }

    static loadImportedFiles = async (monaco, importedFiles, isReload = false) => {
        if (isReload) {
            TypeScriptUtils.#IMPORTED_FILES.clear();
        }
        const fileIO = new FileIO();
        for (const importedFile of importedFiles) {
            try {
                const importedFilePath = fileIO.resolveFilePath(importedFile);
                if (TypeScriptUtils.#IMPORTED_FILES.has(importedFilePath)) {
                    continue;
                }
                const importedFileMetadata = await fileIO.loadText(importedFile);
                let uriPath = `/${importedFileMetadata.workspace}/${importedFileMetadata.filePath}`;
                if (TypeScriptUtils.isGlobalImport(importedFile)) {
                    monaco.languages.typescript.typescriptDefaults.addExtraLib(
                        importedFileMetadata.sourceCode,
                        `file:///node_modules/@types/${importedFile.substring(0, importedFile.indexOf('.ts'))}.d.ts`
                    );
                }

                const uri = new monaco.Uri().with({ path: uriPath });
                if (isReload) {
                    const model = monaco.editor.getModel(uri);
                    model.setValue(importedFileMetadata.sourceCode);
                } else {
                    const fileType = uri.path.endsWith(".json") ? "json" : "typescript";
                    monaco.editor.createModel(importedFileMetadata.sourceCode, fileType, uri);
                }
                if (importedFileMetadata.importedFilesNames?.length > 0) {
                    const relativeImportedPaths = importedFileMetadata.importedFilesNames.map(e => fileIO.resolveRelativePath(importedFile, e));
                    TypeScriptUtils.loadImportedFiles(monaco, relativeImportedPaths, isReload);
                }
                TypeScriptUtils.#IMPORTED_FILES.add(importedFilePath);
            } catch (e) {
                Utils.logErrorMessage(e);
            }
        }
    }

    static async loadDTS(monaco) {
        const res = await fetch('/services/js/all-dts');
        const allDts = await res.json();
        for (const dts of allDts) {
            monaco.languages.typescript.javascriptDefaults.addExtraLib(dts.content, dts.filePath);
            monaco.languages.typescript.typescriptDefaults.addExtraLib(dts.content, dts.filePath);
        }

        const cachedDts = window.sessionStorage.getItem('dtsContent');
        if (cachedDts) {
            monaco.languages.typescript.javascriptDefaults.addExtraLib(cachedDts, "");
            monaco.languages.typescript.typescriptDefaults.addExtraLib(cachedDts, "");
        } else {
            const xhrModules = new XMLHttpRequest();
            xhrModules.open('GET', '/services/js/editor-monaco-extensions/api/dts.js');
            xhrModules.setRequestHeader('X-CSRF-Token', 'Fetch');
            xhrModules.onload = function (xhrModules) {
                // @ts-ignore
                const dtsContent = xhrModules.target.responseText;
                monaco.languages.typescript.javascriptDefaults.addExtraLib(dtsContent, "");
                monaco.languages.typescript.typescriptDefaults.addExtraLib(dtsContent, "");
                window.sessionStorage.setItem('dtsContent', dtsContent);
            };
            xhrModules.onerror = function (error) {
                console.error('Error loading DTS', error);
                statusBarApi.showError('Error loading DTS');
            };
            xhrModules.send();
        }
    }
}