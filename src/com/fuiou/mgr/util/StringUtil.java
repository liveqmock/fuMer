package com.fuiou.mgr.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringUtil{
	private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

	public static char[] DigitalMap = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
		'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	public static int CHAR_BUFFER_SIZE = 4096;
	private static final String sRegex = "^[0-9]*[1-9][0-9]*$";

	public static boolean isNotEmpty(String str)
	{
		return ((str != null) && (str.trim().length() > 0));
	}

	public static boolean isEmpty(String aStr)
	{
		return ((aStr == null) || (aStr.trim().length() == 0));
	}

	public static int indexOfAny(String aStr, String[] aSearchStrs)
	{
		if ((aStr == null) || (aSearchStrs == null)) {
			return -1;
		}

		int tSz = aSearchStrs.length;

		int ret = 2147483647;

		int tmp = 0;
		for (int i = 0; i < tSz; ++i) {
			tmp = aStr.indexOf(aSearchStrs[i]);
			if (tmp == -1) {
				continue;
			}

			if (tmp < ret) {
				ret = tmp;
			}

		}

		return ((ret == 2147483647) ? -1 : ret);
	}

	public static int lastIndexOfAny(String aStr, String[] aSearchStrs)
	{
		if ((aStr == null) || (aSearchStrs == null)) {
			return -1;
		}

		int tSz = aSearchStrs.length;
		int tRet = -1;
		int tTmp = 0;
		for (int i = 0; i < tSz; ++i) {
			tTmp = aStr.lastIndexOf(aSearchStrs[i]);
			if (tTmp > tRet) {
				tRet = tTmp;
			}
		}

		return tRet;
	}

	public static String left(String aStr, int aLen)
	{
		if (aLen < 0) {
			throw new IllegalArgumentException("Requested String length " + aLen + " is less than zero");
		}

		if ((aStr == null) || (aStr.length() <= aLen)) {
			return aStr;
		}

		return aStr.substring(0, aLen);
	}

	public static String right(String aStr, int aLen)
	{
		if (aLen < 0) {
			throw new IllegalArgumentException("Requested String length " + aLen + " is less than zero");
		}

		if ((aStr == null) || (aStr.length() <= aLen)) {
			return aStr;
		}

		return aStr.substring(aStr.length() - aLen);
	}

	public static String mid(String aStr, int pos, int aLen)
	{
		if ((pos < 0) || ((aStr != null) && (pos > aStr.length()))) {
			throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
		}

		if (aLen < 0) {
			throw new IllegalArgumentException("Requested String length " + aLen + " is less than zero");
		}

		if (aStr == null) {
			return null;
		}

		if (aStr.length() <= pos + aLen) {
			return aStr.substring(pos);
		}

		return aStr.substring(pos, pos + aLen);
	}

	public static String concatenate(Object[] aArray)
	{
		return join(aArray, "");
	}

	public static String join(Object[] aArray, String aSeparator)
	{
		if (aSeparator == null) {
			aSeparator = "";
		}

		int tArraySize = aArray.length;
		int tBufSize = (tArraySize == 0) ? 0 : (aArray[0].toString().length() + aSeparator.length()) * tArraySize;
		StringBuffer tBuf = new StringBuffer(tBufSize);

		for (int i = 0; i < tArraySize; ++i) {
			if (i > 0) {
				tBuf.append(aSeparator);
			}

			tBuf.append(aArray[i]);
		}
		return tBuf.toString();
	}

	public static String join(Iterator aIterator, String aSeparator)
	{
		if (aSeparator == null) {
			aSeparator = "";
		}

		StringBuffer tBuf = new StringBuffer(256);

		while (aIterator.hasNext()) {
			tBuf.append(aIterator.next());
			if (aIterator.hasNext()) {
				tBuf.append(aSeparator);
			}
		}

		return tBuf.toString();
	}

	public static String overlayString(String aText, String aOverlay, int aStart, int aEnd)
	{
		return (aStart + aOverlay.length() + aText.length() - aEnd + 1) +
		aText.substring(0, aStart) +
		aOverlay + aText.substring(aEnd);
	}

	public static String center(String aStr, int aSize)
	{
		return center(aStr, aSize, " ");
	}

	public static String center(String aStr, int aSize, String aDelim)
	{
		int tSz = aStr.length();
		int tP = aSize - tSz;
		if (tP < 1) {
			return aStr;
		}

		aStr = leftPad(aStr, tSz + tP / 2, aDelim);
		aStr = rightPad(aStr, aSize, aDelim);
		return aStr;
	}

	public static String chomp(String aStr)
	{
		return chomp(aStr, "\n");
	}

	public static String chomp(String aStr, String aSep)
	{
		int tIdx = aStr.lastIndexOf(aSep);
		if (tIdx != -1) {
			return aStr.substring(0, tIdx);
		}

		return aStr;
	}

	public static String chompLast(String aStr)
	{
		return chompLast(aStr, "\n");
	}

	public static String chompLast(String aStr, String aSep)
	{
		if (aStr.length() == 0) {
			return aStr;
		}

		String tSub = aStr.substring(aStr.length() - aSep.length());
		if (aSep.equals(tSub)) {
			return aStr.substring(0, aStr.length() - aSep.length());
		}

		return aStr;
	}

	public static String getChomp(String aStr, String aSep)
	{
		int tIdx = aStr.lastIndexOf(aSep);
		if (tIdx == aStr.length() - aSep.length()) {
			return aSep;
		}
		if (tIdx != -1) {
			return aStr.substring(tIdx);
		}

		return "";
	}

	public static String prechomp(String aStr, String aSep)
	{
		int tIdx = aStr.indexOf(aSep);
		if (tIdx != -1) {
			return aStr.substring(tIdx + aSep.length());
		}

		return aStr;
	}

	public static String getPrechomp(String aStr, String aSep)
	{
		int tIdx = aStr.indexOf(aSep);
		if (tIdx != -1) {
			return aStr.substring(0, tIdx + aSep.length());
		}

		return "";
	}

	public static String chop(String aStr)
	{
		if ("".equals(aStr)) {
			return "";
		}

		if (aStr.length() == 1) {
			return "";
		}

		int tLastIdx = aStr.length() - 1;
		String tRet = aStr.substring(0, tLastIdx);
		char last = aStr.charAt(tLastIdx);
		if ((last == '\n') &&
				(tRet.charAt(tLastIdx - 1) == '\r')) {
			return tRet.substring(0, tLastIdx - 1);
		}

		return tRet;
	}

	public static String chopNewline(String aStr)
	{
		int tLastIdx = aStr.length() - 1;
		char tLast = aStr.charAt(tLastIdx);
		if (tLast == '\n') {
			if (aStr.charAt(tLastIdx - 1) == '\r') {
				--tLastIdx;
			} else {
				++tLastIdx;
			}
		}

		return aStr.substring(0, tLastIdx);
	}
	//
	//	public static String escape(String aStr)
	//	{
	//		int tSz = aStr.length();
	//		StringBuffer tBuffer = new StringBuffer(2 * tSz);
	//
	//		for (int i = 0; i < tSz; ++i) {
	//			char tCh = aStr.charAt(i);
	//
	//			if (tCh > 4095) {
	//				tBuffer.append("\\u" + Integer.toHexString(tCh));
	//			} else if (tCh > 255) {
	//				tBuffer.append("\\u0" + Integer.toHexString(tCh));
	//			} else if (tCh > '') {
	//				tBuffer.append("\\u00" + Integer.toHexString(tCh)); } else {
	//					if (tCh < ' ') {
	//						;
	//					}
	//					switch (tCh)
	//					{
	//					case '\b':
	//						tBuffer.append('\\');
	//						tBuffer.append('b');
	//						break;
	//					case '\n':
	//						tBuffer.append('\\');
	//						tBuffer.append('n');
	//						break;
	//					case '\t':
	//						tBuffer.append('\\');
	//						tBuffer.append('t');
	//						break;
	//					case '\f':
	//						tBuffer.append('\\');
	//						tBuffer.append('f');
	//						break;
	//					case '\r':
	//						tBuffer.append('\\');
	//						tBuffer.append('r');
	//						break;
	//					case '\11':
	//					default:
	//						if (tCh > '\15') {
	//							tBuffer.append("\\u00" + Integer.toHexString(tCh));
	//						} else {
	//							tBuffer.append("\\u000" + Integer.toHexString(tCh));
	//
	//							continue;
	//
	//							switch (tCh)
	//							{
	//							case '\'':
	//								tBuffer.append('\\');
	//								tBuffer.append('\'');
	//								break;
	//							case '"':
	//								tBuffer.append('\\');
	//								tBuffer.append('"');
	//								break;
	//							case '\\':
	//								tBuffer.append('\\');
	//								tBuffer.append('\\');
	//								break;
	//							default:
	//								tBuffer.append(tCh); }
	//						}
	//					}
	//				}
	//		}
	//		return tBuffer.toString();
	//	}

	public static String repeat(String aStr, int aRepeat)
	{
		StringBuffer tBuffer = new StringBuffer(aRepeat * aStr.length());
		for (int i = 0; i < aRepeat; ++i) {
			tBuffer.append(aStr);
		}

		return tBuffer.toString();
	}

	public static String rightPad(String aStr, int aSize)
	{
		return rightPad(aStr, aSize, " ");
	}

	public static String rightPad(String aStr, int aSize, String aDelim)
	{
		aSize = (aSize - aStr.length()) / aDelim.length();
		if (aSize > 0) {
			aStr = aStr + repeat(aDelim, aSize);
		}

		return aStr;
	}

	public static String leftPad(String aStr, int aSize)
	{
		return leftPad(aStr, aSize, " ");
	}

	public static String leftPad(String aStr, int aSize, String aDelim)
	{
		aSize = (aSize - aStr.length()) / aDelim.length();
		if (aSize > 0) {
			aStr = repeat(aDelim, aSize) + aStr;
		}

		return aStr;
	}





	public static String upperCase(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		return aStr.toUpperCase();
	}

	public static String lowerCase(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		return aStr.toLowerCase();
	}

	public static String uncapitalise(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		if (aStr.length() == 0) {
			return "";
		}

		return aStr.length() + Character.toLowerCase(aStr.charAt(0)) + aStr.substring(1);
	}

	public static String capitalise(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		if (aStr.length() == 0) {
			return "";
		}

		return aStr.length() + Character.toTitleCase(aStr.charAt(0)) + aStr.substring(1);
	}


	public static String capitaliseAllWords(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		int tSz = aStr.length();
		StringBuffer tBuffer = new StringBuffer(tSz);
		boolean tSpace = true;
		for (int i = 0; i < tSz; ++i) {
			char tCh = aStr.charAt(i);
			if (Character.isWhitespace(tCh)) {
				tBuffer.append(tCh);
				tSpace = true;
			} else if (tSpace) {
				tBuffer.append(Character.toTitleCase(tCh));
				tSpace = false;
			} else {
				tBuffer.append(tCh);
			}
		}
		return tBuffer.toString();
	}

	public static String getNestedString(String aStr, String aTag)
	{
		return getNestedString(aStr, aTag, aTag);
	}

	public static String getNestedString(String aStr, String aOpen, String aClose)
	{
		if (aStr == null) {
			return null;
		}

		int tStart = aStr.indexOf(aOpen);
		if (tStart != -1) {
			int end = aStr.indexOf(aClose, tStart + aOpen.length());
			if (end != -1) {
				return aStr.substring(tStart + aOpen.length(), end);
			}
		}

		return null;
	}

	public static int countMatches(String aStr, String aSub)
	{
		if (aStr == null) {
			return 0;
		}

		int tCount = 0;
		int tIdx = 0;
		while ((tIdx = aStr.indexOf(aSub, tIdx)) != -1) {
			++tCount;
			tIdx += aSub.length();
		}
		return tCount;
	}

	public static boolean isAlpha(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if (!(Character.isLetter(aStr.charAt(i)))) {
				return false;
			}
		}


		return true;
	}

	public static boolean isAlphaSpace(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if ((!(Character.isLetter(aStr.charAt(i)))) && (aStr.charAt(i) != ' ')) {
				return false;
			}
		}


		return true;
	}

	public static boolean isAlphanumeric(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if (!(Character.isLetterOrDigit(aStr.charAt(i)))) {
				return false;
			}
		}


		return true;
	}

	public static boolean isAlphanumericSpace(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if ((!(Character.isLetterOrDigit(aStr.charAt(i)))) && (aStr.charAt(i) != ' ')) {
				return false;
			}
		}


		return true;
	}

	public static boolean isNumeric(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if (!(Character.isDigit(aStr.charAt(i)))) {
				return false;
			}
		}


		return true;
	}

	public static boolean isNumericSpace(String aStr)
	{
		if (aStr == null) {
			return false;
		}

		int tSz = aStr.length();
		for (int i = 0; i < tSz; ++i) {
			if ((!(Character.isDigit(aStr.charAt(i)))) && (aStr.charAt(i) != ' ')) {
				return false;
			}
		}


		return true;
	}

	public static String reverse(String aStr)
	{
		if (aStr == null) {
			return null;
		}

		return new StringBuffer(aStr).reverse().toString();
	}



	public static byte[] extract(byte[] bData, int start, int len)
	{
		byte[] bTmp = new byte[len];

		for (int i = 0; i < len; ++i)
		{
			bTmp[i] = bData[(start + i)];
		}

		return bTmp;
	}

	public static String convertUnicodeToNative(String aSource, String aCharset)
	throws IOException
	{
		ByteArrayOutputStream tBaos = new ByteArrayOutputStream();
		OutputStreamWriter tOut = new OutputStreamWriter(tBaos, aCharset);
		tOut.write(aSource);
		tOut.close();
		return tBaos.toString();
	}

	public static String convertNativeToUnicode(String aInput, String aCharset)
	throws IOException
	{
		InputStreamReader tIn = new InputStreamReader(new ByteArrayInputStream(aInput.getBytes()), aCharset);
		StringBuffer tOutput = new StringBuffer();
		char[] tBuf = new char[CHAR_BUFFER_SIZE];
		int tCount = 0;
		while ((tCount = tIn.read(tBuf, 0, CHAR_BUFFER_SIZE)) > 0) {
			tOutput.append(tBuf, 0, tCount);
		}

		tIn.close();
		return tOutput.toString();
	}

	public static char[] decToAscii(long aDec)
	throws Exception
	{
		long temp = aDec;
		long[] ts = null;
		long[] tp = null;
		int n = 1;
		while (temp > 256L) {
			temp /= 256L;
			++n;
		}

		ts = new long[n];
		tp = new long[n];
		temp = aDec;
		for (int x = 0; x < n; ++x) {
			if (temp > 256L) {
				tp[x] = (temp / 256L);
				ts[x] = (temp % 256L);
				temp = tp[x];
			}
		}

		ts[(n - 1)] = temp;
		char[] tc = new char[n];
		int j = 0;
		for (int i = n - 1; i >= 0; --i) {
			tc[j] = (char)(int)ts[i];
			++j;
		}

		return tc;
	}

	public static char[] addHexPrefix0(char[] aSor, int aLength)
	throws Exception
	{
		char[] temp = new char[aLength];
		char c = '0';
		if ((aSor == null) || (aSor.length < 1)) {
			for (int i = 0; i < aLength; ++i) {
				temp[i] = c;
			}

			return temp;
		}
		int sorLen = aSor.length;
		for (int i = 0; i < aLength - sorLen; ++i) {
			temp[i] = c;
		}

		int j = 0;
		for (int i = aLength - sorLen; i < aLength; ++i) {
			temp[i] = aSor[j];
			++j;
		}
		return temp;
	}

	public static String realAmount(String aAmountStr, String aNum)
	{
		int tTemp = 0;
		tTemp = Integer.parseInt(aNum);
		StringBuffer tStringBuffer = new StringBuffer();
		if (aAmountStr.length() > tTemp) {
			tStringBuffer.append(aAmountStr.substring(0, aAmountStr.length() - tTemp));
			tStringBuffer.append(".");
			tStringBuffer.append(aAmountStr.substring(aAmountStr.length() - tTemp, aAmountStr.length()));
		}
		if (aAmountStr.length() < tTemp) {
			tStringBuffer.append("0.");
			tStringBuffer.append(aAmountStr);
		}
		return tStringBuffer.toString();
	}

	public static boolean validateNum(String num)
	{
		boolean isMatch = false;

		Pattern tMask = Pattern.compile("^[0-9]*[1-9][0-9]*$");
		if (isNotEmpty(num))
		{
			Matcher tMatch = tMask.matcher(num);
			if (tMatch.matches()) {
				isMatch = true;
			}

		}

		return isMatch;
	}

	public static int checkArray(String[] arrayList, String chkStr)
	{
		int isValidate = -1;

		for (int i = 0; i < arrayList.length; ++i) {
			if (arrayList[i].equals(chkStr)) {
				isValidate = 0;
				break;
			}
		}


		return isValidate;
	}

	public static String checkSplitStr(String str)
	{
		String splitStr = "";
		if (str.indexOf("\r\n") != -1) {
			splitStr = "\r\n";
		} else if (str.indexOf("\n") != -1) {
			splitStr = "\n";
		} else if (str.indexOf("\r") != -1) {
			splitStr = "\r";
		}

		return splitStr;
	}

	public static String i2a(int x, int len, int base) {
		char[] s = new char[len];

		for (int i = len - 1; i >= 0; --i) {
			s[i] = DigitalMap[(x % base)];
			x /= base;
		}
		return new String(s);
	}
	
	public static String format(long num, int width) {
		if (num < 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}
	
	public static String formatYuanToFen(String amt)
	{
		return formatYuanToFen(Double.parseDouble(amt));
	}

	public static String formatYuanToFen(double amt) {
		return String.valueOf(Math.round(amt * 100.0D));
	}

	public static boolean compareCertId(String certid1, String certid2)
	{
		String cttype1;
		try
		{
			cttype1 = certid1.substring(0, 2);
			String cttype2 = certid2.substring(0, 2);

			if (!(cttype1.equals(cttype2)))
			{
				logger.error("com.chinapay.util.StringUtils    in compareCertId 证件类型不符");
				return false;
			}

			String ctid1 = certid1.substring(2);
			String ctid2 = certid2.substring(2);

			if (cttype1.equals("01")) {
				int len1 = ctid1.length();
				int len2 = ctid2.length();
				if (((len1 != 15) && (len1 != 18)) || ((len2 != 15) && (len2 != 18))) {
					logger.error("com.chinapay.util.StringUtils    in compareCertId 身份证号长度错误");
					return false;
				}
				if (len1 == len2)
				{
					if (!(ctid1.equals(ctid2))) {
						logger.error("com.chinapay.util.StringUtils     pose1:两个证件号不符 certid1[" +
								certid1 + "]certid2[" + certid2 + "]");
						return false;
					}
					return true;
				}

				if (len1 == 18) {
					ctid1 = new StringBuffer(ctid1).delete(6, 8).delete(15, 16).toString();
				} else if (len2 == 18) {
					ctid2 = new StringBuffer(ctid2).delete(6, 8).delete(15, 16).toString();
				}
				if (!(ctid1.equals(ctid2))) {
					logger.error("com.chinapay.util.StringUtils    pose2:两个证件号不符 certid1[" +
							certid1 + "]certid2[" + certid2 + "]");
					return false;
				}
				return true;
			}

			if (!(ctid1.equals(ctid2))) {
				logger.error("com.chinapay.util.StringUtils   pose3:两个证件号不符 certid1[" +
						certid1 + "]certid2[" + certid2 + "]");
				return false;
			}
			return true;
		}
		catch (Exception e) {
		}
		return false;
	}

	public static boolean isNumber(String number, int decDegit)
	{
		char ch;
		if (isEmpty(number)) {
			return false;
		}
		number = number.trim();

		int dot = number.indexOf(".");
		if ((dot != -1) && (decDegit == 0)) {
			return false;
		}
		if (number.lastIndexOf(".") != dot) {
			return false;
		}
		if (dot == 0) {
			return false;
		}
		String intNumber = (dot == -1) ? number : number.substring(0, dot);
		String decNumber = (dot == -1) ? "" : number.substring(dot + 1);
		if (decNumber.length() > decDegit) {
			return false;
		}
		if (intNumber.startsWith("00")) {
			return false;
		}
		for (int i = 0; i < intNumber.length(); ++i) {
			ch = intNumber.charAt(i);
			if ((ch < '0') || (ch > '9')) {
				return false;
			}
		}

		for (int i = 0; i < decNumber.length(); ++i) {
			ch = decNumber.charAt(i);
			if ((ch < '0') || (ch > '9')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Left pad a String with spaces. Pad to a size of n.
	 * 
	 * @param aStr
	 *            String to pad out
	 * @param aSize
	 *            int aSize to pad to
	 */
	public static String leftPadWithZero(String aStr, int aSize)
	{
		return leftPad(aStr, aSize, "0");
	}

}