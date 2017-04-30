package org.eclipse.dirigible.repository.ext.db.transfer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.db.InvalidNumberOfElementsException;
import org.eclipse.dirigible.repository.ext.db.Messages;

public class DBTableDataReader {

	private static final String INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D = Messages.DBTableDataInserter_INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D;

	private static final String DELIMITER = "|"; //$NON-NLS-1$

	public static List<String[]> readRecords(InputStream csvFile) throws FileNotFoundException, IOException, InvalidNumberOfElementsException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile, ICommonConstants.UTF8));
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
						String.format(INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D, items.length, line_number, item_count));
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
