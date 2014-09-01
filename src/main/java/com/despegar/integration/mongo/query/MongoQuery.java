package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.OrderedMapIterator;

import com.despegar.integration.mongo.query.Query.ComparisonOperation;
import com.despegar.integration.mongo.query.Query.GeometryOperation;
import com.despegar.integration.mongo.query.Query.GeometryType;
import com.despegar.integration.mongo.query.Query.MathOperation;
import com.despegar.integration.mongo.query.Query.OperationGeoNearFunction;
import com.despegar.integration.mongo.query.Query.OperationWithComparison;
import com.despegar.integration.mongo.query.Query.OperationWithGeospatialFunction;
import com.despegar.integration.mongo.query.Query.OperationWithMathFunction;
import com.despegar.integration.mongo.query.Query.OperationWithRange;
import com.despegar.integration.mongo.query.Query.OrderDirection;
import com.despegar.integration.mongo.query.Query.Point;
import com.despegar.integration.mongo.query.Query.RangeOperation;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoQuery {

    private final static String ID_FIELD = "id";
    private final static String MONGO_ID_FIELD = "_id";

    private final Query handlerQuery;

    public MongoQuery(final Query handlerQuery) {
        this.handlerQuery = handlerQuery;
    }

    public BasicDBObject getQuery() {

        BasicDBObject res;

        if (this.handlerQuery.getOrs().isEmpty()) {
            res = this.createQueryFromHandler(this.handlerQuery);
        } else {
            final List<Query> queries = new ArrayList<Query>();
            queries.add(this.handlerQuery);
            queries.addAll(this.handlerQuery.getOrs());


            final BasicDBList orComponents = new BasicDBList();
            for (final Query handlerQuery : queries) {
                orComponents.add(this.createQueryFromHandler(handlerQuery));
            }

            res = new BasicDBObject("$or", orComponents); // orRoot
        }

        return res;
    }

    private BasicDBObject createQueryFromHandler(final Query query) {
        BasicDBObject dbQuery = this.createQuery(query);
        this.appendOrQueries(query, dbQuery);
        return dbQuery;
    }

    private BasicDBObject createQuery(final Query query) {
        BasicDBObject dbQuery = new BasicDBObject();

        for (String key : query.getFilters().keySet()) {

            final Object pureValue = query.getFilters().get(key);
            final Object value = pureValue;
            if (value == null) {
                continue;
            }

            if (ID_FIELD.equals(key)) {
                key = MONGO_ID_FIELD;
            }

            if (value.getClass().isEnum()) {
                dbQuery.append(key, value.toString());
            } else {
                dbQuery.append(key, value);
            }

        }

        this.appendRangeOperations(query, dbQuery);
        this.appendComparisionOperations(query, dbQuery);
        this.appendMathOperations(query, dbQuery);
        this.appendGeometryOperations(query, dbQuery);

        return dbQuery;
    }


    private String getComparisonOperation(final ComparisonOperation operation) {
        String comparisonOperation = null;
        switch (operation) {
        case LESS:
            comparisonOperation = "$lt";
            break;
        case LESS_OR_EQUAL:
            comparisonOperation = "$lte";
            break;
        case GREATER:
            comparisonOperation = "$gt";
            break;
        case GREATER_OR_EQUAL:
            comparisonOperation = "$gte";
            break;
        case NOT_EQUAL:
            comparisonOperation = "$ne";
            break;
        case EXISTS:
            comparisonOperation = "$exists";
            break;
        }

        return comparisonOperation;
    }

    private String getRangeOperation(final RangeOperation operation) {
        String rangeOperation = null;
        switch (operation) {
        case IN:
            rangeOperation = "$in";
            break;
        case NOT_IN:
            rangeOperation = "$nin";
            break;
        case ALL:
            rangeOperation = "$all";
            break;
        }

        return rangeOperation;
    }

    private String getMathOperation(final MathOperation operation) {
        String mathOperation = null;
        switch (operation) {
        case MODULO:
            mathOperation = "$mod";
            break;
        }

        return mathOperation;
    }

    private String getGeometryOperation(final GeometryOperation operation) {
        String geometryOperation = null;
        switch (operation) {
        case WITH_IN:
            geometryOperation = "$geoWithin";
            break;
        case INTERSECTS:
            geometryOperation = "$geoIntersects";
            break;
        case NEAR:
            geometryOperation = "$near";
            break;
        case NEAR_SPHERE:
            geometryOperation = "$nearSphere";
            break;
        }

        return geometryOperation;
    }


    private String getGeometryType(final GeometryType type) {
        String geometryType = null;
        switch (type) {
        case POINT:
            geometryType = "Point";
            break;
        case POLYGON:
            geometryType = "Polygon";
            break;
        case LINE:
            geometryType = "LineString";
        }

        return geometryType;
    }

    private BasicDBObject appendComparisionOperations(final Query query, final BasicDBObject dbQuery) {
        final Set<Entry<String, OperationWithComparison>> comparison = query.getComparisonOperators().entrySet();

        for (final Entry<String, OperationWithComparison> entry : comparison) {
            final String key = entry.getKey();

            final List<OperationWithComparison> comparisions = new ArrayList<Query.OperationWithComparison>();
            final OperationWithComparison operationWithComparision = entry.getValue();
            comparisions.add(operationWithComparision);

            comparisions.addAll(operationWithComparision.getMoreComparisions());

            BasicDBObject comparisionComponents = new BasicDBObject();
            for (final OperationWithComparison eachOperation : comparisions) {
                comparisionComponents.append(this.getComparisonOperation(eachOperation.getOperation()),
                    eachOperation.getValue());
            }

            if (entry.getValue().isNegation()) {
                comparisionComponents = new BasicDBObject("$not", comparisionComponents);
            }
            dbQuery.append(key, comparisionComponents);
        }

        return dbQuery;
    }

    private void appendRangeOperations(final Query query, final BasicDBObject dbQuery) {
        final Set<Entry<String, OperationWithRange>> rangeOperations = query.getRangeOperators().entrySet();
        for (final Entry<String, OperationWithRange> entry : rangeOperations) {
            final String key = entry.getKey();
            final RangeOperation operation = entry.getValue().getCollectionOperation();
            final Collection<?> values = entry.getValue().getValues();

            final BasicDBList list = new BasicDBList();
            list.addAll(values);
            DBObject rangeClause = new BasicDBObject(this.getRangeOperation(operation), list);

            if (entry.getValue().isNegation()) {
                rangeClause = new BasicDBObject("$not", rangeClause);
            }
            dbQuery.append(key, rangeClause);
        }
    }

    private void appendGeometryOperations(final Query query, final BasicDBObject dbQuery) {
        final Set<Entry<String, OperationWithGeospatialFunction>> geoOperations = query.getGeospatialOperators().entrySet();

        for (final Entry<String, OperationWithGeospatialFunction> entry : geoOperations) {
            final String key = entry.getKey();
            final GeometryOperation operation = entry.getValue().getGeometryOperation();

            Point[] points = entry.getValue().getCoordinates();
            DBObject operationObject = new BasicDBObject();

            final GeometryType type = entry.getValue().getType();
            String geometryType = this.getGeometryType(type);

            DBObject specifierObject = new BasicDBObject();
            specifierObject.put("type", geometryType);

            specifierObject.put("coordinates", this.getGeometry(type, points));
            operationObject.put("$geometry", specifierObject);

            if (entry.getValue() instanceof OperationGeoNearFunction) {
                OperationGeoNearFunction operationFunction = (OperationGeoNearFunction) entry.getValue();
                if (operationFunction.getMaxDistance() != null) {
                    operationObject.put("$maxDistance", operationFunction.getMaxDistance());
                }
                if (operationFunction.getMinDistance() != null) {
                    operationObject.put("$minDistance", operationFunction.getMaxDistance());
                }
            }

            String geometryOperation = this.getGeometryOperation(operation);

            DBObject geoClause = new BasicDBObject();
            geoClause.put(geometryOperation, operationObject);
            dbQuery.append(key, geoClause);
        }
    }

    private void appendMathOperations(final Query query, final BasicDBObject dbQuery) {
        final Set<Entry<String, OperationWithMathFunction>> mathOperations = query.getMathOperators().entrySet();
        for (final Entry<String, OperationWithMathFunction> entry : mathOperations) {
            final String key = entry.getKey();
            final MathOperation operation = entry.getValue().getMathOperation();
            final Object values = entry.getValue().getValues();

            DBObject mathClause = new BasicDBObject(this.getMathOperation(operation), values);

            if (entry.getValue().isNegation()) {
                mathClause = new BasicDBObject("$not", mathClause);
            }
            dbQuery.append(key, mathClause);
        }
    }

    private void appendOrQueries(final Query query, final BasicDBObject dbQuery) {
        if (!this.handlerQuery.getAndOrs().isEmpty()) {
            final BasicDBList orComponents = new BasicDBList();
            for (final Query handlerQuery : this.handlerQuery.getAndOrs()) {
                orComponents.add(this.createQuery(handlerQuery));
            }
            dbQuery.append("$or", orComponents);
        }
    }

    public DBObject getSortInfo() {
        if (!this.handlerQuery.getOrderFields().isEmpty()) {
            // use a set of fields and order directions0
            final DBObject sortInfo = new BasicDBObject(this.handlerQuery.getOrderFields().size());

            final OrderedMapIterator orderedMapIterator = this.handlerQuery.getOrderFields().orderedMapIterator();
            while (orderedMapIterator.hasNext()) {
                final String key = (String) orderedMapIterator.next();
                final OrderDirection orderDir = (OrderDirection) orderedMapIterator.getValue();
                sortInfo.put(key, this.getOrderDir(orderDir));
            }

            return sortInfo;

        }
        return null;

    }

    @SuppressWarnings("unchecked")
    private Object getGeometry(GeometryType type, Point... points) {
        switch (type) {
        case POINT:
            return this.getCoordenate(points[0]);
        case LINE:
            return this.getCoordinates(points);
        case POLYGON:
            return Arrays.asList(this.getCoordinates(points));
        }
        return null;
    }

    private Collection<Double[]> getCoordinates(Point... points) {
        Collection<Double[]> collection = new ArrayList<Double[]>();
        for (Point point : points) {
            collection.add(this.getCoordenate(point));
        }

        return collection;
    }

    private Double[] getCoordenate(Point point) {
        Double[] coordinates = {point.getLongitude(), point.getLatitude()};
        return coordinates;
    }

    public QueryPage getQueryPage() {
        return new QueryPage(this.handlerQuery.getSkip(), this.handlerQuery.getLimit());
    }

    // no pongo los valores en el enum para no acoplar estos valores de mongo a un enum genérico
    private int getOrderDir(final OrderDirection orderDir) {

        if (Query.OrderDirection.ASC.equals(orderDir)) {
            return 1;
        }

        if (Query.OrderDirection.DESC.equals(orderDir)) {
            return -1;
        }

        return 0;
    }

}
