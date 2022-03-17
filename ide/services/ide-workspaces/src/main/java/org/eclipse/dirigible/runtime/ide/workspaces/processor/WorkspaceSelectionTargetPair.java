/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The Workspace Selection Target Pair including conflict resolution rules.
 */
public class WorkspaceSelectionTargetPair {

        private ArrayList<SelectedNode> sourceSelection;

        private String sourceWorkspace;

        private String targetWorkspace;

        private String target;

        /**
         * Gets the source workspace.
         *
         * @return the sourceWorkspace
         */
        public String getSourceWorkspace() {
            return sourceWorkspace;
        }

        /**
         * Sets the source.
         *
         * @param sourceWorkspace the new source workspace
         */
        public void setSourceWorkspace(String sourceWorkspace) {
            this.sourceWorkspace = sourceWorkspace;
        }

        /**
         * Gets the source.
         *
         * @return the source
         */
        public ArrayList<SelectedNode> getSource() {
            return sourceSelection;
        }

        /**
         * Sets the source.
         *
         * @param source the new source
         */
        public void setSource(ArrayList<SelectedNode> source) {
            this.sourceSelection = source;
        }

        /**
         * Gets the target workspace.
         *
         * @return the targetWorkspace
         */
        public String getTargetWorkspace() {
            return targetWorkspace;
        }

        /**
         * Sets the target workspace.
         *
         * @param targetWorkspace the new target workspace
         */
        public void setTargetWorkspace(String targetWorkspace) {
            this.targetWorkspace = targetWorkspace;
        }

        /**
         * Gets the target.
         *
         * @return the target
         */
        public String getTarget() {
            return target;
        }

        /**
         * Sets the target.
         *
         * @param target the new target
         */
        public void setTarget(String target) {
            this.target = target;
        }

    /**
     * @param skipPath the path to skip all inside
     */
        public void skipByPath(String skipPath) {
            for (int i = 0; i < sourceSelection.size(); i++) {
                String pathOfNode = sourceSelection.get(i).getPath();
                String path2compare = pathOfNode.substring(0, pathOfNode.length() >= skipPath.length() ? skipPath.length() : pathOfNode.length());
                System.out.println(path2compare + ", " + pathOfNode);
                if (path2compare.equals(skipPath)) {
                    sourceSelection.set(i, sourceSelection.get(i).setResolution("skip"));
                }
            }
        }


    public class SelectedNode {
            private String id;
            private String path;
            private String type;
            private String norootpath;
            private String resolution;
            public String getInternalPath() {
                return norootpath;
            };
            public String getNodeType() {
                return type;
            }
            public String getPath() {
                return path;
            }
            public void setPath(String newPath) {
                path = newPath;
            }
            public String getRelativePath() {
                Path fullpath = Paths.get(path);
                return fullpath.subpath(2, fullpath.getNameCount()).toString().replaceAll("^/+", "");
            }
            public String getResolution() {
                String defaultResolution = "rename";
                if (resolution != null) return resolution;
                return defaultResolution;
            }
            public SelectedNode setResolution(String resolution) {
                this.resolution = resolution;
                return this;
            }
        }
}
