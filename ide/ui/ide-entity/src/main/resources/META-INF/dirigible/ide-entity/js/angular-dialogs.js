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
angular.module('ui.entity-data.modeler', ['ngAnimate', 'ngSanitize', 'ui.bootstrap'])
	.controller('ModelerCtrl', function ($scope) {
		let ctrl = this;
		ctrl.$scope = $scope;

		ctrl.animationsEnabled = true;

		ctrl.layoutTypes = [
			{ "key": "MANAGE", "label": "Manage Entities" },
			{ "key": "MANAGE_MASTER", "label": "Manage Master Entities" },
			{ "key": "MANAGE_DETAILS", "label": "Manage Details Entities" },
			{ "key": "LIST", "label": "List Entities" },
			{ "key": "LIST_MASTER", "label": "List Master Entities" },
			{ "key": "LIST_DETAILS", "label": "List Details Entities" },
			{ "key": "REPORT_TABLE", "label": "Report in a Table Format" },
			{ "key": "REPORT_BAR", "label": "Report in a Bar Chart Format" },
			{ "key": "REPORT_LINE", "label": "Report in a Line Chart Format" },
			{ "key": "REPORT_PIE", "label": "Report in a Pie Chart Format" }
		];

		ctrl.dataTypes = [
			{ "key": "VARCHAR", "label": "VARCHAR" },
			{ "key": "CHAR", "label": "CHAR" },
			{ "key": "DATE", "label": "DATE" },
			{ "key": "TIME", "label": "TIME" },
			{ "key": "TIMESTAMP", "label": "TIMESTAMP" },
			{ "key": "INTEGER", "label": "INTEGER" },
			{ "key": "TINYINT", "label": "TINYINT" },
			{ "key": "BIGINT", "label": "BIGINT" },
			{ "key": "SMALLINT", "label": "SMALLINT" },
			{ "key": "REAL", "label": "REAL" },
			{ "key": "DOUBLE", "label": "DOUBLE" },
			{ "key": "BOOLEAN", "label": "BOOLEAN" },
			{ "key": "BLOB", "label": "BLOB" },
			{ "key": "DECIMAL", "label": "DECIMAL" },
			{ "key": "BIT", "label": "BIT" }
		];

		ctrl.widgetTypes = [
			{ "key": "TEXTBOX", "label": "Text Box" },
			{ "key": "TEXTAREA", "label": "Text Area" },
			{ "key": "DATE", "label": "Date Picker" },
			{ "key": "DROPDOWN", "label": "Dropdown" },
			{ "key": "CHECKBOX", "label": "Check Box" },
			{ "key": "LOOKUPDIALOG", "label": "Lookup Dialog" },
			{ "key": "NUMBER", "label": "Number" },
			{ "key": "COLOR", "label": "Color" },
			{ "key": "DATETIME-LOCAL", "label": "Datetime Local" },
			{ "key": "EMAIL", "label": "e-mail" },
			{ "key": "MONTH", "label": "Month" },
			{ "key": "RANGE", "label": "Range" },
			{ "key": "SEARCH", "label": "Search" },
			{ "key": "TEL", "label": "Telephone" },
			{ "key": "TIME", "label": "Time" },
			{ "key": "URL", "label": "URL" },
			{ "key": "WEEK", "label": "Week" }
		];

		ctrl.relationshipTypes = [
			{ "key": "ASSOCIATION", "label": "Association" },
			{ "key": "AGGREGATION", "label": "Aggregation" },
			{ "key": "COMPOSITION", "label": "Composition" },
			{ "key": "EXTENSION", "label": "Extension" }
		];

		ctrl.relationshipCardinalities = [
			{ "key": "1_1", "label": "one-to-one" },
			{ "key": "1_n", "label": "one-to-many" },
			{ "key": "n_1", "label": "many-to-one" },
		];

		ctrl.entityTypes = [
			{ "key": "PRIMARY", "label": "Primary Entity" },
			{ "key": "DEPENDENT", "label": "Dependent Entity" },
			{ "key": "REPORT", "label": "Report Entity" }
		];

		ctrl.isMajorTypes = [
			{ "key": "true", "label": "Show in table header" },
			{ "key": "false", "label": "Show in form only" }
		];

		ctrl.icons = [];
		// [
		// 	{ "name": "align-left", "icon": "&#xf036; align-left" },
		// 	{ "name": "align-right", "icon": "&#xf038; align-right" },
		// 	{ "name": "amazon", "icon": "&#xf270; amazon" },
		// 	{ "name": "ambulance", "icon": "&#xf0f9; ambulance" },
		// 	{ "name": "anchor", "icon": "&#xf13d; anchor" },
		// 	{ "name": "android", "icon": "&#xf17b; android" },
		// 	{ "name": "angellist", "icon": "&#xf209; angellist" },
		// 	{ "name": "angle-double-down", "icon": "&#xf103; angle-double-down" },
		// 	{ "name": "angle-double-left", "icon": "&#xf100; angle-double-left" },
		// 	{ "name": "angle-double-right", "icon": "&#xf101; angle-double-right" },
		// 	{ "name": "angle-double-up", "icon": "&#xf102; angle-double-up" },
		// 	{ "name": "angle-left", "icon": "&#xf104; angle-left" },
		// 	{ "name": "angle-right", "icon": "&#xf105; angle-right" },
		// 	{ "name": "angle-up", "icon": "&#xf106; angle-up" },
		// 	{ "name": "apple", "icon": "&#xf179; apple" },
		// 	{ "name": "archive", "icon": "&#xf187; archive" },
		// 	{ "name": "area-chart", "icon": "&#xf1fe; area-chart" },
		// 	{ "name": "arrow-circle-down", "icon": "&#xf0ab; arrow-circle-down" },
		// 	{ "name": "arrow-circle-left", "icon": "&#xf0a8; arrow-circle-left" },
		// 	{ "name": "arrow-circle-o-down", "icon": "&#xf01a; arrow-circle-o-down" },
		// 	{ "name": "arrow-circle-o-left", "icon": "&#xf190; arrow-circle-o-left" },
		// 	{ "name": "arrow-circle-o-right", "icon": "&#xf18e; arrow-circle-o-right" },
		// 	{ "name": "arrow-circle-o-up", "icon": "&#xf01b; arrow-circle-o-up" },
		// 	{ "name": "arrow-circle-right", "icon": "&#xf0a9; arrow-circle-right" },
		// 	{ "name": "arrow-circle-up", "icon": "&#xf0aa; arrow-circle-up" },
		// 	{ "name": "arrow-down", "icon": "&#xf063; arrow-down" },
		// 	{ "name": "arrow-left", "icon": "&#xf060; arrow-left" },
		// 	{ "name": "arrow-right", "icon": "&#xf061; arrow-right" },
		// 	{ "name": "arrow-up", "icon": "&#xf062; arrow-up" },
		// 	{ "name": "arrows", "icon": "&#xf047; arrows" },
		// 	{ "name": "arrows-alt", "icon": "&#xf0b2; arrows-alt" },
		// 	{ "name": "arrows-h", "icon": "&#xf07e; arrows-h" },
		// 	{ "name": "arrows-v", "icon": "&#xf07d; arrows-v" },
		// 	{ "name": "asterisk", "icon": "&#xf069; asterisk" },
		// 	{ "name": "at", "icon": "&#xf1fa; at" },
		// 	{ "name": "automobile", "icon": "&#xf1b9; automobile" },
		// 	{ "name": "backward", "icon": "&#xf04a; backward" },
		// 	{ "name": "balance-scale", "icon": "&#xf24e; balance-scale" },
		// 	{ "name": "ban", "icon": "&#xf05e; ban" },
		// 	{ "name": "bank", "icon": "&#xf19c; bank" },
		// 	{ "name": "bar-chart", "icon": "&#xf080; bar-chart" },
		// 	{ "name": "bar-chart-o", "icon": "&#xf080; bar-chart-o" },
		// 	{ "name": "battery-full", "icon": "&#xf240; battery-full" },
		// 	{ "name": "beer", "icon": "&#xf0fc; beer" },
		// 	{ "name": "behance", "icon": "&#xf1b4; behance" },
		// 	{ "name": "behance-square", "icon": "&#xf1b5; behance-square" },
		// 	{ "name": "bell", "icon": "&#xf0f3; bell" },
		// 	{ "name": "bell-o", "icon": "&#xf0a2; bell-o" },
		// 	{ "name": "bell-slash", "icon": "&#xf1f6; bell-slash" },
		// 	{ "name": "bell-slash-o", "icon": "&#xf1f7; bell-slash-o" },
		// 	{ "name": "bicycle", "icon": "&#xf206; bicycle" },
		// 	{ "name": "binoculars", "icon": "&#xf1e5; binoculars" },
		// 	{ "name": "birthday-cake", "icon": "&#xf1fd; birthday-cake" },
		// 	{ "name": "bitbucket", "icon": "&#xf171; bitbucket" },
		// 	{ "name": "bitbucket-square", "icon": "&#xf172; bitbucket-square" },
		// 	{ "name": "bitcoin", "icon": "&#xf15a; bitcoin" },
		// 	{ "name": "black-tie", "icon": "&#xf27e; black-tie" },
		// 	{ "name": "bold", "icon": "&#xf032; bold" },
		// 	{ "name": "bolt", "icon": "&#xf0e7; bolt" },
		// 	{ "name": "bomb", "icon": "&#xf1e2; bomb" },
		// 	{ "name": "book", "icon": "&#xf02d; book" },
		// 	{ "name": "bookmark", "icon": "&#xf02e; bookmark" },
		// 	{ "name": "bookmark-o", "icon": "&#xf097; bookmark-o" },
		// 	{ "name": "briefcase", "icon": "&#xf0b1; briefcase" },
		// 	{ "name": "btc", "icon": "&#xf15a; btc" },
		// 	{ "name": "bug", "icon": "&#xf188; bug" },
		// 	{ "name": "building", "icon": "&#xf1ad; building" },
		// 	{ "name": "building-o", "icon": "&#xf0f7; building-o" },
		// 	{ "name": "bullhorn", "icon": "&#xf0a1; bullhorn" },
		// 	{ "name": "bullseye", "icon": "&#xf140; bullseye" },
		// 	{ "name": "bus", "icon": "&#xf207; bus" },
		// 	{ "name": "cab", "icon": "&#xf1ba; cab" },
		// 	{ "name": "calendar", "icon": "&#xf073; calendar" },
		// 	{ "name": "camera", "icon": "&#xf030; camera" },
		// 	{ "name": "car", "icon": "&#xf1b9; car" },
		// 	{ "name": "caret-up", "icon": "&#xf0d8; caret-up" },
		// 	{ "name": "cart-plus", "icon": "&#xf217; cart-plus" },
		// 	{ "name": "cc", "icon": "&#xf20a; cc" },
		// 	{ "name": "cc-amex", "icon": "&#xf1f3; cc-amex" },
		// 	{ "name": "cc-jcb", "icon": "&#xf24b; cc-jcb" },
		// 	{ "name": "cc-paypal", "icon": "&#xf1f4; cc-paypal" },
		// 	{ "name": "cc-stripe", "icon": "&#xf1f5; cc-stripe" },
		// 	{ "name": "cc-visa", "icon": "&#xf1f0; cc-visa" },
		// 	{ "name": "chain", "icon": "&#xf0c1; chain" },
		// 	{ "name": "check", "icon": "&#xf00c; check" },
		// 	{ "name": "chevron-left", "icon": "&#xf053; chevron-left" },
		// 	{ "name": "chevron-right", "icon": "&#xf054; chevron-right" },
		// 	{ "name": "chevron-up", "icon": "&#xf077; chevron-up" },
		// 	{ "name": "child", "icon": "&#xf1ae; child" },
		// 	{ "name": "chrome", "icon": "&#xf268; chrome" },
		// 	{ "name": "circle", "icon": "&#xf111; circle" },
		// 	{ "name": "circle-o", "icon": "&#xf10c; circle-o" },
		// 	{ "name": "circle-o-notch", "icon": "&#xf1ce; circle-o-notch" },
		// 	{ "name": "circle-thin", "icon": "&#xf1db; circle-thin" },
		// 	{ "name": "clipboard", "icon": "&#xf0ea; clipboard" },
		// 	{ "name": "clock-o", "icon": "&#xf017; clock-o" },
		// 	{ "name": "clone", "icon": "&#xf24d; clone" },
		// 	{ "name": "close", "icon": "&#xf00d; close" },
		// 	{ "name": "cloud", "icon": "&#xf0c2; cloud" },
		// 	{ "name": "cloud-download", "icon": "&#xf0ed; cloud-download" },
		// 	{ "name": "cloud-upload", "icon": "&#xf0ee; cloud-upload" },
		// 	{ "name": "cny", "icon": "&#xf157; cny" },
		// 	{ "name": "code", "icon": "&#xf121; code" },
		// 	{ "name": "code-fork", "icon": "&#xf126; code-fork" },
		// 	{ "name": "codepen", "icon": "&#xf1cb; codepen" },
		// 	{ "name": "coffee", "icon": "&#xf0f4; coffee" },
		// 	{ "name": "cog", "icon": "&#xf013; cog" },
		// 	{ "name": "cogs", "icon": "&#xf085; cogs" },
		// 	{ "name": "columns", "icon": "&#xf0db; columns" },
		// 	{ "name": "comment", "icon": "&#xf075; comment" },
		// 	{ "name": "comment-o", "icon": "&#xf0e5; comment-o" },
		// 	{ "name": "commenting", "icon": "&#xf27a; commenting" },
		// 	{ "name": "commenting-o", "icon": "&#xf27b; commenting-o" },
		// 	{ "name": "comments", "icon": "&#xf086; comments" },
		// 	{ "name": "comments-o", "icon": "&#xf0e6; comments-o" },
		// 	{ "name": "compass", "icon": "&#xf14e; compass" },
		// 	{ "name": "compress", "icon": "&#xf066; compress" },
		// 	{ "name": "connectdevelop", "icon": "&#xf20e; connectdevelop" },
		// 	{ "name": "contao", "icon": "&#xf26d; contao" },
		// 	{ "name": "copy", "icon": "&#xf0c5; copy" },
		// 	{ "name": "copyright", "icon": "&#xf1f9; copyright" },
		// 	{ "name": "creative-commons", "icon": "&#xf25e; creative-commons" },
		// 	{ "name": "credit-card", "icon": "&#xf09d; credit-card" },
		// 	{ "name": "crop", "icon": "&#xf125; crop" },
		// 	{ "name": "crosshairs", "icon": "&#xf05b; crosshairs" },
		// 	{ "name": "css3", "icon": "&#xf13c; css3" },
		// 	{ "name": "cube", "icon": "&#xf1b2; cube" },
		// 	{ "name": "cubes", "icon": "&#xf1b3; cubes" },
		// 	{ "name": "cut", "icon": "&#xf0c4; cut" },
		// 	{ "name": "cutlery", "icon": "&#xf0f5; cutlery" },
		// 	{ "name": "dashboard", "icon": "&#xf0e4; dashboard" },
		// 	{ "name": "dashcube", "icon": "&#xf210; dashcube" },
		// 	{ "name": "database", "icon": "&#xf1c0; database" },
		// 	{ "name": "dedent", "icon": "&#xf03b; dedent" },
		// 	{ "name": "delicious", "icon": "&#xf1a5; delicious" },
		// 	{ "name": "desktop", "icon": "&#xf108; desktop" },
		// 	{ "name": "deviantart", "icon": "&#xf1bd; deviantart" },
		// 	{ "name": "diamond", "icon": "&#xf219; diamond" },
		// 	{ "name": "digg", "icon": "&#xf1a6; digg" },
		// 	{ "name": "dollar", "icon": "&#xf155; dollar" },
		// 	{ "name": "download", "icon": "&#xf019; download" },
		// 	{ "name": "dribbble", "icon": "&#xf17d; dribbble" },
		// 	{ "name": "dropbox", "icon": "&#xf16b; dropbox" },
		// 	{ "name": "drupal", "icon": "&#xf1a9; drupal" },
		// 	{ "name": "edit", "icon": "&#xf044; edit" },
		// 	{ "name": "eject", "icon": "&#xf052; eject" },
		// 	{ "name": "ellipsis-h", "icon": "&#xf141; ellipsis-h" },
		// 	{ "name": "ellipsis-v", "icon": "&#xf142; ellipsis-v" },
		// 	{ "name": "empire", "icon": "&#xf1d1; empire" },
		// 	{ "name": "envelope", "icon": "&#xf0e0; envelope" },
		// 	{ "name": "envelope-o", "icon": "&#xf003; envelope-o" },
		// 	{ "name": "eur", "icon": "&#xf153; eur" },
		// 	{ "name": "euro", "icon": "&#xf153; euro" },
		// 	{ "name": "exchange", "icon": "&#xf0ec; exchange" },
		// 	{ "name": "exclamation", "icon": "&#xf12a; exclamation" },
		// 	{ "name": "exclamation-circle", "icon": "&#xf06a; exclamation-circle" },
		// 	{ "name": "exclamation-triangle", "icon": "&#xf071; exclamation-triangle" },
		// 	{ "name": "expand", "icon": "&#xf065; expand" },
		// 	{ "name": "expeditedssl", "icon": "&#xf23e; expeditedssl" },
		// 	{ "name": "external-link", "icon": "&#xf08e; external-link" },
		// 	{ "name": "external-link-square", "icon": "&#xf14c; external-link-square" },
		// 	{ "name": "eye", "icon": "&#xf06e; eye" },
		// 	{ "name": "eye-slash", "icon": "&#xf070; eye-slash" },
		// 	{ "name": "eyedropper", "icon": "&#xf1fb; eyedropper" },
		// 	{ "name": "facebook", "icon": "&#xf09a; facebook" },
		// 	{ "name": "facebook-f", "icon": "&#xf09a; facebook-f" },
		// 	{ "name": "facebook-official", "icon": "&#xf230; facebook-official" },
		// 	{ "name": "facebook-square", "icon": "&#xf082; facebook-square" },
		// 	{ "name": "fast-backward", "icon": "&#xf049; fast-backward" },
		// 	{ "name": "fast-forward", "icon": "&#xf050; fast-forward" },
		// 	{ "name": "fax", "icon": "&#xf1ac; fax" },
		// 	{ "name": "feed", "icon": "&#xf09e; feed" },
		// 	{ "name": "female", "icon": "&#xf182; female" },
		// 	{ "name": "fighter-jet", "icon": "&#xf0fb; fighter-jet" },
		// 	{ "name": "file", "icon": "&#xf15b; file" },
		// 	{ "name": "file-archive-o", "icon": "&#xf1c6; file-archive-o" },
		// 	{ "name": "file-audio-o", "icon": "&#xf1c7; file-audio-o" },
		// 	{ "name": "file-code-o", "icon": "&#xf1c9; file-code-o" },
		// 	{ "name": "file-excel-o", "icon": "&#xf1c3; file-excel-o" },
		// 	{ "name": "file-image-o", "icon": "&#xf1c5; file-image-o" },
		// 	{ "name": "file-movie-o", "icon": "&#xf1c8; file-movie-o" },
		// 	{ "name": "file-o", "icon": "&#xf016; file-o" },
		// 	{ "name": "file-pdf-o", "icon": "&#xf1c1; file-pdf-o" },
		// 	{ "name": "file-photo-o", "icon": "&#xf1c5; file-photo-o" },
		// 	{ "name": "file-picture-o", "icon": "&#xf1c5; file-picture-o" },
		// 	{ "name": "file-powerpoint-o", "icon": "&#xf1c4; file-powerpoint-o" },
		// 	{ "name": "file-sound-o", "icon": "&#xf1c7; file-sound-o" },
		// 	{ "name": "file-text", "icon": "&#xf15c; file-text" },
		// 	{ "name": "file-text-o", "icon": "&#xf0f6; file-text-o" },
		// 	{ "name": "file-video-o", "icon": "&#xf1c8; file-video-o" },
		// 	{ "name": "file-word-o", "icon": "&#xf1c2; file-word-o" },
		// 	{ "name": "file-zip-o", "icon": "&#xf1c6; file-zip-o" },
		// 	{ "name": "files-o", "icon": "&#xf0c5; files-o" },
		// 	{ "name": "film", "icon": "&#xf008; film" },
		// 	{ "name": "filter", "icon": "&#xf0b0; filter" },
		// 	{ "name": "fire", "icon": "&#xf06d; fire" },
		// 	{ "name": "fire-extinguisher", "icon": "&#xf134; fire-extinguisher" },
		// 	{ "name": "firefox", "icon": "&#xf269; firefox" },
		// 	{ "name": "flag", "icon": "&#xf024; flag" },
		// 	{ "name": "flag-checkered", "icon": "&#xf11e; flag-checkered" },
		// 	{ "name": "flag-o", "icon": "&#xf11d; flag-o" },
		// 	{ "name": "flash", "icon": "&#xf0e7; flash" },
		// 	{ "name": "flask", "icon": "&#xf0c3; flask" },
		// 	{ "name": "flickr", "icon": "&#xf16e; flickr" },
		// 	{ "name": "floppy-o", "icon": "&#xf0c7; floppy-o" },
		// 	{ "name": "folder", "icon": "&#xf07b; folder" },
		// 	{ "name": "folder-o", "icon": "&#xf114; folder-o" },
		// 	{ "name": "folder-open", "icon": "&#xf07c; folder-open" },
		// 	{ "name": "folder-open-o", "icon": "&#xf115; folder-open-o" },
		// 	{ "name": "font", "icon": "&#xf031; font" },
		// 	{ "name": "fonticons", "icon": "&#xf280; fonticons" },
		// 	{ "name": "forumbee", "icon": "&#xf211; forumbee" },
		// 	{ "name": "forward", "icon": "&#xf04e; forward" },
		// 	{ "name": "foursquare", "icon": "&#xf180; foursquare" },
		// 	{ "name": "frown-o", "icon": "&#xf119; frown-o" },
		// 	{ "name": "futbol-o", "icon": "&#xf1e3; futbol-o" },
		// 	{ "name": "gamepad", "icon": "&#xf11b; gamepad" },
		// 	{ "name": "gavel", "icon": "&#xf0e3; gavel" },
		// 	{ "name": "gbp", "icon": "&#xf154; gbp" },
		// 	{ "name": "ge", "icon": "&#xf1d1; ge" },
		// 	{ "name": "gear", "icon": "&#xf013; gear" },
		// 	{ "name": "gears", "icon": "&#xf085; gears" },
		// 	{ "name": "genderless", "icon": "&#xf22d; genderless" },
		// 	{ "name": "get-pocket", "icon": "&#xf265; get-pocket" },
		// 	{ "name": "gg", "icon": "&#xf260; gg" },
		// 	{ "name": "gg-circle", "icon": "&#xf261; gg-circle" },
		// 	{ "name": "gift", "icon": "&#xf06b; gift" },
		// 	{ "name": "git", "icon": "&#xf1d3; git" },
		// 	{ "name": "git-square", "icon": "&#xf1d2; git-square" },
		// 	{ "name": "github", "icon": "&#xf09b; github" },
		// 	{ "name": "github-alt", "icon": "&#xf113; github-alt" },
		// 	{ "name": "github-square", "icon": "&#xf092; github-square" },
		// 	{ "name": "gittip", "icon": "&#xf184; gittip" },
		// 	{ "name": "glass", "icon": "&#xf000; glass" },
		// 	{ "name": "globe", "icon": "&#xf0ac; globe" },
		// 	{ "name": "google", "icon": "&#xf1a0; google" },
		// 	{ "name": "google-plus", "icon": "&#xf0d5; google-plus" },
		// 	{ "name": "google-plus-square", "icon": "&#xf0d4; google-plus-square" },
		// 	{ "name": "google-wallet", "icon": "&#xf1ee; google-wallet" },
		// 	{ "name": "graduation-cap", "icon": "&#xf19d; graduation-cap" },
		// 	{ "name": "gratipay", "icon": "&#xf184; gratipay" },
		// 	{ "name": "group", "icon": "&#xf0c0; group" },
		// 	{ "name": "h-square", "icon": "&#xf0fd; h-square" },
		// 	{ "name": "hacker-news", "icon": "&#xf1d4; hacker-news" },
		// 	{ "name": "hand-grab-o", "icon": "&#xf255; hand-grab-o" },
		// 	{ "name": "hand-lizard-o", "icon": "&#xf258; hand-lizard-o" },
		// 	{ "name": "hand-o-down", "icon": "&#xf0a7; hand-o-down" },
		// 	{ "name": "hand-o-left", "icon": "&#xf0a5; hand-o-left" },
		// 	{ "name": "hand-o-right", "icon": "&#xf0a4; hand-o-right" },
		// 	{ "name": "hand-o-up", "icon": "&#xf0a6; hand-o-up" },
		// 	{ "name": "hand-paper-o", "icon": "&#xf256; hand-paper-o" },
		// 	{ "name": "hand-peace-o", "icon": "&#xf25b; hand-peace-o" },
		// 	{ "name": "hand-pointer-o", "icon": "&#xf25a; hand-pointer-o" },
		// 	{ "name": "hand-rock-o", "icon": "&#xf255; hand-rock-o" },
		// 	{ "name": "hand-scissors-o", "icon": "&#xf257; hand-scissors-o" },
		// 	{ "name": "hand-spock-o", "icon": "&#xf259; hand-spock-o" },
		// 	{ "name": "hand-stop-o", "icon": "&#xf256; hand-stop-o" },
		// 	{ "name": "hdd-o", "icon": "&#xf0a0; hdd-o" },
		// 	{ "name": "header", "icon": "&#xf1dc; header" },
		// 	{ "name": "headphones", "icon": "&#xf025; headphones" },
		// 	{ "name": "heart", "icon": "&#xf004; heart" },
		// 	{ "name": "heart-o", "icon": "&#xf08a; heart-o" },
		// 	{ "name": "heartbeat", "icon": "&#xf21e; heartbeat" },
		// 	{ "name": "history", "icon": "&#xf1da; history" },
		// 	{ "name": "home", "icon": "&#xf015; home" },
		// 	{ "name": "hospital-o", "icon": "&#xf0f8; hospital-o" },
		// 	{ "name": "hotel", "icon": "&#xf236; hotel" },
		// 	{ "name": "hourglass", "icon": "&#xf254; hourglass" },
		// 	{ "name": "hourglass-1", "icon": "&#xf251; hourglass-1" },
		// 	{ "name": "hourglass-2", "icon": "&#xf252; hourglass-2" },
		// 	{ "name": "hourglass-3", "icon": "&#xf253; hourglass-3" },
		// 	{ "name": "hourglass-end", "icon": "&#xf253; hourglass-end" },
		// 	{ "name": "hourglass-half", "icon": "&#xf252; hourglass-half" },
		// 	{ "name": "hourglass-o", "icon": "&#xf250; hourglass-o" },
		// 	{ "name": "hourglass-start", "icon": "&#xf251; hourglass-start" },
		// 	{ "name": "houzz", "icon": "&#xf27c; houzz" },
		// 	{ "name": "html5", "icon": "&#xf13b; html5" },
		// 	{ "name": "i-cursor", "icon": "&#xf246; i-cursor" },
		// 	{ "name": "ils", "icon": "&#xf20b; ils" },
		// 	{ "name": "image", "icon": "&#xf03e; image" },
		// 	{ "name": "inbox", "icon": "&#xf01c; inbox" },
		// 	{ "name": "indent", "icon": "&#xf03c; indent" },
		// 	{ "name": "industry", "icon": "&#xf275; industry" },
		// 	{ "name": "info", "icon": "&#xf129; info" },
		// 	{ "name": "info-circle", "icon": "&#xf05a; info-circle" },
		// 	{ "name": "inr", "icon": "&#xf156; inr" },
		// 	{ "name": "instagram", "icon": "&#xf16d; instagram" },
		// 	{ "name": "institution", "icon": "&#xf19c; institution" },
		// 	{ "name": "internet-explorer", "icon": "&#xf26b; internet-explorer" },
		// 	{ "name": "intersex", "icon": "&#xf224; intersex" },
		// 	{ "name": "ioxhost", "icon": "&#xf208; ioxhost" },
		// 	{ "name": "italic", "icon": "&#xf033; italic" },
		// 	{ "name": "joomla", "icon": "&#xf1aa; joomla" },
		// 	{ "name": "jpy", "icon": "&#xf157; jpy" },
		// 	{ "name": "jsfiddle", "icon": "&#xf1cc; jsfiddle" },
		// 	{ "name": "key", "icon": "&#xf084; key" },
		// 	{ "name": "keyboard-o", "icon": "&#xf11c; keyboard-o" },
		// 	{ "name": "krw", "icon": "&#xf159; krw" },
		// 	{ "name": "language", "icon": "&#xf1ab; language" },
		// 	{ "name": "laptop", "icon": "&#xf109; laptop" },
		// 	{ "name": "lastfm", "icon": "&#xf202; lastfm" },
		// 	{ "name": "lastfm-square", "icon": "&#xf203; lastfm-square" },
		// 	{ "name": "leaf", "icon": "&#xf06c; leaf" },
		// 	{ "name": "leanpub", "icon": "&#xf212; leanpub" },
		// 	{ "name": "legal", "icon": "&#xf0e3; legal" },
		// 	{ "name": "lemon-o", "icon": "&#xf094; lemon-o" },
		// 	{ "name": "level-down", "icon": "&#xf149; level-down" },
		// 	{ "name": "level-up", "icon": "&#xf148; level-up" },
		// 	{ "name": "life-bouy", "icon": "&#xf1cd; life-bouy" },
		// 	{ "name": "life-buoy", "icon": "&#xf1cd; life-buoy" },
		// 	{ "name": "life-ring", "icon": "&#xf1cd; life-ring" },
		// 	{ "name": "life-saver", "icon": "&#xf1cd; life-saver" },
		// 	{ "name": "lightbulb-o", "icon": "&#xf0eb; lightbulb-o" },
		// 	{ "name": "line-chart", "icon": "&#xf201; line-chart" },
		// 	{ "name": "link", "icon": "&#xf0c1; link" },
		// 	{ "name": "linkedin", "icon": "&#xf0e1; linkedin" },
		// 	{ "name": "linkedin-square", "icon": "&#xf08c; linkedin-square" },
		// 	{ "name": "linux", "icon": "&#xf17c; linux" },
		// 	{ "name": "list", "icon": "&#xf03a; list" },
		// 	{ "name": "list-alt", "icon": "&#xf022; list-alt" },
		// 	{ "name": "list-ol", "icon": "&#xf0cb; list-ol" },
		// 	{ "name": "list-ul", "icon": "&#xf0ca; list-ul" },
		// 	{ "name": "location-arrow", "icon": "&#xf124; location-arrow" },
		// 	{ "name": "lock", "icon": "&#xf023; lock" },
		// 	{ "name": "long-arrow-down", "icon": "&#xf175; long-arrow-down" },
		// 	{ "name": "long-arrow-left", "icon": "&#xf177; long-arrow-left" },
		// 	{ "name": "long-arrow-right", "icon": "&#xf178; long-arrow-right" },
		// 	{ "name": "long-arrow-up", "icon": "&#xf176; long-arrow-up" },
		// 	{ "name": "magic", "icon": "&#xf0d0; magic" },
		// 	{ "name": "magnet", "icon": "&#xf076; magnet" },
		// 	{ "name": "mars-stroke-v", "icon": "&#xf22a; mars-stroke-v" },
		// 	{ "name": "maxcdn", "icon": "&#xf136; maxcdn" },
		// 	{ "name": "meanpath", "icon": "&#xf20c; meanpath" },
		// 	{ "name": "medium", "icon": "&#xf23a; medium" },
		// 	{ "name": "medkit", "icon": "&#xf0fa; medkit" },
		// 	{ "name": "meh-o", "icon": "&#xf11a; meh-o" },
		// 	{ "name": "mercury", "icon": "&#xf223; mercury" },
		// 	{ "name": "microphone", "icon": "&#xf130; microphone" },
		// 	{ "name": "mobile", "icon": "&#xf10b; mobile" },
		// 	{ "name": "motorcycle", "icon": "&#xf21c; motorcycle" },
		// 	{ "name": "mouse-pointer", "icon": "&#xf245; mouse-pointer" },
		// 	{ "name": "music", "icon": "&#xf001; music" },
		// 	{ "name": "navicon", "icon": "&#xf0c9; navicon" },
		// 	{ "name": "neuter", "icon": "&#xf22c; neuter" },
		// 	{ "name": "newspaper-o", "icon": "&#xf1ea; newspaper-o" },
		// 	{ "name": "opencart", "icon": "&#xf23d; opencart" },
		// 	{ "name": "openid", "icon": "&#xf19b; openid" },
		// 	{ "name": "opera", "icon": "&#xf26a; opera" },
		// 	{ "name": "outdent", "icon": "&#xf03b; outdent" },
		// 	{ "name": "pagelines", "icon": "&#xf18c; pagelines" },
		// 	{ "name": "paper-plane-o", "icon": "&#xf1d9; paper-plane-o" },
		// 	{ "name": "paperclip", "icon": "&#xf0c6; paperclip" },
		// 	{ "name": "paragraph", "icon": "&#xf1dd; paragraph" },
		// 	{ "name": "paste", "icon": "&#xf0ea; paste" },
		// 	{ "name": "pause", "icon": "&#xf04c; pause" },
		// 	{ "name": "paw", "icon": "&#xf1b0; paw" },
		// 	{ "name": "paypal", "icon": "&#xf1ed; paypal" },
		// 	{ "name": "pencil", "icon": "&#xf040; pencil" },
		// 	{ "name": "pencil-square-o", "icon": "&#xf044; pencil-square-o" },
		// 	{ "name": "phone", "icon": "&#xf095; phone" },
		// 	{ "name": "photo", "icon": "&#xf03e; photo" },
		// 	{ "name": "picture-o", "icon": "&#xf03e; picture-o" },
		// 	{ "name": "pie-chart", "icon": "&#xf200; pie-chart" },
		// 	{ "name": "pied-piper", "icon": "&#xf1a7; pied-piper" },
		// 	{ "name": "pied-piper-alt", "icon": "&#xf1a8; pied-piper-alt" },
		// 	{ "name": "pinterest", "icon": "&#xf0d2; pinterest" },
		// 	{ "name": "pinterest-p", "icon": "&#xf231; pinterest-p" },
		// 	{ "name": "pinterest-square", "icon": "&#xf0d3; pinterest-square" },
		// 	{ "name": "plane", "icon": "&#xf072; plane" },
		// 	{ "name": "play", "icon": "&#xf04b; play" },
		// 	{ "name": "play-circle", "icon": "&#xf144; play-circle" },
		// 	{ "name": "play-circle-o", "icon": "&#xf01d; play-circle-o" },
		// 	{ "name": "plug", "icon": "&#xf1e6; plug" },
		// 	{ "name": "plus", "icon": "&#xf067; plus" },
		// 	{ "name": "plus-circle", "icon": "&#xf055; plus-circle" },
		// 	{ "name": "plus-square", "icon": "&#xf0fe; plus-square" },
		// 	{ "name": "plus-square-o", "icon": "&#xf196; plus-square-o" },
		// 	{ "name": "power-off", "icon": "&#xf011; power-off" },
		// 	{ "name": "print", "icon": "&#xf02f; print" },
		// 	{ "name": "puzzle-piece", "icon": "&#xf12e; puzzle-piece" },
		// 	{ "name": "qq", "icon": "&#xf1d6; qq" },
		// 	{ "name": "qrcode", "icon": "&#xf029; qrcode" },
		// 	{ "name": "question", "icon": "&#xf128; question" },
		// 	{ "name": "question-circle", "icon": "&#xf059; question-circle" },
		// 	{ "name": "quote-left", "icon": "&#xf10d; quote-left" },
		// 	{ "name": "quote-right", "icon": "&#xf10e; quote-right" },
		// 	{ "name": "ra", "icon": "&#xf1d0; ra" },
		// 	{ "name": "random", "icon": "&#xf074; random" },
		// 	{ "name": "rebel", "icon": "&#xf1d0; rebel" },
		// 	{ "name": "recycle", "icon": "&#xf1b8; recycle" },
		// 	{ "name": "reddit", "icon": "&#xf1a1; reddit" },
		// 	{ "name": "reddit-square", "icon": "&#xf1a2; reddit-square" },
		// 	{ "name": "refresh", "icon": "&#xf021; refresh" },
		// 	{ "name": "registered", "icon": "&#xf25d; registered" },
		// 	{ "name": "remove", "icon": "&#xf00d; remove" },
		// 	{ "name": "renren", "icon": "&#xf18b; renren" },
		// 	{ "name": "reorder", "icon": "&#xf0c9; reorder" },
		// 	{ "name": "repeat", "icon": "&#xf01e; repeat" },
		// 	{ "name": "reply", "icon": "&#xf112; reply" },
		// 	{ "name": "reply-all", "icon": "&#xf122; reply-all" },
		// 	{ "name": "retweet", "icon": "&#xf079; retweet" },
		// 	{ "name": "rmb", "icon": "&#xf157; rmb" },
		// 	{ "name": "road", "icon": "&#xf018; road" },
		// 	{ "name": "rocket", "icon": "&#xf135; rocket" },
		// 	{ "name": "rotate-left", "icon": "&#xf0e2; rotate-left" },
		// 	{ "name": "rotate-right", "icon": "&#xf01e; rotate-right" },
		// 	{ "name": "rouble", "icon": "&#xf158; rouble" },
		// 	{ "name": "rss", "icon": "&#xf09e; rss" },
		// 	{ "name": "rss-square", "icon": "&#xf143; rss-square" },
		// 	{ "name": "rub", "icon": "&#xf158; rub" },
		// 	{ "name": "ruble", "icon": "&#xf158; ruble" },
		// 	{ "name": "rupee", "icon": "&#xf156; rupee" },
		// 	{ "name": "safari", "icon": "&#xf267; safari" },
		// 	{ "name": "sliders", "icon": "&#xf1de; sliders" },
		// 	{ "name": "slideshare", "icon": "&#xf1e7; slideshare" },
		// 	{ "name": "smile-o", "icon": "&#xf118; smile-o" },
		// 	{ "name": "sort-asc", "icon": "&#xf0de; sort-asc" },
		// 	{ "name": "sort-desc", "icon": "&#xf0dd; sort-desc" },
		// 	{ "name": "sort-down", "icon": "&#xf0dd; sort-down" },
		// 	{ "name": "spinner", "icon": "&#xf110; spinner" },
		// 	{ "name": "spoon", "icon": "&#xf1b1; spoon" },
		// 	{ "name": "spotify", "icon": "&#xf1bc; spotify" },
		// 	{ "name": "square", "icon": "&#xf0c8; square" },
		// 	{ "name": "square-o", "icon": "&#xf096; square-o" },
		// 	{ "name": "star", "icon": "&#xf005; star" },
		// 	{ "name": "star-half", "icon": "&#xf089; star-half" },
		// 	{ "name": "stop", "icon": "&#xf04d; stop" },
		// 	{ "name": "subscript", "icon": "&#xf12c; subscript" },
		// 	{ "name": "tablet", "icon": "&#xf10a; tablet" },
		// 	{ "name": "tachometer", "icon": "&#xf0e4; tachometer" },
		// 	{ "name": "tag", "icon": "&#xf02b; tag" },
		// 	{ "name": "tags", "icon": "&#xf02c; tags" },
		// 	{ "name": "tasks", "icon": "&#xf0ae; tasks" },
		// 	{ "name": "television", "icon": "&#xf26c; television" },
		// 	{ "name": "terminal", "icon": "&#xf120; terminal" },
		// 	{ "name": "th", "icon": "&#xf00a; th" },
		// 	{ "name": "th-large", "icon": "&#xf009; th-large" },
		// 	{ "name": "th-list", "icon": "&#xf00b; th-list" },
		// 	{ "name": "thermometer", "icon": "&#xf2c7; thermometer" },
		// 	{ "name": "toggle-on", "icon": "&#xf205; toggle-on" },
		// 	{ "name": "train", "icon": "&#xf238; train" },
		// 	{ "name": "trophy", "icon": "&#xf091; trophy" },
		// 	{ "name": "truck", "icon": "&#xf0d1; truck" },
		// 	{ "name": "umbrella", "icon": "&#xf0e9; umbrella" },
		// 	{ "name": "user", "icon": "&#xf007; user" },
		// 	{ "name": "users", "icon": "&#xf0c0; users" },
		// 	{ "name": "video-camera", "icon": "&#xf03d; video-camera" },
		// 	{ "name": "wrench", "icon": "&#xf0ad; wrench" }
		// ];

		ctrl.loadIcons = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('GET', '/services/v4/web/resources/unicons/list.json');
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send();
			});
		};

		ctrl.loadModels = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('POST', '/services/v4/ide/workspace-find/');
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send('*.model');
			});
		};

		ctrl.loadEntities = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('GET', '/services/v4/ide/workspaces' + $scope.$parent.referencedModel);
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send();
			});
		};

		ctrl.loadIcons().then(
			result => ctrl.icons = JSON.parse(result),
			error => console.log(error)
		);

		ctrl.loadModels().then(
			result => ctrl.availableModels = JSON.parse(result),
			error => console.log(error)
		);

		ctrl.updateEntities = function () {
			ctrl.loadEntities().then(
				result => ctrl.availableEntities = $scope.$parent.availableEntities = JSON.parse(result).model.entities,
				error => console.log(error)
			);
		}

		// Save Entity's properties
		ctrl.okEntityProperties = function () {
			let clone = $scope.$parent.cell.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
			if (clone.entityType === 'PROJECTION') {
				$scope.$parent.graph.getSelectionCell().style = 'projection';
				$scope.$parent.graph.getSelectionCell().children.forEach(cell => cell.style = 'projectionproperty');
				$scope.$parent.graph.refresh();
			}
			if (clone.entityType === 'EXTENSION') {
				$scope.$parent.graph.getSelectionCell().style = 'extension';
				$scope.$parent.graph.getSelectionCell().children.forEach(cell => cell.style = 'extensionproperty');
				$scope.$parent.graph.refresh();
			}
		};

		// Save Property's properties
		ctrl.okPropertyProperties = function () {
			let clone = $scope.$parent.cell.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
		};

		// Save Connector's properties
		ctrl.okConnectorProperties = function () {
			let clone = $scope.$parent.cell.source.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell.source, clone);

			let connector = new Connector();
			connector.name = $scope.$parent.cell.source.value.relationshipName;
			$scope.$parent.graph.model.setValue($scope.$parent.cell, connector);
		};

		// Save Navigation's properties
		ctrl.okNavigationProperties = function () {
			// var clone = $scope.$parent.sidebar;
			// $scope.$parent.graph.model.sidebar = clone;

			// var sidebarNavigation = new SidebarNavigation();
			// connector.name = $scope.$parent.cell.source.value.relationshipName;
			// $scope.$parent.graph.model.setValue($scope.$parent.cell, connector);
		};

		ctrl.availablePerspectives = function () {
			return $scope.$parent.graph.model.perspectives;
		};

		// Perspectives Management
		$scope.openPerspectiveNewDialog = function () {
			$scope.actionType = 'perspectiveNew';
			$scope.perspectiveEntity = {};
			togglePerspectiveEntityModal();
		};

		$scope.openPerspectiveEditDialog = function (entity) {
			$scope.actionType = 'perspectiveUpdate';
			$scope.perspectiveEntity = entity;
			togglePerspectiveEntityModal();
		};

		$scope.openPerspectiveDeleteDialog = function (entity) {
			$scope.actionType = 'perspectiveDelete';
			$scope.perspectiveEntity = entity;
			togglePerspectiveEntityModal();
		};

		$scope.closePerspective = function () {
			//load();
			togglePerspectiveEntityModal();
		};

		$scope.perspectiveCreate = function () {
			if (!$scope.$parent.graph.model.perspectives) {
				$scope.$parent.graph.model.perspectives = [];
			}
			let exists = $scope.$parent.graph.model.perspectives.filter(function (e) {
				return e.id === $scope.perspectiveEntity.id;
			});
			if (exists.length === 0) {
				$scope.$parent.graph.model.perspectives.push($scope.perspectiveEntity);
				togglePerspectiveEntityModal();
			} else {
				$scope.error = "Perspective with the id [" + $scope.perspectiveEntity.id + "] already exists!";
			}

		};

		$scope.perspectiveUpdate = function () {
			// auto-wired
			togglePerspectiveEntityModal();
		};

		$scope.perspectiveDelete = function () {
			if (!$scope.$parent.graph.model.perspectives) {
				$scope.$parent.graph.model.perspectives = [];
			}
			$scope.$parent.graph.model.perspectives = $scope.$parent.graph.model.perspectives.filter(function (e) {
				return e !== $scope.perspectiveEntity;
			});
			togglePerspectiveEntityModal();
		};

		function togglePerspectiveEntityModal() {
			$('#perspectiveEntityModal').modal('toggle');
			$scope.error = null;
		}
		// ----

		// Sidebar Management
		$scope.openSidebarNewDialog = function () {
			$scope.actionType = 'sidebarNew';
			$scope.sidebarEntity = {};
			toggleSidebarEntityModal();
		};

		$scope.openSidebarEditDialog = function (entity) {
			$scope.actionType = 'sidebarUpdate';
			$scope.sidebarEntity = entity;
			toggleSidebarEntityModal();
		};

		$scope.openSidebarDeleteDialog = function (entity) {
			$scope.actionType = 'sidebarDelete';
			$scope.sidebarEntity = entity;
			toggleSidebarEntityModal();
		};

		$scope.closeSidebar = function () {
			//load();
			toggleSidebarEntityModal();
		};

		$scope.sidebarCreate = function () {
			if (!$scope.$parent.graph.model.sidebar) {
				$scope.$parent.graph.model.sidebar = [];
			}
			let exists = $scope.$parent.graph.model.sidebar.filter(function (e) {
				return e.path === $scope.sidebarEntity.path;
			});
			if (exists.length === 0) {
				$scope.$parent.graph.model.sidebar.push($scope.sidebarEntity);
				toggleSidebarEntityModal();
			} else {
				$scope.error = "Navigation with the path [" + $scope.sidebarEntity.path + "] already exists!";
			}

		};

		$scope.sidebarUpdate = function () {
			// auto-wired
			toggleSidebarEntityModal();
		};

		$scope.sidebarDelete = function () {
			if (!$scope.$parent.graph.model.sidebar) {
				$scope.$parent.graph.model.sidebar = [];
			}
			$scope.$parent.graph.model.sidebar = $scope.$parent.graph.model.sidebar.filter(function (e) {
				return e !== $scope.sidebarEntity;
			});
			toggleSidebarEntityModal();
		};

		function toggleSidebarEntityModal() {
			$('#sidebarEntityModal').modal('toggle');
			$scope.error = null;
		}
		// ----

		main(document.getElementById('graphContainer'),
			document.getElementById('outlineContainer'),
			document.getElementById('toolbarContainer'),
			document.getElementById('sidebarContainer'),
			document.getElementById('statusContainer'));

	});