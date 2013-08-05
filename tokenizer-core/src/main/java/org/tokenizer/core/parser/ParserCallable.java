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
package org.tokenizer.core.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.DefaultHtmlMapper;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.sax.TeeContentHandler;
import org.slf4j.Logger;
import org.tokenizer.core.datum.Outlink;
import org.tokenizer.core.datum.ParsedDatum;

class ParserCallable implements Callable<ParsedDatum> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ParserCallable.class);

    private static class CustomHtmlMapper extends DefaultHtmlMapper {

        private Set<String> validTags;
        private Set<String> validAttributes;

        public CustomHtmlMapper(Set<String> validTags, Set<String> validAttributes) {
            this.validTags = validTags;
            this.validAttributes = validAttributes;
        }

        @Override
        public String mapSafeElement(String name) {
            if (validTags.contains(name.toLowerCase())) {
                return name.toLowerCase();
            }
            else {
                return super.mapSafeElement(name);
            }
        }

        @Override
        public String mapSafeAttribute(String elementName, String attributeName) {
            // TODO KKr - really the _validAttributes should be a map from
            // element name
            // to a list of valid attributes, but that's not how it's
            // implemented currently.
            // So we blindly assume that if the attribute exists, it's valid.

            if (validAttributes.contains(attributeName)) {
                return attributeName;
            }
            else {
                return super.mapSafeAttribute(elementName, attributeName);
            }
        }
    }

    // Simplistic language code pattern used when there are more than one
    // languages specified
    // FUTURE KKr - improve this to handle en-US, and "eng" for those using
    // old-style language codes.
    private static final Pattern LANGUAGE_CODE_PATTERN = Pattern.compile("([a-z]{2})([,;-]).*");

    private Parser _parser;
    private BaseContentExtractor _contentExtractor;
    private BaseLinkExtractor _linkExtractor;
    private InputStream _input;
    private Metadata _metadata;
    private boolean _extractLanguage;

    public ParserCallable(Parser parser, BaseContentExtractor contentExtractor, BaseLinkExtractor linkExtractor,
            InputStream input, Metadata metadata) {
        this(parser, contentExtractor, linkExtractor, input, metadata, true);
    }

    public ParserCallable(Parser parser, BaseContentExtractor contentExtractor, BaseLinkExtractor linkExtractor,
            InputStream input, Metadata metadata, boolean extractLanguage) {
        _parser = parser;
        _contentExtractor = contentExtractor;
        _linkExtractor = linkExtractor;
        _input = input;
        _metadata = metadata;
        _extractLanguage = extractLanguage;
    }

    @Override
    public ParsedDatum call() throws Exception {
        try {
            TeeContentHandler teeContentHandler;
            ProfilingHandler profilingHandler = null;

            if (_extractLanguage) {
                profilingHandler = new ProfilingHandler();
                teeContentHandler = new TeeContentHandler(_contentExtractor, _linkExtractor, profilingHandler);
            }
            else {
                teeContentHandler = new TeeContentHandler(_contentExtractor, _linkExtractor);
            }
            _parser.parse(_input, teeContentHandler, _metadata, makeParseContext());

            String lang = _extractLanguage ? detectLanguage(_metadata, profilingHandler) : "";

            if (LOG.isDebugEnabled()) {
                for (Outlink o : _linkExtractor.getLinks()) {
                    LOG.debug("Outlink extracted: {}", o);
                }
            }

            return new ParsedDatum(_metadata.get(Metadata.RESOURCE_NAME_KEY), null, _contentExtractor.getContent(),
                    lang, _metadata.get(Metadata.TITLE), _linkExtractor.getLinks(), makeMap(_metadata));
        } catch (Exception e) {
            // Generic exception that's OK to re-throw
            throw e;
        } catch (NoSuchMethodError e) {
            throw new RuntimeException("Attempting to use excluded parser");
        } catch (Throwable t) {
            // Make sure nothing inside Tika can kill us
            throw new RuntimeException("Serious shut-down error thrown from Tika", t);
        }
    }

    /**
     * Decide if we need to set up our own HtmlMapper, because the link extractor has tags that aren't part of the
     * default set.
     * 
     * @return
     */
    private ParseContext makeParseContext() {
        ParseContext result = new ParseContext();

        Set<String> validTags = _linkExtractor.getLinkTags();
        HtmlMapper defaultMapper = DefaultHtmlMapper.INSTANCE;
        for (String tag : validTags) {
            if (defaultMapper.mapSafeElement(tag) == null) {
                result.set(HtmlMapper.class, new CustomHtmlMapper(validTags, _linkExtractor.getLinkAttributeTypes()));
                break;
            }
        }

        return result;
    }

    /**
     * See if a language was set by the parser, from meta tags. As a last resort falls back to the result from the
     * ProfilingHandler.
     * 
     * @param metadata
     * @param profilingHandler
     * @return The first language found (two char lang code) or empty string if no language was detected.
     */
    private static String detectLanguage(Metadata metadata, ProfilingHandler profilingHandler) {
        String result = null;

        String dubCoreLang = metadata.get(Metadata.LANGUAGE);
        String httpEquivLang = metadata.get(Metadata.CONTENT_LANGUAGE);

        if (dubCoreLang != null) {
            result = dubCoreLang;
        }
        else if (httpEquivLang != null) {
            result = httpEquivLang;
        }

        result = getFirstLanguage(result);

        if (result == null) {
            // Language is still unspecified, so use ProfileHandler's result
            LanguageIdentifier langIdentifier = profilingHandler.getLanguage();
            // FUTURE KKr - provide config for specifying required certainty
            // level.
            if (langIdentifier.isReasonablyCertain()) {
                result = langIdentifier.getLanguage();
                LOG.trace("Using language specified by profiling handler: " + result);
            }
            else {
                result = "";
            }

        }

        return result;
    }

    private static Map<String, String> makeMap(Metadata metadata) {
        Map<String, String> result = new HashMap<String, String>();

        for (String key : metadata.names()) {
            result.put(key, metadata.get(key));
        }

        return result;
    }

    private static String getFirstLanguage(String lang) {
        if (lang != null && lang.length() > 0) {
            // TODO VMa -- DublinCore languages could be specified in a multiple
            // of ways
            // see :
            // http://dublincore.org/documents/2000/07/16/usageguide/qualified-html.shtml#language
            // This means that it is possible to get back 3 character language
            // strings as per ISO639-2
            // For now, we handle just two character language strings and if we
            // do get a 3 character string we
            // treat it as a "null" language.

            // TODO VMa - what if the length is just one char ?
            if (lang.length() > 2) {
                Matcher m = LANGUAGE_CODE_PATTERN.matcher(lang);

                if (m.matches()) {
                    lang = m.group(1);
                }
                else {
                    lang = null;
                }
            }
        }
        return lang;
    }

}
