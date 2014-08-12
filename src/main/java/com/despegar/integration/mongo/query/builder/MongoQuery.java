package com.despegar.integration.mongo.query.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.OrderedMapIterator;

import com.despegar.integration.mongo.query.Query;
import com.despegar.integration.mongo.query.Query.ComparisonOperation;
import com.despegar.integration.mongo.query.Query.GeometryOperation;
import com.despegar.integration.mongo.query.Query.GeometrySpecifiers;
import com.despegar.integration.mongo.query.Query.MathOperation;
import com.despegar.integration.mongo.query.Query.OperationWithComparison;
import com.despegar.integration.mongo.query.Query.OperationWithGeospatialFunction;
import com.despegar.integration.mongo.query.Query.OperationWithMathFunction;
import com.despegar.integration.mongo.query.Query.OperationWithRange;
import com.despegar.integration.mongo.query.Query.OrderDirection;
import com.despegar.integration.mongo.query.Query.RangeOperation;
import com.despegar.integration.mongo.query.QueryPage;
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

    private String getGeometrySpecifier(final GeometrySpecifiers operation) {
        String geometrySpecifier = null;
        switch (operation) {
        case BOX:
            geometrySpecifier = "$box";
            break;
        case CENTER:
            geometrySpecifier = "$center";
            break;
        case CENTER_SPHERE:
            geometrySpecifier = "$centerSphere";
            break;
        case GEOMETRY:
            geometrySpecifier = "$geometry";
            break;
        case MAX_DISTANCE:
            geometrySpecifier = "$maxDistance";
            break;
        case POLYGON:
            geometrySpecifier = "$polygon";
            break;
        case UNIQUE_DOCS:
            geometrySpecifier = "$uniqueDocs";
            break;
        }

        return geometrySpecifier;
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
            final Map<GeometrySpecifiers, Object> specifiers = entry.getValue().getGeometrySpecifiers();

            String geometryOperation = this.getGeometryOperation(operation);
            DBObject specifierProperties = new BasicDBObject();
            for (Entry<GeometrySpecifiers, Object> specifier : specifiers.entrySet()) {
                String geometrySpecifier = this.getGeometrySpecifier(specifier.getKey());
                specifierProperties.put(geometrySpecifier, specifier.getValue());
            }

            DBObject geoClause = new BasicDBObject(geometryOperation, specifierProperties);

            if (entry.getValue().isNegation()) {
                geoClause = new BasicDBObject("$not", geoClause);
            }
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
