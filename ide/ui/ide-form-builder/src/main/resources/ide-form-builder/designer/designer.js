/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
(function() {
  angular.module('app', ['builder', 'builder.components', 'validator.rules']).run([
    '$builder', function($builder) {
      $builder.registerComponent('sampleInput', {
        group: 'Complex',
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
    '$scope', '$builder', '$validator', function($scope, $builder, $validator) {

      var messageHub = new FramesMessageHub();
      var contents;
      
      function getResource(resourcePath) {
            var xhr = new XMLHttpRequest();
            xhr.open('GET', resourcePath, false);
            xhr.send();
            if (xhr.status === 200) {
              return xhr.responseText;
            }
      }
	
      function loadContents(file) {
        if (file) {
          return getResource('../../../../../../services/v4/ide/workspaces' + file);
        }
        console.error('file parameter is not present in the URL');
      }

      function load() {
        var searchParams = new URLSearchParams(window.location.search);
        $scope.file = searchParams.get('file');
        contents = loadContents($scope.file);
        $scope.components = JSON.parse(contents);
        $.each($scope.components, function(i, item){
            var formObj = $builder.addFormObject('default', item);
        });
      }
	
      load();

      function saveContents(text) {
        console.log('Save called...');
        if ($scope.file) {
          var xhr = new XMLHttpRequest();
          xhr.open('PUT', '../../../../../../services/v4/ide/workspaces' + $scope.file);
          xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
              console.log('file saved: ' + $scope.file);
            }
          };
          xhr.send(text);
          messageHub.post({data: $scope.file}, 'editor.file.saved');
        } else {
          console.error('file parameter is not present in the request');
        }
      }

      $scope.save = function() {
debugger
        contents = JSON.stringify($scope.form);
        saveContents(contents);
      };
	
      $scope.$watch(function() {
        var components = JSON.stringify($scope.components);
        if (contents !== components) {
          messageHub.post({data: $scope.file}, 'editor.file.dirty');
        }
      });

      // textbox = $builder.addFormObject('default', {
      //   id: 'textbox',
      //   component: 'textInput',
      //   label: 'Name',
      //   description: 'Your name',
      //   placeholder: 'Your name',
      //   required: true,
      //   editable: true
      // });

      $scope.form = $builder.forms['default'];
      $scope.input = [];
      $scope.defaultValue = {};
      return $scope.submit = function() {
        return $validator.validate($scope, 'default').success(function() {
          return console.log('success');
        }).error(function() {
          return console.log('error');
        });
      };
    }
  ]);

}).call(this);
