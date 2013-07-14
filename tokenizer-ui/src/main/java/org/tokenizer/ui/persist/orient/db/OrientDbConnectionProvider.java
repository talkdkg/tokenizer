/*
 * Copyright 2013 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.ui.persist.orient.db;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.persist.orient.custom.OrientCustomType_DateTime;
import org.tokenizer.ui.persist.orient.custom.OrientCustomType_Locale;

import uk.co.q3c.v7.base.config.ConfigUtil;

import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

/**
 * There seemed no benefit to making this a Guice provider - it is only ever used by one class.
 */

public class OrientDbConnectionProvider {
	private static Logger log = LoggerFactory.getLogger(OrientDbConnectionProvider.class);
	private final String dbURL;
	private boolean initialised = false;
	private final String pwd;
	private final String user;

	@Inject
	protected OrientDbConnectionProvider(@DbURL String dbURL, @DbUser String user, @DbPwd String pwd) {
		super();
		this.dbURL = ConfigUtil.orientFilePathWithExpandedVariable(dbURL);
		this.user = user;
		this.pwd = pwd;
	}

	public OObjectDatabaseTx get() {
		if (!initialised) {
			initialise();
		}

		OObjectDatabaseTx db = OObjectDatabasePool.global().acquire(dbURL, user, pwd);
		log.debug("acquired connection to OrientDB database at " + dbURL);
		return db;
	}

	private void initialise() {
		OObjectDatabaseTx db = new OObjectDatabaseTx(dbURL);
		if (!db.exists()) {
			db.create();
			log.debug("created OrientDB database at " + dbURL);
		}
		OObjectSerializerContext serializerContext = new OObjectSerializerContext();
		serializerContext.bind(new OrientCustomType_DateTime());
		serializerContext.bind(new OrientCustomType_Locale());
		OObjectSerializerHelper.bindSerializerContext(null, serializerContext);
		initialised = true;
	}

}
