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
package org.xaloon.core.api.plugin;

import javax.inject.Named;

/**
 * This plugin does not contain anything and is created only for default usage purposes
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.3
 */
@Named
public class EmptyPlugin extends AbstractPlugin<AbstractPluginBean> {
    private static final long serialVersionUID = 1L;
}
