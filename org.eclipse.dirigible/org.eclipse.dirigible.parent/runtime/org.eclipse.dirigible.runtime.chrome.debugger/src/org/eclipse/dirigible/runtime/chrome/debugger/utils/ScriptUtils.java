package org.eclipse.dirigible.runtime.chrome.debugger.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;

//TODO: more than one function declared on same line
public class ScriptUtils {

	private static final String FUNCTION_PREFIX = "function";

	/**
	 * Returns the name of the enclosing function for {@code breakpointLine}
	 *
	 * @param scriptId
	 *            the scriptId associated with the source in which to look for
	 * @param breakpointLine
	 *            the line number for which too look for an enclosing function
	 * @return the name of the enclosing function
	 */
	public static String getEnclosingFunctionName(final String scriptId, final Integer breakpointLine) {
		final Location startLocation = getStartLocation(scriptId, breakpointLine);
		final int lineWithFunction = startLocation.getLineNumber().intValue();
		if(lineWithFunction == -1){
			return null;
		}
		final String source = ScriptRepository.getInstance().getSourceFor(scriptId);
		final String line = getLine(source, lineWithFunction);
		final Integer indexOfFunc = line.indexOf(FUNCTION_PREFIX) + FUNCTION_PREFIX.length();
		final Integer indexOfBracket = line.indexOf("(");

		return line.substring(indexOfFunc, indexOfBracket).trim();
	}

	/**
	 * Returns a {@link Location} object representing the start location of the enclosing function
	 * for {@code breakpointLine}.
	 *
	 * @param scriptId
	 *            the scriptId associated with the source in which to look for
	 * @param breakpointLine
	 *            the line number for which to look for an enclosing scope
	 * @return the start location of the enclosing function
	 */
	public static Location getStartLocation(final String scriptId, final Integer breakpointLine) {
		final String source = ScriptRepository.getInstance().getSourceFor(scriptId);
		final Map<Integer, List<Integer>> lineScopes = getLineScopes(source);
		final Integer startLine = getStartLine(lineScopes, breakpointLine);
		final Integer startColumn = getStartColumn(source, startLine);
		final Location location = new Location();
		location.setColumnNumber(startColumn * 1.0);
		location.setLineNumber(startLine * 1.0);
		location.setScriptId(scriptId);
		return location;
	}

	/**
	 * Returns a {@link Location} object representing the
	 * end location of the enclosing scope in
	 * which the {@code breakpointLine} is.
	 *
	 * @param scriptId
	 *            the scriptId associated with a source file
	 * @param breakpointLine
	 *            the line at which a breakpoint in a file with {@code scriptId} is set
	 * @return the end location for the enclosing scope of the breakpointLine
	 */
	public static Location getEndLocation(final String scriptId, final Integer breakpointLine) {
		final String source = ScriptRepository.getInstance().getSourceFor(scriptId);
		final Map<Integer, List<Integer>> lineScopes = getLineScopes(source);
		final Integer endLine = getEndLine(lineScopes, breakpointLine);
		final Integer startColumn = getStartColumn(source, breakpointLine);
		final Integer startLine = getStartLine(lineScopes, breakpointLine);
		final Integer endColumn = getEndColumn(source, startLine, startColumn);

		final Location location = new Location();
		location.setColumnNumber(endColumn * 1.0);
		location.setLineNumber(endLine * 1.0);
		location.setScriptId(scriptId);
		return location;
	}

	/**
	 * Returns a map representation of the <b>lines<b> at which a function starts and ends in the
	 * {@code source} text.
	 * More than one function can be on the same line, so the list represents the lines on which
	 * these functions end.
	 *
	 * @param source
	 *            the text in which to search for function scopes
	 * @return a map of the line scopes of the functions
	 */
	private static Map<Integer, List<Integer>> getLineScopes(final String source) {
		final Map<Integer, List<Integer>> lineScopes = new HashMap<Integer, List<Integer>>();
		final Map<Integer, Integer> functionScopes = getFunctionScopes(source);
		for (final Map.Entry<Integer, Integer> scope : functionScopes.entrySet()) {
			final Integer startIndex = scope.getKey();
			final Integer endIndex = scope.getValue();

			final Integer startLine = getLineNumberForIndex(source, startIndex);
			List<Integer> functionsOnLine = lineScopes.get(startLine);
			if (functionsOnLine == null) {
				functionsOnLine = new ArrayList<Integer>();
			}
			final Integer endLine = getLineNumberForIndex(source, endIndex);
			functionsOnLine.add(endLine);
			lineScopes.put(startLine, functionsOnLine);
		}

		return lineScopes;
	}

