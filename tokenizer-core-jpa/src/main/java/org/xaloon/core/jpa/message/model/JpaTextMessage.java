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
package org.xaloon.core.jpa.message.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.xaloon.core.api.classifier.ClassifierItem;
import org.xaloon.core.api.message.model.TextMessage;
import org.xaloon.core.jpa.classifier.model.JpaClassifierItem;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_TEXT_MESSAGE")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "MESSAGE_TYPE", discriminatorType = DiscriminatorType.STRING)
public class JpaTextMessage extends JpaMessage implements TextMessage {
	private static final long serialVersionUID = 1L;


	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "CLASSIFIER_ITEM_FOLDER_ID", referencedColumnName = "ID")
	private JpaClassifierItem folder;

	@Column(name = "SUBJECT")
	private String subject;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ClassifierItem getFolder() {
		return folder;
	}

	/**
	 * @param folder
	 */
	public void setFolder(JpaClassifierItem folder) {
		this.folder = folder;
	}

	@Override
	public void setFolder(ClassifierItem messageFolder) {
		folder = (JpaClassifierItem)messageFolder;
	}
}
