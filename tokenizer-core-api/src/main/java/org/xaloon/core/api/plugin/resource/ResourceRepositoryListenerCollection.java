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
package org.xaloon.core.api.plugin.resource;

import org.xaloon.core.api.config.ListenerCollection;
import org.xaloon.core.api.plugin.Plugin;

/**
 * Collection, containing list of {@link ResourceRepositoryListener} listeners. These listeners will be executed every time Resource value is stored
 * into repository.
 * <p>
 * Implementation of {@link PluginResourceRepository} interface is responsible to call this collection
 * 
 * @author vytautas r.
 */
public class ResourceRepositoryListenerCollection extends ListenerCollection<ResourceRepositoryListener> implements ResourceRepositoryListener {
	private static final long serialVersionUID = 1L;

	public void onBeforeSaveProperty(final Plugin plugin, final String propertyKey, final Object value) {
		notify(new Notifier<ResourceRepositoryListener>() {
			public void notify(ResourceRepositoryListener listener) {
				listener.onBeforeSaveProperty(plugin, propertyKey, value);
			}
		});
	}

	public void onAfterSaveProperty(final Plugin plugin, final String propertyKey) {
		notify(new Notifier<ResourceRepositoryListener>() {
			public void notify(ResourceRepositoryListener listener) {
				listener.onAfterSaveProperty(plugin, propertyKey);
			}
		});
	}
}
