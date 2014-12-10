package com.vssnake.gpswear.config;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by vssnake on 29/10/2014.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {
}