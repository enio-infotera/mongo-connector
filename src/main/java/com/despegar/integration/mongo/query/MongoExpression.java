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
