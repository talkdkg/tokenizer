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
package org.tokenizer.ui.v7.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.data.Property;

/**
 * Annotation used for marking a Vaadin UI component in the demo app as needing
 * I18N translation. The parameters provide the keys for I18N lookup. All
 * parameters are optional, but the value parameter is relevant only for those
 * components which implement {@link Property}. Its value would be ignored
 * otherwise
 * 
 * @see https://sites.google.com/site/q3cjava/internationalisation-i18n
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DemoI18N {
    DemoLabelKeys caption() default DemoLabelKeys._nullkey_;

    DemoLabelKeys description() default DemoLabelKeys._nullkey_;

    /**
     * Usually only used with Vaadin Labels
     * 
     * @return
     */
    DemoLabelKeys value() default DemoLabelKeys._nullkey_;
}
