package com.despegar.integration.mongo.query;

import com.despegar.integration.mongo.entities.Bulkeable;
import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;

public class BulkFind<T extends GenericIdentifiableEntity<?>> implements Bulkeable{
	
	private Query query;
	private BulkFindOperation operation;
	private Update update;
	private T entity;
	
	enum BulkFindOperation{
		REMOVE,
		UPDATE,
		REMOVE_ONE,
		REPLACE_ONE,
		UPDATE_ONE;
	}
	
	public BulkFind(Query query){
		this.query = query;
	}
	
	
	public void remove(){
		this.operation = BulkFindOperation.REMOVE;
	}
	
	public void update(Update update){
		this.update = update;
		this.operation = BulkFindOperation.UPDATE;
	}
	
	public void removeOne(){
		this.operation = BulkFindOperation.REMOVE_ONE;
	}
	
	public void updateOne(Update update){
		this.update = update;
		this.operation = BulkFindOperation.UPDATE_ONE;
	}
	
	public void replaceOne(T entity){
		this.entity = entity;
		this.operation = BulkFindOperation.REPLACE_ONE;
	}
	
	public Query getQuery() {
		return query;
	}


	public BulkFindOperation getOperation() {
		return operation;
	}


	public Update getUpdate() {
		return update;
	}


	public T getEntity() {
		return entity;
	}
	
}
