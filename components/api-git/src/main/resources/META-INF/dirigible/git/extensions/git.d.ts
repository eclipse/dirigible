declare module "@dirigible/git" {
    module client {
        /**
         * Inits git repository
         * @param user
         * @param email
         * @param workspaceName
         * @param projectName
         * @param repositoryName
         * @param commitMessage
         */
        function initRepository(user: string, email: string, workspaceName: string, projectName: string, repositoryName: string, commitMessage: string)

        /**
         * Commits changes.
         * @param user
         * @param userEmail
         * @param workspaceName
         * @param repositoryName
         * @param commitMessage
         * @param add
         */
        function commit(user: string, userEmail: string, workspaceName: string, repositoryName: string, commitMessage: string, add: boolean);

        /**
         * Returns list of repositories in workspace
         * @param workspaceName
         */
        function getGitRepositories(workspaceName: string): string[];

        /**
         * Return hystory for the repository
         * @param repositoryName
         * @param workspaceName
         * @param path
         */

        function getHistory(repositoryName: string, workspaceName: string, path: string);

        /**
         * Deletes repository from workspace
         * @param workspaceName
         * @param repositoryName
         */
        function deleteRepository(workspaceName: string, repositoryName: string)

        /**
         * Clone repository.
         * @param workspaceName
         * @param repositoryUri
         * @param username
         * @param password
         * @param branch
         */
        function cloneRepository(workspaceName: string, repositoryUri: string, username: string, password: string, branch: string)

        /**
         * Pull repository
         * @param workspaceName
         * @param repositoryName
         * @param username
         * @param password
         */
        function pull(workspaceName: string, repositoryName: string, username: string, password: string)

        /**
         * Push to repository
         * @param workspaceName
         * @param repositoryName
         * @param username
         * @param password
         */
        function push(workspaceName: string, repositoryName: string, username: string, password: string)

        /**
         * Checkout
         * @param workspaceName
         * @param repositoryName
         * @param branchName
         */
        function checkout(workspaceName: string, repositoryName: string, branchName: string)

        /**
         *
         * @param workspaceName
         * @param repositoryName
         * @param branchName
         * @param startingPoint
         */
        function createBranch(workspaceName: string, repositoryName: string, branchName: string, startingPoint: string)

        /**
         * Hard reset.
         * @param workspaceName
         * @param repositoryName
         */
        function hardReset(workspaceName: string, repositoryName: string)

        /**
         * Rebase
         * @param workspaceName
         * @param repositoryName
         * @param branchName
         */
        function rebase(workspaceName: string, repositoryName: string, branchName: string)

        /**
         * Status
         * @param workspaceName
         * @param repositoryName
         */
        function status(workspaceName: string, repositoryName: string)

        /**
         * Get Branch
         * @param workspaceName
         * @param repositoryName
         */
        function getBranch(workspaceName: string, repositoryName: string)

        /**
         * Get local branches
         * @param workspaceName
         * @param repositoryName
         */
        function getLocalBranches(workspaceName: string, repositoryName: string)

        /**
         * Get remote branches
         * @param workspaceName
         * @param repositoryName
         */
        function getRemoteBranches(workspaceName: string, repositoryName: string)

        /**
         * Get unstaged changes
         * @param workspaceName
         * @param repositoryName
         */
        function getUnstagedChanges(workspaceName: string, repositoryName: string)

        /**
         * Get staged changes
         * @param workspaceName
         * @param repositoryName
         */
        function getStagedChanges(workspaceName: string, repositoryName: string)

        /**
         * get
         * @param workspaceName
         * @param repositoryName
         * @param filePath
         * @param revStr
         */
        function getFileContent(workspaceName: string, repositoryName: string, filePath: string, revStr: string): string

    }
}
