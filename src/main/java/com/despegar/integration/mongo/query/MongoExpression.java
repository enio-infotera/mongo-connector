package com.despegar.integration.mongo.query;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoExpression {

    public static DBObject getExpressionDBObject(Expression expression) {

        switch (expression.getOperator()) {
        case CONDITIONAL:
            return getCollection("$cond", expression.getParameters());
        case SIZE:
            return getUnary("$size", expression.getParameters());
        case AVG:
            return getUnary("$avg", expression.getParameters());
        case SUM:
            return getUnary("$sum", expression.getParameters());
        case SET_INTERSECTION:
            return getCollection("$setIntersection", expression.getParameters());
        case ADD:
            return getCollection("$add", expression.getParameters());
        case ADD_TO_SET:
            return getUnary("$addToSet", expression.getParameters());
        case DAY:
            return getUnary("$dayOfMonth", expression.getParameters());
        case DIVIDE:
            return getCollection("$divide", expression.getParameters());
        case EQUAL:
            return getCollection("$eq", expression.getParameters());
        case FIRST:
            return getUnary("$first", expression.getParameters());
        case GREAT_THAN:
            return getCollection("$gt", expression.getParameters());
        case GREAT_THAN_EQUAL:
            return getCollection("$gte", expression.getParameters());
        case HOUR:
            return getUnary("$hour", expression.getParameters());
        case LAST:
            return getUnary("$last", expression.getParameters());
        case LESS_THAN:
            return getCollection("$lt", expression.getParameters());
        case LESS_THAN_EQUAL:
            return getCollection("$lte", expression.getParameters());
        case MAX:
            return getUnary("$max", expression.getParameters());
        case MIN:
            return getUnary("$min", expression.getParameters());
        case MINUTES:
            return getUnary("$minute", expression.getParameters());
        case MONTH:
            return getUnary("$month", expression.getParameters());
        case MULTIPLY:
            return getCollection("$multiply", expression.getParameters());
        case NON_EQUAL:
            return getCollection("$ne", expression.getParameters());
        case PUSH:
            return getUnary("$push", expression.getParameters());
        case SUBTRACT:
            return getCollection("$subtract", expression.getParameters());
        case YEAR:
            return getUnary("$year", expression.getParameters());
        }

        return null;
    }

    private static DBObject getCollection(String operator, Object... parameters) {
        BasicDBList list = new BasicDBList();

        for (Object param : parameters) {
            list.add(resolveObjects(param));
        }

        return new BasicDBObject(operator, list);
    }

    private static DBObject getUnary(String operator, Object... parameters) {
        return new BasicDBObject(operator, resolveObjects(parameters[0]));
    }

    public static Object resolveObjects(Object parameter) {
        if (parameter instanceof Expression) {
            return getExpressionDBObject((Expression) parameter);
        }

        return parameter;
    }

}
