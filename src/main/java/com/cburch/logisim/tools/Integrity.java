package com.cburch.logisim.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Integrity {
	/**
	 * Hash the input with <code>INTEGRITY_ALGORITHM</code>, adding the
	 * <code>INTEGRITY_SALT</code>
	 *
	 * @param input
	 *          string
	 * @return input string hash as hex string
	 */
	public static String getHashOf(String input) {
		MessageDigest mDigest;

		try {
			mDigest = MessageDigest.getInstance(Integrity.INTEGRITY_ALGORITHM);

			/* Remove problematic characters */
			input = input.replace("\r", "").replace("\n", "").replace(" ", "");

			/* Add salt */
			input = input + Integrity.INTEGRITY_SALT;

			StringBuffer sb = new StringBuffer();

			byte[] result;
			try {
				result = mDigest.digest(input.getBytes("UTF-8"));

				for (int i = 0; i < result.length; i++) {
					sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
							.substring(1));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Verifies string checksum
	 *
	 * @param input
	 *          string
	 * @param testChecksum
	 *          the expected checksum
	 * @return true if the expected checksum matches the input checksum; false
	 *         otherwise.
	 */
	public static boolean verifyChecksum(String input, String testChecksum) {
		return getHashOf(input) == testChecksum;
	}

	private final static String INTEGRITY_ALGORITHM = "SHA1";

	private final static String INTEGRITY_SALT = "6234ebf2-60c5-4f0e-b3cb-bb887cfd379b";
}
