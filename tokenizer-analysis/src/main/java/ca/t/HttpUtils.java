package ca.t;

import org.apache.tika.mime.MediaType;

public class HttpUtils {

	public static String DEFAULT_CHARSET = "UTF-8";

	public static String getMimeTypeFromContentType(String contentType) {
		String result = "";
		MediaType mt = MediaType.parse(contentType);
		if (mt != null) {
			result = mt.getType() + "/" + mt.getSubtype();
		}

		return result;
	}

	public static String getCharsetFromContentType(String contentType) {
		String result = DEFAULT_CHARSET;
		MediaType mt = MediaType.parse(contentType);
		if (mt != null) {
			String charset = mt.getParameters().get("charset");
			if (charset != null) {
				result = charset;
			}
		}

		return result;
	}
}
