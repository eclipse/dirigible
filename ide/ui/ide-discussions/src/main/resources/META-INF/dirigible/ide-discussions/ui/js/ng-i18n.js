/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * I18n module for AngularJs
 * @version v0.2.1 - 2013-03-26
 * @link http://gertn.github.com/ng-i18n/
 * @license MIT License, http://www.opensource.org/licenses/MIT
 */
'use strict';

angular.module('ngI18nConfig', []).value('ngI18nConfig', {});
angular.module('ngI18n', ['ngI18nService', 'ngI18nConfig'])
    .value('ngI18nVersion', '0.2.1');

angular.module('ngI18nService', [], ['$provide',function ($provide) {

    $provide.factory('ngI18nResourceBundle', ['$http', 'ngI18nConfig', '$window',
        function ($http, ngI18nConfig, $window) {
            ngI18nConfig.basePath = ngI18nConfig.basePath || 'i18n';
            ngI18nConfig.cache = ngI18nConfig.cache || false;
            ngI18nConfig.supportedLocales = ngI18nConfig.supportedLocales || [];

            function get(options) {
                var _options = options || {};
                var resourceBundleName = _options.name || 'resourceBundle';
                var url = ngI18nConfig.basePath + '/' + resourceBundleName + getLocaleOrLanguageFromLocaleSuffix(_options).toLowerCase() + '.json';
                return $http({ method:"GET", url:url, cache:ngI18nConfig.cache });
            }

            function getLocaleOrLanguageFromLocaleSuffix(options) {
                var locale = getLocale(options);
                var suffix = determineSuffixFrom(locale);
                if(angular.isUndefined(suffix)){
                    var language = getLanguageFromLocale(locale);
                    suffix = determineSuffixFrom(language);
                }
                return angular.isUndefined(suffix) ? '' : suffix;
            }

            function determineSuffixFrom(localeOrLanguage) {
                var suffix;
                if(isDefaultLocale(localeOrLanguage)){
                    suffix = '';
                }  else  if (isLocaleSupported(localeOrLanguage)) {
                    suffix = '_' + localeOrLanguage;
                }
                return suffix;
            }

            function isDefaultLocale(locale) {
                return locale.toLowerCase() === ngI18nConfig.defaultLocale;
            }

            function getLanguageFromLocale(locale) {
                return locale.split('-')[0];
            }

            function isLocaleSupported(locale) {
                return indexOf(ngI18nConfig.supportedLocales, locale.toLowerCase()) != -1;
            }

            function indexOf(array, obj) {
                if (array.indexOf) return array.indexOf(obj);

                for (var i = 0; i < array.length; i++) {
                    if (obj === array[i]) return i;
                }
                return -1;
            }

            function getLocale(options) {
                return options.locale || getLanguageFromNavigator();
            }

            function getLanguageFromNavigator() {
                return $window.navigator.userLanguage || $window.navigator.language;
            }

            return { get:get};
        }]);

}]).value('name', 'ngI18nService');
