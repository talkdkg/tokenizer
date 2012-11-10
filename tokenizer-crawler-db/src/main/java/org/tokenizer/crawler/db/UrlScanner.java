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
import java.util.Arrays;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

public class UrlScanner extends AbstractHBaseRecordScanner<UrlRecord> {
    private ResultScanner hbaseScanner;

    /**
     * 
     * @param host
     *            - internet hos such as www.amazon.ca (without "dot" at the
     *            end)
     */
    public UrlScanner(String host, CrawlerHBaseRepository repository) {
        byte[] start = UrlRecordDecoder.encode("http://" + host);
        byte[] end = Arrays.copyOf(start, start.length + 1);
        end[start.length] = (byte) 0xff;
        Scan scan = new Scan(start, end);
        try {
            hbaseScanner = repository.getUrlTable().getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    UrlRecord decode(Result result) throws RepositoryException,
            InterruptedException {
        return CrawlerHBaseRepository.decodeUrlRecord(result);
    }

    @Override
    ResultScanner getScanner() {
        return hbaseScanner;
    }
}
