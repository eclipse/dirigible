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

module.exports = function( grunt ) {
	grunt.registerMultiTask(
		"compile",
		"Compile sizzle.js to the dist directory. Embed date/version.",
		function() {
			var data = this.data,
				dest = data.dest,
				src = data.src,
				version = grunt.config( "pkg.version" ),
				compiled = grunt.file.read( src );

			// Embed version and date
			compiled = compiled
				.replace( /@VERSION/g, version )
				.replace( "@DATE", function() {
					var date = new Date();

					// YYYY-MM-DD
					return [
						date.getFullYear(),
						( "0" + ( date.getMonth() + 1 ) ).slice( -2 ),
						( "0" + date.getDate() ).slice( -2 )
					].join( "-" );
				});

			// Write source to file
			grunt.file.write( dest, compiled );

			grunt.log.ok( "File written to " + dest );
		}
	);
};
