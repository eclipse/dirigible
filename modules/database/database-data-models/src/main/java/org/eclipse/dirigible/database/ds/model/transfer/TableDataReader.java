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
package org.eclipse.dirigible.database.ds.model.transfer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TableDataReader {

	private static final String INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D = "Invalid number (%d) of elements at line: %d. Initial columns number: %d.";

	private static final String DELIMITER = "|"; //$NON-NLS-1$

	public static List<String[]> readRecords(InputStream csvFile)
			throws FileNotFoundException, IOException, InvalidNumberOfElementsException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile, StandardCharsets.UTF_8));
		List<String[]> data = new ArrayList<String[]>();

		int item_count = -1;
		int line_number = 0;
		while (true) {
			String line = reader.readLine();
			line_number++;
			if (line == null) {
				break;
			}
			String[] items = getStringItems(line);
			if (item_count == -1) {
				item_count = items.length;
			} else if (item_count != items.length) {
				throw new InvalidNumberOfElementsException(
						String.format(INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D, items.length,
								line_number, item_count));
			}
			data.add(items);
		}
		reader.close();
		return data;
	}

	private static String[] getStringItems(String str) {
		String delimiter = DELIMITER;
		StringTokenizer tok = new StringTokenizer(str, delimiter, true);

		List<String> res = new ArrayList<String>();

		boolean delimiterIsPreviousToken = true;
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if (delimiter.equals(token)) {
				if (delimiterIsPreviousToken) {
					res.add((String) null);
				}
				delimiterIsPreviousToken = true;
			} else {
				res.add(token);
				delimiterIsPreviousToken = false;
			}
		}
		if (delimiterIsPreviousToken) {
			res.add((String) null);
		}

		String[] myArr = new String[res.size()];
		res.toArray(myArr);
		return myArr;
	}

}
