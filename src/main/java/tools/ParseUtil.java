package tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import objects.exception.JSONException;
import objects.json.JSONObject;

final class ParseUtil {
	private ParseUtil() {
		
		throw new AssertionError();
	}

	public static String getRawString(String name, JSONObject json) {
		try {
			if (json.isNull(name)) {
				return null;
			} else {
				return json.getString(name);
			}
		} catch (JSONException jsone) {
			return null;
		}
	}

	static String getURLDecodedString(String name, JSONObject json) {
		String returnValue = getRawString(name, json);
		if (returnValue != null) {
			try {
				returnValue = URLDecoder.decode(returnValue, "UTF-8");
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		return returnValue;
	}

	public static int getInt(String name, JSONObject json) {
		return getInt(getRawString(name, json));
	}

	public static int getInt(String str) {
		if (null == str || "".equals(str) || "null".equals(str)) {
			return -1;
		} else {
			try {
				return Integer.valueOf(str);
			} catch (NumberFormatException nfe) {
				// workaround for the API side issue
				// http://issue.twitter4j.org/youtrack/issue/TFJ-484
				return -1;
			}
		}
	}

	public static long getLong(String name, JSONObject json) {
		return getLong(getRawString(name, json));
	}

	public static long getLong(String str) {
		if (null == str || "".equals(str) || "null".equals(str)) {
			return -1;
		} else {
			// some count over 100 will be expressed as "100+"
			if (str.endsWith("+")) {
				str = str.substring(0, str.length() - 1);
				return Long.valueOf(str) + 1;
			}
			return Long.valueOf(str);
		}
	}

	public static double getDouble(String name, JSONObject json) {
		String str2 = getRawString(name, json);
		if (null == str2 || "".equals(str2) || "null".equals(str2)) {
			return -1;
		} else {
			return Double.valueOf(str2);
		}
	}

	public static boolean getBoolean(String name, JSONObject json) {
		String str = getRawString(name, json);
		if (null == str || "null".equals(str)) {
			return false;
		}
		return Boolean.valueOf(str);
	}

}
