/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoUtils {

	private static final Logger LOG = LoggerFactory.getLogger(IoUtils.class);

	public static void safeClose(InputStream is) {
		if (is == null) {
			return;
		}

		try {
			is.close();
		} catch (IOException e) {
			LOG.warn("IOException closing input stream", e);
		}
	}

	public static void safeClose(OutputStream os) {
		if (os == null) {
			return;
		}

		try {
			os.close();
		} catch (IOException e) {
			LOG.warn("IOException closing input stream", e);
		}
	}

	/**
	 * Read one line of input from the console.
	 * 
	 * @return Text that the user entered
	 * @throws IOException
	 */
	public static String readInputLine() throws IOException {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		return br.readLine();
	}

}
