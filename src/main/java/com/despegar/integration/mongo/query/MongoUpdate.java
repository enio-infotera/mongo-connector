package com.despegar.integration.mongo.query;

import com.mongodb.BasicDBObject;

public class MongoUpdate {

    private final static String ID_FIELD = "id";
    private final static String MONGO_ID_FIELD = "_id";

    private final Update handlerUpdate;

    public MongoUpdate(final Update handlerUpdate) {
        this.handlerUpdate = handlerUpdate;
    }

    public BasicDBObject getUpdate() {
        BasicDBObject dbUpdate = new BasicDBObject();

        for (String key : this.handlerUpdate.getProperties().keySet()) {

            final Object pureValue = this.handlerUpdate.getProperties().get(key);
            final Object value = pureValue;

            if (ID_FIELD.equals(key)) {
                key = MONGO_ID_FIELD;
            }

            dbUpdate.append(key, value);
        }

        this.prependUpsateOperation(this.handlerUpdate, dbUpdate);

        return dbUpdate;
    }

    private BasicDBObject prependUpsateOperation(Update query, BasicDBObject dbUpdate) {
        if (query.getUpdateOperation() != null) {
            String updateOperation = null;
            switch (query.getUpdateOperation()) {
            case INC:
                updateOperation = "$inc";
                break;
            case SET:
                updateOperation = "$set";
                break;
            case UNSET:
                updateOperation = "$unset";
                break;
            case ADD_TO_SET:
                updateOperation = "$addToSet";
                break;
            case POP:
                updateOperation = "$pop";
                break;
            case PULL_ALL:
                updateOperation = "$pullAll";
                break;
            case PULL:
                updateOperation = "$pull";
                break;
            case PUSH:
                updateOperation = "$push";
                break;
            default:
                break;
            }
            if (updateOperation != null) {
                return new BasicDBObject(updateOperation, dbUpdate);
            }
        }
        return dbUpdate;
    }


}
