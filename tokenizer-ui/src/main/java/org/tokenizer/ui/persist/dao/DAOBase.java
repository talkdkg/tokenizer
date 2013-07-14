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
package org.tokenizer.ui.persist.dao;

import java.util.List;

/**
 * T is the entity class
 * 
 * @author David Sowerby 29 Jan 2013
 * 
 * @param <T>
 */
public interface DAOBase<T> {

	T save(T entity);

	void commit();

	void close();

	T newEntity();

	/**
	 * Returns The object representing the entity id. This has to be an Object so that the DAO can be non-implementation
	 * specific, but if you call this method make sure the recordId is of a valid type for the implementation
	 * 
	 * @param entity
	 *            for which you want the identity
	 * @return
	 */

	Object getIdentity(T entity);

	T load(Object identity);

	List<T> findAll();

}
