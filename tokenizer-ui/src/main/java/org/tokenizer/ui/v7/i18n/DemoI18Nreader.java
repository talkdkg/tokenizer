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

import java.lang.annotation.Annotation;

import uk.co.q3c.v7.i18n.I18NAnnotationReader;
import uk.co.q3c.v7.i18n.I18NKeys;

public class DemoI18Nreader implements I18NAnnotationReader {

    @Override
    public I18NKeys<?> caption(Annotation annotation) {
        return ((DemoI18N) annotation).caption();
    }

    @Override
    public I18NKeys<?> description(Annotation annotation) {
        return ((DemoI18N) annotation).description();
    }

    @Override
    public I18NKeys<?> value(Annotation annotation) {
        return ((DemoI18N) annotation).value();
    }
}
