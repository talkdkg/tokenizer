package ca.t;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * {@link http://stackoverflow.com/questions/221165/pros-and-cons-of-using-md5-hash-of-uri-as-the-primary-key-in-a-database}
 * 
 * @author Fuad
 *
 */
public class SHA256 {

	public static final String SHA256(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(bytes);
		byte[] mdbytes = md.digest();

		// convert the byte to hex format
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			String hex = Integer.toHexString(0xff & mdbytes[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}


	public static final String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  {
		return SHA256(text.getBytes("UTF-8"));
	}

}