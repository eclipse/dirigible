/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
(function () {
  angular.module('app', ['builder', 'builder.components', 'validator.rules', 'ngSanitize']).run([
    '$builder', function ($builder) {
      $builder.registerComponent('sampleInput', {
        group: 'Composite',
        label: 'Sample',
        description: 'From html template',
        placeholder: 'placeholder',
        required: false,
        validationOptions: [
          {
            label: 'none',
            rule: '/.*/'
          }, {
            label: 'number',
            rule: '[number]'
          }, {
            label: 'email',
            rule: '[email]'
          }, {
            label: 'url',
            rule: '[url]'
          }
        ],
        templateUrl: 'designer/template.html',
        popoverTemplateUrl: 'designer/popoverTemplate.html'
      });
    }
  ]).controller('DesignerController', [
    '$scope', '$builder', '$validator', function ($scope, $builder, $validator) {

      let $ = jQuery;
      let messageHub = new FramesMessageHub();
      let contents;

      function getResource(resourcePath) {
        let xhr = new XMLHttpRequest();
        xhr.open('GET', resourcePath, false);
        xhr.send();
        if (xhr.status === 200) {
          return xhr.responseText;
        }
      }

      function loadContents(file) {
        if (file) {
          return getResource('/services/v4/ide/workspaces' + file);
        }
        console.error('file parameter is not present in the URL');
      }

      function getViewParameters() {
        if (window.frameElement.hasAttribute("data-parameters")) {
          let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
          $scope.file = params["file"];
        } else {
          let searchParams = new URLSearchParams(window.location.search);
          $scope.file = searchParams.get('file');
        }
      }

      function load() {
        getViewParameters();
        contents = loadContents($scope.file);
        if (!contents || contents === null || contents === "") {
          contents = '{"metadata":{"feeds":[]},"form":[]}';
        }
        let description = JSON.parse(contents);
        if (!description.form) {
          description.form = {};
        }
        if (!description.metadata) {
          description.metadata = {};
        }
        if (!description.metadata.feeds) {
          description.metadata.feeds = [];
        }
        if (!description.metadata.styles) {
          description.metadata.styles = [];
        }
        if (!description.metadata.scripts) {
          description.metadata.scripts = [];
        }
        if (!description.metadata.handlers) {
          description.metadata.handlers = [];
        }
        $scope.components = description.form;
        $scope.metadata = description.metadata;
        $scope.defaultValue = {};
        $scope.data = {};
        $scope.metadata.feeds.forEach(feed => {
          let data = getResource(feed.url);
          if (feed.primary) {
            if (data) {
              $scope.data = JSON.parse(data);
            } else {
              console.error("The feed: " + feed.name + " at: " + feed.url + " is not available");
            }
          } else {
            if (data) {
              $scope[feed.name] = JSON.parse(data);
            } else {
              console.error("The feed: " + feed.name + " at: " + feed.url + " is not available");
            }
          }
        });
        $.each($scope.components, function (i, item) {
          let formObj = $builder.addFormObject('default', item);
          $scope.defaultValue[formObj.id] = $scope.data[formObj.model];
        });
      }
      $scope.getData = function (control) {
        alert(JSON.stringify(control));
      };

      load();

      function saveContents(text, publish) {
        console.log('Save called...');
        if ($scope.file) {
          let xhr = new XMLHttpRequest();
          xhr.open('PUT', '/services/v4/ide/workspaces' + $scope.file);
          xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
              console.log('file saved: ' + $scope.file);
              if (publish) {
                setTimeout(function () { publishFile(); }, 800);
              }
            }
          };
          xhr.send(text);
          messageHub.post({
            name: $scope.file.substring($scope.file.lastIndexOf('/') + 1),
            path: $scope.file.substring($scope.file.indexOf('/', 1)),
            contentType: 'application/json+form', // TODO: Take this from data-parameters
            workspace: $scope.file.substring(1, $scope.file.indexOf('/', 1)),
          }, 'ide.file.saved');
          messageHub.post({ message: `File '${$scope.file}' saved` }, 'ide.status.message');
        } else {
          console.error('file parameter is not present in the request');
        }
      }

      function prepareContents() {
        let description = {};
        description.metadata = $scope.metadata;
        description.form = $scope.form;
        return JSON.stringify(description);
      }

      function publishFile() {
        console.log('Publish called...');
        if ($scope.file) {
          let xhr = new XMLHttpRequest();
          xhr.open('POST', '/services/v4/ide/publisher/request' + $scope.file.substring(0, $scope.file.lastIndexOf('/')));
          xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
              console.log('publish request sent for file: ' + $scope.file);
              messageHub.post({ data: $scope.file }, 'workspace.file.published');
            }
          };
          xhr.send("{}");
        } else {
          console.error('file parameter is not present in the request');
        }
      }

      $scope.save = function () {
        contents = prepareContents();
        saveContents(contents, false);
      };

      $scope.saveAndPublish = function () {
        contents = prepareContents();
        saveContents(contents, true);
      };

      messageHub.subscribe(
        function () {
          let current = prepareContents();
          if (contents !== current) {
            $scope.save();
          }
        },
        "editor.file.save.all"
      );

      messageHub.subscribe(
        function (msg) {
          let file = msg.data && typeof msg.data === 'object' && msg.data.file;
          let current = prepareContents();
          if (file && file === $scope.file && contents !== current)
            $scope.save();
        },
        "editor.file.save"
      );

      $scope.$watch(function () {
        let current = prepareContents();
        if (contents !== current) {
          messageHub.post({ resourcePath: $scope.file, isDirty: true }, 'ide-core.setEditorDirty');
        } else {
          messageHub.post({ resourcePath: $scope.file, isDirty: false }, 'ide-core.setEditorDirty');
        }
      });

      // metadata

      // Feeds
      $scope.openNewFeedDialog = function () {
        $scope.actionType = 'new';
        $scope.feed = {};
        $scope.feed.url = "";
        $scope.feed.primary = false;
        toggleFeedModal();
      };

      $scope.openEditFeedDialog = function (entity) {
        $scope.actionType = 'update';
        $scope.feed = entity;
        toggleFeedModal();
      };

      $scope.openDeleteFeedDialog = function (entity) {
        $scope.actionType = 'delete';
        $scope.feed = entity;
        toggleFeedModal();
      };

      $scope.closeFeed = function () {
        load();
        toggleFeedModal();
      };

      $scope.createFeed = function () {
        if (!$scope.metadata) {
          $scope.metadata = {};
        }
        if (!$scope.metadata.feeds) {
          $scope.metadata.feeds = [];
        }
        let exists = $scope.metadata.feeds.filter(function (e) {
          return e.name === $scope.feed.name;
        });
        if (exists.length === 0) {
          $scope.metadata.feeds.push($scope.feed);
          toggleFeedModal();
        } else {
          $scope.error = "Feed with a name [" + $scope.feed.name + "] already exists!";
        }

      };

      $scope.updateFeed = function () {
        // auto-wired
        toggleFeedModal();
      };

      $scope.deleteFeed = function () {
        $scope.metadata.feeds = $scope.metadata.feeds.filter(function (e) {
          return e !== $scope.feed;
        });
        toggleFeedModal();
      };


      function toggleFeedModal() {
        $('#feedModal').modal('toggle');
        $scope.error = null;
      }
      // -- Feeds


      // Styles
      $scope.openNewStyleDialog = function () {
        $scope.actionType = 'new';
        $scope.style = {};
        $scope.style.url = "";
        toggleStyleModal();
      };

      $scope.openEditStyleDialog = function (entity) {
        $scope.actionType = 'update';
        $scope.style = entity;
        toggleStyleModal();
      };

      $scope.openDeleteStyleDialog = function (entity) {
        $scope.actionType = 'delete';
        $scope.style = entity;
        toggleStyleModal();
      };

      $scope.closeStyle = function () {
        load();
        toggleStyleModal();
      };

      $scope.createStyle = function () {
        if (!$scope.metadata) {
          $scope.metadata = {};
        }
        if (!$scope.metadata.styles) {
          $scope.metadata.styles = [];
        }
        let exists = $scope.metadata.styles.filter(function (e) {
          return e.name === $scope.styles.name;
        });
        if (exists.length === 0) {
          $scope.metadata.styles.push($scope.style);
          toggleStyleModal();
        } else {
          $scope.error = "Style with a name [" + $scope.style.name + "] already exists!";
        }

      };

      $scope.updateStyle = function () {
        // auto-wired
        toggleStyleModal();
      };

      $scope.deleteStyle = function () {
        $scope.metadata.styles = $scope.metadata.styles.filter(function (e) {
          return e !== $scope.styles;
        });
        toggleStyleModal();
      };


      function toggleStyleModal() {
        $('#styleModal').modal('toggle');
        $scope.error = null;
      }
      // -- Styles


      // Scripts
      $scope.openNewScriptDialog = function () {
        $scope.actionType = 'new';
        $scope.script = {};
        $scope.script.url = "";
        toggleScriptModal();
      };

      $scope.openEditScriptDialog = function (entity) {
        $scope.actionType = 'update';
        $scope.script = entity;
        toggleScriptModal();
      };

      $scope.openDeleteScriptDialog = function (entity) {
        $scope.actionType = 'delete';
        $scope.script = entity;
        toggleScriptModal();
      };

      $scope.close = function () {
        load();
        toggleScriptModal();
      };

      $scope.createScript = function () {
        if (!$scope.metadata) {
          $scope.metadata = {};
        }
        if (!$scope.metadata.scripts) {
          $scope.metadata.scripts = [];
        }
        let exists = $scope.metadata.scripts.filter(function (e) {
          return e.name === $scope.script.name;
        });
        if (exists.length === 0) {
          $scope.metadata.scripts.push($scope.script);
          toggleScriptModal();
        } else {
          $scope.error = "Script with a name [" + $scope.script.name + "] already exists!";
        }

      };

      $scope.updateScript = function () {
        // auto-wired
        toggleScriptModal();
      };

      $scope.deleteScript = function () {
        $scope.metadata.scripts = $scope.metadata.scripts.filter(function (e) {
          return e !== $scope.script;
        });
        toggleScriptModal();
      };


      function toggleScriptModal() {
        $('#scriptModal').modal('toggle');
        $scope.error = null;
      }
      // -- Scripts


      // Handlers
      $scope.openNewHandlerDialog = function () {
        $scope.actionType = 'new';
        $scope.handler = {};
        $scope.handler.url = "";
        toggleHandlerModal();
      };

      $scope.openEditHandlerDialog = function (entity) {
        $scope.actionType = 'update';
        $scope.handler = entity;
        toggleHandlerModal();
      };

      $scope.openDeleteHandlerDialog = function (entity) {
        $scope.actionType = 'delete';
        $scope.handler = entity;
        toggleHandlerModal();
      };

      $scope.close = function () {
        load();
        toggleHandlerModal();
      };

      $scope.createHandler = function () {
        if (!$scope.metadata) {
          $scope.metadata = {};
        }
        if (!$scope.metadata.handlers) {
          $scope.metadata.handlers = [];
        }
        let exists = $scope.metadata.handlers.filter(function (e) {
          return e.name === $scope.handler.name;
        });
        if (exists.length === 0) {
          $scope.metadata.handlers.push($scope.handler);
          toggleHandlerModal();
        } else {
          $scope.error = "Handler with a name [" + $scope.handler.name + "] already exists!";
        }

      };

      $scope.updateHandler = function () {
        // auto-wired
        toggleHandlerModal();
      };

      $scope.deleteHandler = function () {
        $scope.metadata.handlers = $scope.metadata.handlers.filter(function (e) {
          return e !== $scope.handler;
        });
        toggleHandlerModal();
      };

      function toggleHandlerModal() {
        $('#handlerModal').modal('toggle');
        $scope.error = null;
      }
      // -- Handlers

      $scope.form = $builder.forms['default'];
      $scope.input = [];

      return $scope.submit = function (button) {
        return $validator.validate($scope, 'default').then(function () {
          return console.log('success');
        }, function () {
          return console.log('error');
        });
      };

    }
  ]);

}).call(this);
