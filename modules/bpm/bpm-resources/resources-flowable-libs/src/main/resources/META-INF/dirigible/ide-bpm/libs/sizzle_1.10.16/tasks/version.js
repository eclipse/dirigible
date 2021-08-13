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
"use strict";

var exec = require( "child_process" ).exec;

module.exports = function( grunt ) {
	grunt.registerTask( "version", "Commit a new version", function( version ) {
		if ( !/\d\.\d+\.\d+(?:-pre)?/.test( version ) ) {
			grunt.fatal( "Version must follow semver release format: " + version );
			return;
		}

		var done = this.async(),
			files = grunt.config( "version.files" ),
			rversion = /("version":\s*")[^"]+/;

		// Update version in specified files
		files.forEach(function( filename ) {
			var text = grunt.file.read( filename );
			text = text.replace( rversion, "$1" + version );
			grunt.file.write( filename, text );
		});

		// Add files to git index
		exec( "git add -A", function( err ) {
			if ( err ) {
				grunt.fatal( err );
				return;
			}
			// Commit next pre version
			grunt.config( "pkg.version", version );
			grunt.task.run([ "build", "uglify", "dist", "commit:'Update version to " + version + "'" ]);
			done();
		});
	});
};
