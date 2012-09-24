/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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