	/**
	 * Returns a map representation of the <b>indexes<b> of the start and end location of the
	 * functions in the {@code source} text.
	 * A scope is defined as follows:
	 *
	 * <pre>
	 * 	[ Index at which {@code function} keyword occurs : Index of matching closing bracket]
	 * </pre>
	 *
	 * @param source
	 *            the source text in which to look for function scopes
	 * @return a map representing the scopes in the {@code source} text
	 */
	public static Map<Integer, Integer> getFunctionScopes(final String source) {
		final Map<Integer, Integer> scopes = new HashMap<Integer, Integer>();
		final List<Integer> indexes = getMatchingIndexes(source, FUNCTION_PREFIX);
		Integer endIndex;
		for (int i = 0; i < indexes.size(); i++) {
			final int startIndex = indexes.get(i);
			endIndex = getClosingBracketIndex(source, startIndex);
			scopes.put(startIndex, endIndex);
		}

		return scopes;
	}

	/**
	 * Returns a list containing all the indexes at which {@code match} occurs in {@code source}.
	 *
	 * @param source
	 *            the text in which to search for
	 * @param match
	 *            the text to search for in {@code source}
	 * @return a list of all the indexes at which {@code match} occurrs
	 */
	private static List<Integer> getMatchingIndexes(final String source, final String match) {
		final List<Integer> indexes = new ArrayList<Integer>();
		int index = ((source.indexOf(match) + match.length())) - 1;
		while (index >= 0 && index < source.length()) {
			while (source.charAt(index) != '{' && index < source.length() - 1) {
				index++;
			}
			indexes.add(index);
			index = source.indexOf(match, index + 1);
		}
		return indexes;
	}

