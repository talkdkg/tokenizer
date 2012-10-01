/*
 * Copyright 2007-2012 Tokenizer Inc.
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
package org.tokenizer.crawler.db;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

abstract class AbstractHBaseRecordScanner<T> implements RecordScanner<T> {
  
  abstract T decode(Result result) throws RepositoryException,
      InterruptedException;
  
  abstract ResultScanner getScanner();
  
  public T next() throws RepositoryException, InterruptedException {
    Result result;
    try {
      result = getScanner().next();
    } catch (IOException e) {
      throw new RepositoryException(e);
    }
    
    if (result == null) {
      // no more results
      return null;
    }
    
    return decode(result);
  }
  
  public void close() {
    getScanner().close();
  }
  
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private T next;
      
      @Override
      public boolean hasNext() {
        if (next != null) {
          return true;
        } else {
          try {
            next = AbstractHBaseRecordScanner.this.next();
          } catch (RepositoryException e) {
            throw new RuntimeException(e);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
          }
          return next != null;
        }
      }
      
      @Override
      public T next() {
        if (!hasNext()) {
          return null;
        }
        
        T result = next;
        next = null;
        return result;
      }
      
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
