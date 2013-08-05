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
package org.xaloon.core.api.search;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public abstract class SearchRequest implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private long firstRow = -1;

    private long maxRowCount = -1;

    /**
     * @return start position to search for items. -1,0 - search from start position
     */
    public long getFirstRow() {
        return firstRow;
    }

    /**
     * @param firstRow
     */
    public void setFirstRow(long firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * @return maximum count of items to return. -1 - return all
     */
    public long getMaxRowCount() {
        return maxRowCount;
    }

    /**
     * @param maxRowCount
     */
    public void setMaxRowCount(long maxRowCount) {
        this.maxRowCount = maxRowCount;
    }
}
