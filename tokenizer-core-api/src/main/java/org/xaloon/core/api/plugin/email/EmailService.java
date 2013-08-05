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
package org.xaloon.core.api.plugin.email;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public interface EmailService extends Serializable {
    /**
     * Error key when email plugin is not properly configured
     */
    String EMAIL_PROPERTIES_NOT_CONFIGURED = "EMAIL_PROPERTIES_NOT_CONFIGURED";

    /**
     * @param emailContent
     * @param fromEmail
     * @param fromName
     * @return true if email sent successfully
     */
    boolean sendMailToSystem(String emailContent, String fromEmail, String fromName);

    /**
     * @param emailContent
     * @param subject
     * @param toEmail
     * @param toName
     * @return true if email sent successfully
     */
    boolean sendMailFromSystem(String emailContent, String subject, String toEmail, String toName);

    /**
     * 
     * @return true if email plugin is enabled
     */
    boolean isEnabled();

    /**
     * Returns email of the system administrator
     * 
     * @return system administrator's email
     */
    String getSystemEmail();
}
