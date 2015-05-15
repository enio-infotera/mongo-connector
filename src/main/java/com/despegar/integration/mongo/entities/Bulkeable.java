package com.despegar.integration.mongo.entities;

import com.despegar.integration.mongo.query.BulkOperation;


public interface Bulkeable {
	
	BulkOperation getOperation();
	
}
