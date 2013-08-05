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
package org.xaloon.core.api.config;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a collection of listeners. Facilitates invocation of events on each listener.
 * <p>
 * Kindly borrowed from Apache Wicket implementation.
 * 
 * @author vytautas r.
 * 
 * @param <T>
 *            type of listeners
 */
public abstract class ListenerCollection<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** list of listeners */
	private final List<T> listeners = new CopyOnWriteArrayList<T>();

	protected static interface Notifier<T> {
		void notify(T listener);
	}

	/**
	 * Notifies each listener in this
	 * 
	 * @param notifier
	 *            notifier used to notify each listener
	 */
	protected void notify(Notifier<T> notifier) {
		for (T listener : listeners) {
			notifier.notify(listener);
		}
	}

	/**
	 * Adds a listener to this set of listeners.
	 * 
	 * @param listener
	 *            The listener to add
	 * @return {@code true} if the listener was added
	 */
	public boolean add(final T listener) {
		if (listener == null) {
			return false;
		}
		if (listeners.contains(listener)) {
			return false;
		}
		listeners.add(listener);
		return true;
	}
}
