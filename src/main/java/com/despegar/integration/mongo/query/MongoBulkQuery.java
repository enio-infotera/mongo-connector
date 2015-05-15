package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.List;

import com.despegar.integration.mongo.entities.Bulkeable;
import com.despegar.integration.mongo.query.BulkOperation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;


public class MongoBulkQuery {
	
	
	@SuppressWarnings("rawtypes")
	private Bulk bulk;
	
	class BulkUpdateOperation implements BulkOperable{
		private DBObject query;
		private DBObject update;
		
		public BulkUpdateOperation(DBObject query, DBObject update) {
			this.query = query;
			this.update = update;
		}
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			bulk.find(query).update(update);
		}
	}
	
	class BulkUpdateOneOperation implements BulkOperable{
		private DBObject query;
		private DBObject update;
		
		public BulkUpdateOneOperation(DBObject query, DBObject update) {
			this.query = query;
			this.update = update;
		}
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			bulk.find(query).updateOne(update);
		}
	}
	
	class BulkReplaceOneOperation implements BulkOperable{
		private DBObject query;
		private Object o;
		
		public BulkReplaceOneOperation(DBObject query, Object o) {
			this.query = query;
			this.o = o;
		}
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			BasicDBObject entity = mapper.convertValue(o, BasicDBObject.class);
			bulk.find(query).replaceOne(entity);
		}
	}
	
	class BulkRemoveOperation implements BulkOperable {
		private DBObject query;
		
		public BulkRemoveOperation(DBObject query) {
			this.query = query;
		}
		
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			bulk.find(query).remove();
		}
	}
	
	class BulkRemoveOneOperation implements BulkOperable {
		private DBObject query;
		
		public BulkRemoveOneOperation(DBObject query) {
			this.query = query;
		}
		
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			bulk.find(query).removeOne();
		}
	}
	
	class BulkInsertOperation implements BulkOperable {
		private Object o;
		
		public BulkInsertOperation(Object o) {
			this.o = o;
		}
		
		@Override
		public void addTo(BulkWriteOperation bulk, ObjectMapper mapper) {
			BasicDBObject entity = mapper.convertValue(o, BasicDBObject.class);
			bulk.insert(entity);
		}
	}
	
	public interface BulkOperable {
		void addTo(BulkWriteOperation bulk, ObjectMapper mapper);
	}
	
	@SuppressWarnings("rawtypes")
	public MongoBulkQuery(Bulk bulk){
		this.bulk = bulk;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BulkOperable> getOperations(){
		List<BulkOperable> operationList = new ArrayList<BulkOperable>();
		List<Bulkeable> bulkList = bulk.getOperationsList();
		for(Bulkeable bulkeable:bulkList){
				BulkOperation operation = bulkeable.getOperation();
				BulkFind find = !BulkOperation.INSERT.equals(bulkeable.getOperation()) ? (BulkFind) bulkeable : null;
		        switch (operation) {
		        case REMOVE:
		        	BasicDBObject removeQuery = new MongoQuery(find.getQuery()).getQuery();
		            BulkRemoveOperation removeInstance = new BulkRemoveOperation(removeQuery);
		            operationList.add(removeInstance);
		            break;
		        case REMOVE_ONE:
		        	BasicDBObject removeOneQuery = new MongoQuery(find.getQuery()).getQuery();
		            BulkRemoveOneOperation removeOneInstance = new BulkRemoveOneOperation(removeOneQuery);
		            operationList.add(removeOneInstance);
		            break;
		        case UPDATE:
		        	BasicDBObject updateFindQuery = new MongoQuery(find.getQuery()).getQuery();
		        	BasicDBObject updateQuery = new MongoUpdate(find.getUpdate()).getUpdate();
		            BulkUpdateOperation updateInstance = new BulkUpdateOperation(updateFindQuery,updateQuery);
		            operationList.add(updateInstance);
		            break;
		        case UPDATE_ONE:
		        	BasicDBObject updateFindOneQuery = new MongoQuery(find.getQuery()).getQuery();
		        	BasicDBObject updateOneQuery = new MongoUpdate(find.getUpdate()).getUpdate();
		            BulkUpdateOneOperation updateOneInstance = new BulkUpdateOneOperation(updateFindOneQuery,updateOneQuery);
		            operationList.add(updateOneInstance);
		            break;
		        case REPLACE_ONE:
		        	BasicDBObject replaceFindOneQuery = new MongoQuery(find.getQuery()).getQuery();
		            BulkReplaceOneOperation replaceOneInstance = new BulkReplaceOneOperation(replaceFindOneQuery,find.getEntity());
		            operationList.add(replaceOneInstance);
		            break;
				case INSERT:
					BulkInsertOperation bulkInsertOperation = new BulkInsertOperation(((BulkInsert) bulkeable).getEntity());
					operationList.add(bulkInsertOperation);
					break;
		        }
			}
		return operationList;
	}
	
}
