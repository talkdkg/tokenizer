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
package org.xaloon.core.impl.mount;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

/**
 * Class to get matching resources - uses Spring's {@link valueMatchingResourcePatternResolver}.
 * 
 * @see valueMatchingResourcePatternResolver
 * 
 * @author Doug Donohoe
 */
public class MatchingResources {
    private static Logger logger = LoggerFactory.getLogger(MatchingResources.class);

    private Resource[] resources;
    private String pattern;

    /**
     * Initialize list of matching {@link Resource} as found by
     * {@link valueMatchingResourcePatternResolver#getResources(String)}.
     * 
     * @param sPattern
     *            the pattern to search for
     * @see valueMatchingResourcePatternResolver
     */
    public MatchingResources(String sPattern) {
        pattern = sPattern.replace('\\', '/'); // fix DOS values
        PathMatchingResourcePatternResolver match = new PathMatchingResourcePatternResolver();
        try {
            resources = match.getResources(pattern);
            logger.debug("Found " + resources.length + " resource(s) for: " + pattern);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all matching resources
     * 
     * @return A {@link Resource} array of all matches. If no matches are found
     *         this is a zero-length array.
     */
    public Resource[] getAllMatches() {
        return resources;
    }

    /**
     * Get all matching resources as URLs.
     * 
     * @return {@link URL} array detemined by calling {@link #getURL(Resource)} on each resource.
     */
    public URL[] getAllMatchesURL() {
        URL urls[] = new URL[resources.length];
        for (int i = 0; i < resources.length; i++) {
            urls[i] = getURL(resources[i]);
        }
        return urls;
    }

    /**
     * Get all matching classes that are annotated with the given Annotation.
     * 
     * @param annotation
     *            an annotation class
     * @param includeSubclasses
     *            if true, this will also return classes whose superclass has
     *            the specified annotation
     * @return List of all classes that have the given annotation. List is empty
     *         if non matches found.
     */
    public List<Class<?>> getAnnotatedMatches(Class<? extends Annotation> annotation, boolean includeSubclasses) {
        List<Class<?>> matches = new ArrayList<Class<?>>();
        MetadataReaderFactory f = new SimpleMetadataReaderFactory();
        for (Resource r : resources) {
            MetadataReader meta = null;
            try {
                meta = f.getMetadataReader(r);
            } catch (IOException e) {
                throw new RuntimeException("Unable to get MetadataReader for " + r, e);
            }
            AnnotationMetadata anno = meta.getAnnotationMetadata();
            Set<String> types = anno.getAnnotationTypes();
            if (types.contains(annotation.getName())
                    || (includeSubclasses && anySuperHas(getClass(anno.getSuperClassName()), annotation))) {
                matches.add(getClass(anno.getClassName()));
            }
        }
        return matches;
    }

    /**
     * Get all matching classes that are annotated with the given Annotation.
     * Note that this method only returns those classes that actually contain
     * the annotation, and does not return classes whose superclass contains the
     * annotation.
     * 
     * @param annotation
     *            an annotation class
     * @return List of all classes that have the given annotation. List is empty
     *         if non matches found.
     * @see MatchingResources#getAnnotatedMatches(Class, boolean)
     */
    public List<Class<?>> getAnnotatedMatches(Class<? extends Annotation> annotation) {
        return getAnnotatedMatches(annotation, false);
    }

    private boolean anySuperHas(Class<?> clz, Class<? extends Annotation> annotation) {
        return clz != null && (clz.isAnnotationPresent(annotation) || anySuperHas(clz.getSuperclass(), annotation));
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a single matching resource. Throws an exception if multiple are
     * found. This is useful if you are expecting to find only one instance of
     * an item on the classpath.
     * 
     * @return The single matching {@link Resource}
     * @throws RuntimeException
     *             if more than one {@link Resource} was found
     */
    public Resource getSingleResource() {
        if (resources.length > 1) {
            throw new RuntimeException("Found more than one resource in classpath for " + pattern + ": " + toString());
        }

        if (resources.length == 0)
            return null;
        return resources[0];
    }

    /**
     * Similar to {@link #getSingleResource()}, but returns result as an {@link URL}.
     * 
     * @return The single matching {@link Resource} as an {@link URL}
     * @throws RuntimeException
     *             if more than one {@link Resource} was found
     */
    public URL getSingleResourceURL() {
        Resource r = getSingleResource();
        if (r == null)
            return null;
        return getURL(r);
    }

    /**
     * Get a single required matching resource. Throws an exception if zero or
     * multiple are found. This is useful if you are expecting to find one and
     * only one instance of an item on the classpath.
     * 
     * @return The single matching {@link Resource}
     * @throws RuntimeException
     *             if zero or more than one {@link Resource} was found
     */
    public Resource getSingleRequiredResource() {
        if (resources.length == 0) {
            throw new RuntimeException("Cound not find required resource for " + pattern);
        }

        if (resources.length > 1) {
            throw new RuntimeException("Found more than one resource in classpath for " + pattern + ": " + toString());
        }

        if (resources.length == 0)
            return null;
        return resources[0];
    }

    /**
     * Similar to {@link #getSingleRequiredResource()}, but returns result as an {@link URL}.
     * 
     * @return The single matching {@link Resource} as an {@link URL}
     * @throws RuntimeException
     *             if zero or more than one {@link Resource} was found
     */
    public URL getSingleRequiredResourceURL() {
        Resource r = getSingleRequiredResource();
        if (r == null)
            return null;
        return getURL(r);
    }

    /**
     * Get URL from resource.
     * 
     * @param r
     *            a {@link Resource}
     * @return its URL
     * @throws RuntimeException
     *             if {@link Resource#getURL()} throws {@link IOException}
     */
    public URL getURL(Resource r) {
        if (r == null)
            return null;
        try {
            return r.getURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return string representing all matching resouces as URLs
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Resource r : resources) {
            if (sb.length() > 0)
                sb.append('\n');
            sb.append(getURL(r).toString());
        }
        return sb.toString();
    }
}