	/**
	 * Starting from position {@code startingIndex} search through the given {@code text} for the
	 * index corresponding to the closing bracket. The index of the opening bracket is either
	 * at {@code startingIndex} or the first index at which an opening bracket occurs after
	 * {@code startingIndex}.
	 *
	 * @param text
	 *            the text in which to search for the closing bracket
	 * @param startingIndex
	 *            the index at which the opening bracket is or the index from which to start
	 *            searching for a scope
	 * @return the index of the closing bracket of the outer most scope with an opening bracket
	 *         closest or equal to {@code startingIndex}
	 */
	private static Integer getClosingBracketIndex(final String text, int startingIndex) {
		final Deque<Character> stack = new ArrayDeque<Character>();

		while ((startingIndex < text.length()) && (text.charAt(startingIndex) != '{')) {
			startingIndex++;
		}

		for (int i = startingIndex; i < text.length(); i++) {
			final char current = text.charAt(i);
			if (current == '{') {
				stack.push(current);
			}
			if (current == '}') {
				stack.pop();
			}
			if (stack.isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Given an {@code index} in the {@code source} text returns the line number which corresponds
	 * to the index.
	 *
	 * @param source
	 *            the text in which to look for
	 * @param index
	 *            the index for which we want to know the line number
	 * @return the line number which corresponds to the index
	 */
	private static Integer getLineNumberForIndex(final String source, final Integer index) {
		int lineNum = 0;
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == '\n') {
				lineNum++;
			}

			if (i == index) {
				return lineNum;
			}
		}

		return lineNum;
	}

	/**
	 * Returns the start line of the scope in which {@code lineNumber} is.
	 *
	 * @param lineScopes
	 *            a map representing the start-end lines of scopes
	 * @param lineNumber
	 *            the line number for which we want to know the start line
	 * @return the start line of the scope
	 */
	private static Integer getStartLine(final Map<Integer, List<Integer>> lineScopes, final Integer lineNumber) {
		int closestLine = -1;
		for (final Entry<Integer, List<Integer>> scope : lineScopes.entrySet()) {
			final Integer startLine = scope.getKey();
			final List<Integer> endLines = scope.getValue();
			for (final Integer line : endLines) {
				if ((lineNumber >= startLine) && (lineNumber <= line)) {
					if (closestLine <= startLine) {
						closestLine = startLine;
					}
				}
			}

		}
		return closestLine; // TODO
	}

	/**
	 * Returns the column at which {@code function} keyword occurs on {@code lineNumber} in
	 * {@code source} text.
	 *
	 * @param source
	 *            the text in which to search
	 * @param lineNumber
	 *            the line number on which to search
	 * @return the column number of the function keyword
	 */
	private static Integer getStartColumn(final String source, final Integer breakpointLine) {
		final Map<Integer, Integer> functionScopes = getFunctionScopes(source);
		int closestLine = 0;
		for (final Map.Entry<Integer, Integer> e : functionScopes.entrySet()) {
			final Integer scopeStart = e.getKey();
			final Integer scopeEnd = e.getValue();
			final Integer startLine = getLineNumberForIndex(source, scopeStart);
			final Integer endLine = getLineNumberForIndex(source, scopeEnd);
			if ((breakpointLine >= startLine) && (breakpointLine <= endLine)) {
				if (closestLine <= startLine) {
					closestLine = startLine;
				}
			}
		}

		final String line = getLine(source, closestLine);
		int index, column = 0;
		for(index = 0; index < line.length(); index++){
			if(line.charAt(index) == '{'){
				return column;
			}
			
			if(line.charAt(index) == '\t'){
				column += 4;
			}else{
				column++;
			}
		}
		return column;
	}

	private static String getLine(final String source, final Integer lineNumber) {
		return source.split("\n")[lineNumber];
	}

	/**
	 * Returns the end line of the scope enclosing {@code lineNumber}.
	 *
	 * @param lineScopes
	 *            a map representation of the line numbers of the scopes
	 * @param lineNumber
	 *            the line number for which we want to know the end line of the enclosing scope
	 * @return the end line of the enclosing scope
	 */
	private static Integer getEndLine(final Map<Integer, List<Integer>> lineScopes, final Integer lineNumber) {
		int closestEndLine = Integer.MAX_VALUE;
		final Integer startLine = getStartLine(lineScopes, lineNumber);
		for (final Map.Entry<Integer, List<Integer>> e : lineScopes.entrySet()) {
			if (e.getKey().equals(startLine)) {
				for (final Integer endLine : e.getValue()) {
					if (lineNumber <= endLine && endLine <= closestEndLine) {
						closestEndLine = endLine;
					}
				}
			}
		}

		if (closestEndLine == Integer.MAX_VALUE) {
			return -1;
		}

		return closestEndLine;
	}

	/**
	 * Returns the column number of the closing bracket of the scope starting on line
	 * {@code startLine} and opening bracket on {@code startColumn}.
	 *
	 * @param source
	 *            the source in which to search
	 * @param startLine
	 *            the start line of the scope
	 * @param startColumn
	 *            the start column of the scope
	 * @return the column number of the end of the scope
	 */
	private static Integer getEndColumn(final String source, final Integer startLine, final Integer startColumn) {
		final Map<Integer, Map<Integer, Integer>> scopes = getColumnScopes(source);
		for (final Map.Entry<Integer, Map<Integer, Integer>> scope : scopes.entrySet()) {
			final Integer startLineForScope = scope.getKey();
			if (startLineForScope.equals(startLine)) {
				final Map<Integer, Integer> columnScopes = scope.getValue();
				for (final Map.Entry<Integer, Integer> e : columnScopes.entrySet()) {
					final Integer startColumnForScope = e.getKey();
					if (startColumnForScope.equals(startColumn)) {
						final Integer endColumn = e.getValue();
						return endColumn;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Returns a map representation of the column scopes of the functions in the given
	 * {@code source}.
	 * The format is as follows:
	 *
	 * <pre>
	 *  < Start line number : < Start column number : End column number > >
	 * </pre>
	 *
	 * @param source
	 * @return
	 */
	private static Map<Integer, Map<Integer, Integer>> getColumnScopes(final String source) {
		final Map<Integer, Map<Integer, Integer>> scopes = new HashMap<Integer, Map<Integer, Integer>>();
		final Map<Integer, Integer> functionScopes = getFunctionScopes(source);
		for (final Map.Entry<Integer, Integer> e : functionScopes.entrySet()) {
			final Integer startScope = e.getKey();
			final Integer endScope = e.getValue();
			final Integer startLine = getLineNumberForIndex(source, startScope);
			final Integer startColumn = getColumnNumberForIndex(source, startScope);
			final Integer endColumn = getColumnNumberForIndex(source, endScope);
			final Map<Integer, Integer> columnScope = new HashMap<Integer, Integer>();
			columnScope.put(startColumn, endColumn);
			scopes.put(startLine, columnScope);
		}
		return scopes;
	}

	/**
	 * Given an {@code index} in the {@code source} text returns the column number which corresponds
	 * to the index.
	 *
	 * @param source
	 *            the text in which to look for
	 * @param index
	 *            the index for which we want to know the column number
	 * @return the column number which corresponds to the index
	 */
	private static Integer getColumnNumberForIndex(final String source, final Integer index) {
		int columnNum = 0;
		for (int i = 0; i < source.length(); i++) {
			if (i == index) {
				return columnNum;
			}

			if (source.charAt(i) == '\n') {
				columnNum = 0;
			}

			if (source.charAt(i) == '\t') {
				columnNum += 4;
			}
			columnNum++;
		}
		return columnNum;
	}

	public static boolean hasFunctions(String scriptId) {
		ScriptRepository scriptRepo = ScriptRepository.getInstance();
		String source = scriptRepo.getSourceFor(scriptId);
		return source.contains(FUNCTION_PREFIX);
	}

	public static Double getLastLine(String scriptId) {
		ScriptRepository scriptRepo = ScriptRepository.getInstance();
		String source = scriptRepo.getSourceFor(scriptId);
		return Double.valueOf(getLineNumberForIndex(source, source.length() - 1));
	}

	public static Double getLastColumn(String scriptId) {
		ScriptRepository scriptRepo = ScriptRepository.getInstance();
		String source = scriptRepo.getSourceFor(scriptId);
		return Double.valueOf(getColumnNumberForIndex(source, source.length() - 1));
	}

	public static Double getFirstLine(String scriptId) {
		ScriptRepository scriptRepo = ScriptRepository.getInstance();
		String source = scriptRepo.getSourceFor(scriptId);
		for(int i = 0; i<source.length(); i++){
			Integer lineNumber = getLineNumberForIndex(source, i);
			String line = getLine(source, lineNumber);
			i += line.length();
			if(!isComment(line)){
				return Double.valueOf(lineNumber);
			}
		}
		
		return null;
	}

	private static boolean isComment(String line) {
		return line.startsWith("//") || line.startsWith("/*") || line.trim().isEmpty();
	}

	public static Double getStartColumnForLine(String scriptId, Double lineNumber) {
		ScriptRepository repo = ScriptRepository.getInstance();
		String source = repo.getSourceFor(scriptId);
		String line = getLine(source, lineNumber.intValue());
		int column = 0;
		while(Character.isWhitespace(line.indexOf(column)) && column < line.length()){
			column++;
		}
		return Double.valueOf(column);
	}

	public static Location getFirstLocationAfter(Location functionEndLocation) {
		Double line = functionEndLocation.getLineNumber();
		String scriptId = functionEndLocation.getScriptId();
		ScriptRepository repo = ScriptRepository.getInstance();
		String source = repo.getSourceFor(scriptId);
		String nextSourceLine = "";
		while(nextSourceLine.trim().isEmpty()){
			line++;
			nextSourceLine = getLine(source, line.intValue());
		}
		Location location = new Location();
		location.setColumnNumber(getStartColumnForLine(scriptId, line));
		location.setLineNumber(line);
		location.setScriptId(scriptId);
		return location;
	}
}
