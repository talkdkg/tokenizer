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
package org.tokenizer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtil {

    public static boolean promptYesNo(final String message,
            final boolean defaultInput) {
        String input = "";
        while (!input.equals("yes") && !input.equals("no")
                && !input.equals("y") && !input.equals("n")) {
            input = prompt(message, defaultInput ? "yes" : "no");
            input = input.toLowerCase();
        }
        return (input.equals("yes") || input.equals("y"));
    }

    public static String prompt(final String message, final String defaultInput) {
        System.out.println(message);
        System.out.flush();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input;
        try {
            input = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading input from console.", e);
        }
        if (input == null || input.trim().equals("")) {
            input = defaultInput;
        }
        return input;
    }
}
