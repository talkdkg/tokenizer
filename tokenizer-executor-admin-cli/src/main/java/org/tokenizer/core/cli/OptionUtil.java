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
package org.tokenizer.core.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OptionUtil {

    public static int getIntOption(final CommandLine cmd, final Option option,
            final int defaultValue) {
        if (cmd.hasOption(option.getOpt())) {
            try {
                return Integer.parseInt(cmd.getOptionValue(option.getOpt()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for option "
                        + option.getLongOpt() + " : "
                        + cmd.getOptionValue(option.getOpt()));
                System.exit(1);
            }
        }
        return defaultValue;
    }
}
