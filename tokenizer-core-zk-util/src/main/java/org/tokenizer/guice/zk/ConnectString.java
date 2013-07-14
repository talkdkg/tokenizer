package org.tokenizer.guice.zk;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.google.inject.BindingAnnotation;

@Retention(RUNTIME)
@BindingAnnotation
public @interface ConnectString {

}
