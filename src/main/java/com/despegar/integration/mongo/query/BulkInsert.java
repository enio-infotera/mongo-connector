package com.despegar.integration.mongo.query;

import com.despegar.integration.mongo.entities.Bulkeable;
import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;
import com.despegar.integration.mongo.query.BulkOperation;

public class BulkInsert<T extends GenericIdentifiableEntity<?>> implements Bulkeable{
	
	private T entity;
	
	public BulkInsert(T entity){
		this.entity = entity;
	}


	public T getEntity() {
		return entity;
	}


	@Override
	public BulkOperation getOperation() {
		return BulkOperation.INSERT;
	}
	
}
