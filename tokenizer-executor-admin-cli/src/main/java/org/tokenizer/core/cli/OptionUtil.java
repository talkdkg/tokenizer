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
package org.tokenizer.core.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OptionUtil {

    public static int getIntOption(final CommandLine cmd, final Option option, final int defaultValue) {
        if (cmd.hasOption(option.getOpt())) {
            try {
                return Integer.parseInt(cmd.getOptionValue(option.getOpt()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for option " + option.getLongOpt() + " : "
                        + cmd.getOptionValue(option.getOpt()));
                System.exit(1);
            }
        }
        return defaultValue;
    }
}
