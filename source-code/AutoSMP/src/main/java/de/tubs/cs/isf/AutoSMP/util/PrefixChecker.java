package de.tubs.cs.isf.AutoSMP.util;

import java.util.List;

import de.tubs.cs.isf.AutoSMP.AutoSMP;

/**
 * Class used to compute the longest common prefix for a list of strings. Used
 * by the {@link TWiseSamplingFramework} to automatically determine whether
 * sampling stability should be computed or not.
 * 
 * @author Joshua Sprey
 */
public class PrefixChecker {

	private static boolean allCharactersAreSame(String[] strings, int pos) {
		String first = strings[0];
		for (String curString : strings) {
			if (curString.length() <= pos || curString.charAt(pos) != first.charAt(pos)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes the longest common prefix for a given list of strings.
	 * 
	 * @param stringsList List of strings.
	 * @return Longest common prefix.
	 */
	public static String getLongestCommonPrefix(List<String> stringsList) {
		String[] strings = stringsList.toArray(new String[stringsList.size()]);
		int commonPrefixLength = 0;
		while (allCharactersAreSame(strings, commonPrefixLength)) {
			commonPrefixLength++;
		}
		return strings[0].substring(0, commonPrefixLength);
	}

	/**
	 * Computes the longest common prefix for a given array of strings.
	 * 
	 * @param stringsArray Array of strings.
	 * @return Longest common prefix.
	 */
	public static String getLongestCommonPrefix(String[] stringsArray) {
		int commonPrefixLength = 0;
		while (allCharactersAreSame(stringsArray, commonPrefixLength)) {
			commonPrefixLength++;
		}
		return stringsArray[0].substring(0, commonPrefixLength);
	}
}
