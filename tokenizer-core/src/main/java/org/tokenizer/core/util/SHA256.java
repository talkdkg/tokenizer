/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * {@link http
 * ://stackoverflow.com/questions/221165/pros-and-cons-of-using-md5-hash-of-uri-as-the-primary-key-in-a-database}
 * 
 * Quote 1: It's perfectly fine. Accidental collision of MD5 is practically impossible (to get a 50% chance of collision
 * you'd have to hash 6 billion URLs per second, every second for 100 years). Quote 2: You still need to be aware that
 * deliberate MD5 hash collisions are easy to do. If this is going to be a problem you would be better off using SHA-1
 * or some other newer hash function that isn't as broken as MD5.
 * 
 * @author Fuad
 * 
 */
public class SHA256 {

	public static final String SHA256(byte[] bytes) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
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

	public static final String SHA256(String text) {
		try {
			return SHA256(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
