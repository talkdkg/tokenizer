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
package org.xaloon.core.api.plugin.comment;

import org.xaloon.core.api.plugin.AbstractPluginBean;

/**
 * Configuration bean for commenting plugin
 * 
 * @author vytautas r.
 */
public class CommentPluginBean extends AbstractPluginBean {
    private static final long serialVersionUID = 1L;

    private boolean websiteVisible;

    private boolean applyByAdministrator = true;

    private boolean allowPostForAnonymous;

    private boolean sendEmail = true;

    private boolean enableCaptcha = true;

    /**
     * @return true if website is publicly available
     */
    public boolean isWebsiteVisible() {
        return websiteVisible;
    }

    /**
     * @param websiteVisible
     */
    public void setWebsiteVisible(boolean websiteVisible) {
        this.websiteVisible = websiteVisible;
    }

    /**
     * @return true if administrator needs to apply comments
     */
    public boolean isApplyByAdministrator() {
        return applyByAdministrator;
    }

    /**
     * @param applyByAdministrator
     */
    public void setApplyByAdministrator(boolean applyByAdministrator) {
        this.applyByAdministrator = applyByAdministrator;
    }

    /**
     * @return true if anonymous posts are allowed
     */
    public boolean isAllowPostForAnonymous() {
        return allowPostForAnonymous;
    }

    /**
     * @param allowPostForAnonymous
     */
    public void setAllowPostForAnonymous(boolean allowPostForAnonymous) {
        this.allowPostForAnonymous = allowPostForAnonymous;
    }

    /**
     * @return true if email should be sent to administrator
     */
    public boolean isSendEmail() {
        return sendEmail;
    }

    /**
     * @param sendEmail
     */
    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    /**
     * @return true if captcha should be used by commenting plugin
     */
    public boolean isEnableCaptcha() {
        return enableCaptcha;
    }

    /**
     * @param enableCaptcha
     */
    public void setEnableCaptcha(boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }
}
