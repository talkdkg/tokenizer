package org.tokenizer.core.util;

import org.tokenizer.core.TokenizerConfig;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;


public class LanguageDetector {

    static {
        try {
            DetectorFactory.loadProfile(TokenizerConfig.getHome() + "/profiles");
        } catch (LangDetectException e) {
            throw new RuntimeException(e);
        }
    }


    public static String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }

}
