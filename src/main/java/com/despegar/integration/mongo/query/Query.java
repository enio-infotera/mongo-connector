package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;

public class Query {

    public static class Point {
        private Double latitude;
        private Double longitude;

        public Point(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return this.latitude;
        }

        public Double getLongitude() {
            return this.longitude;
        }
    }

    public static enum OrderDirection {
        ASC, DESC
    }

    public static enum GeometryType {
        POINT, POLYGON, LINE
    }

    static enum RangeOperation {
        IN, NOT_IN, ALL
    }

    static enum MathOperation {
        MODULO
    }

    static enum ComparisonOperation {
        GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL, EXISTS
    }

    static enum GeometryOperation {
        WITH_IN, INTERSECTS, NEAR, NEAR_SPHERE
    }

    private Collection<OperationWithComparison> comparisonOperators = new ArrayList<OperationWithComparison>();
    private Collection<OperationWithRange> rangeOperators = new ArrayList<OperationWithRange>();
    private Collection<OperationWithGeospatialFunction> geospatialOperators = new ArrayList<OperationWithGeospatialFunction>();
    private Collection<OperationWithMathFunction> mathOperators = new ArrayList<OperationWithMathFunction>();
    private Collection<OperationEqual> filters = new ArrayList<OperationEqual>();
    private OrderedMap orderFields = new ListOrderedMap();
    private Boolean crucialDataIntegration = Boolean.FALSE;

    private List<Query> ors = new ArrayList<Query>();
    private List<Query> andOrs = new ArrayList<Query>();

    private Integer limit = null;
    private Integer skip = 0;

    public Query() {
    }

    public Query equals(String property, Object value) {
        if (property != null) {
            this.getFilters().add(new OperationEqual(property, value));
        }

        return this;
    }

    public Query putAll(Map<String, Object> filters) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            this.equals(entry.getKey(), entry.getValue());
        }

        return this;
    }

    private void put(String key, RangeOperation operator, Collection<?> values, boolean negation) {
        if (key == null) {
            return;
        }

        this.getRangeOperators().add(new OperationWithRange(key, operator, values, negation));
    }

    public Query in(String property, Collection<?> values) {
        this.put(property, RangeOperation.IN, values, Boolean.FALSE);
        return this;
    }

    public Query in(String property, Collection<?> values, Boolean negation) {
        this.put(property, RangeOperation.IN, values, negation);
        return this;
    }

    public Query notIn(String property, Collection<?> values) {
        this.put(property, RangeOperation.NOT_IN, values, Boolean.FALSE);
        return this;
    }

    public Query notIn(String property, Collection<?> values, Boolean negation) {
        this.put(property, RangeOperation.NOT_IN, values, negation);
        return this;
    }

    public Query all(String property, Collection<?> values) {
        this.put(property, RangeOperation.ALL, values, Boolean.FALSE);
        return this;
    }

    public Query all(String property, Collection<?> values, Boolean negation) {
        this.put(property, RangeOperation.ALL, values, negation);
        return this;
    }

    private void put(String key, ComparisonOperation operator, Object value, boolean negation) {
        if (key == null) {
            return;
        }
        this.getComparisonOperators().add(new OperationWithComparison(key, operator, value, negation));
    }

    public Query greater(String property, Object value, Boolean negation) {
        this.put(property, ComparisonOperation.GREATER, value, negation);
        return this;
    }

    public Query greater(String property, Object value) {
        this.put(property, ComparisonOperation.GREATER, value, Boolean.FALSE);
        return this;
    }

    public Query greaterOrEqual(String property, Object value, Boolean negation) {
        this.put(property, ComparisonOperation.GREATER_OR_EQUAL, value, negation);
        return this;
    }

    public Query greaterOrEqual(String property, Object value) {
        this.put(property, ComparisonOperation.GREATER_OR_EQUAL, value, Boolean.FALSE);
        return this;
    }

    public Query less(String property, Object value, Boolean negation) {
        this.put(property, ComparisonOperation.LESS, value, negation);
        return this;
    }

    public Query less(String property, Object value) {
        this.put(property, ComparisonOperation.LESS, value, Boolean.FALSE);
        return this;
    }

    public Query lessOrEqual(String property, Object value, Boolean negation) {
        this.put(property, ComparisonOperation.LESS_OR_EQUAL, value, negation);
        return this;
    }

    public Query lessOrEqual(String property, Object value) {
        this.put(property, ComparisonOperation.LESS_OR_EQUAL, value, Boolean.FALSE);
        return this;
    }

    public Query notExists(String property) {
        this.put(property, ComparisonOperation.EXISTS, 0, Boolean.FALSE);
        return this;
    }

    public Query exists(String property) {
        this.put(property, ComparisonOperation.EXISTS, 1, Boolean.FALSE);
        return this;
    }

    public Query notEqual(String property, Object value) {
        this.put(property, ComparisonOperation.NOT_EQUAL, value, Boolean.FALSE);
        return this;
    }

    private void put(String key, MathOperation operator, Object value, Boolean negation) {
        if (key == null) {
            return;
        }
        this.getMathOperators().add(new OperationWithMathFunction(key, operator, value, negation));
    }

    public Query mod(String property, Integer divisor, Integer remainder, Boolean negation) {
        this.put(property, MathOperation.MODULO, Arrays.asList(divisor, remainder), negation);
        return this;
    }

    public Query mod(String property, Integer divisor, Integer remainder) {
        this.put(property, MathOperation.MODULO, Arrays.asList(divisor, remainder), Boolean.FALSE);
        return this;
    }

    public Query near(String property, Point point) {
        this.near(property, point, null, null);
        return this;
    }

    public Query near(String property, Point point, Double maxDistance) {
        this.near(property, point, maxDistance, null);
        return this;
    }

    public Query near(String property, Point point, Double maxDistance, Double minDistance) {
        if (property != null) {
            this.getGeospatialOperators().add(
                new OperationGeoNearFunction(property, Boolean.FALSE, maxDistance, minDistance, point));
        }
        return this;
    }

    public Query nearSphere(String property, Point point) {
        this.nearSphere(property, point, null, null);
        return this;
    }

    public Query nearSphere(String property, Point point, Double maxDistance) {
        this.nearSphere(property, point, maxDistance, null);
        return this;
    }

    public Query nearSphere(String property, Point point, Double maxDistance, Double minDistance) {
        if (property != null) {
            this.getGeospatialOperators().add(
                new OperationGeoNearFunction(property, Boolean.TRUE, maxDistance, minDistance, point));
        }
        return this;
    }

    public Query within(String property, Point... points) {
        if (property != null) {
            this.getGeospatialOperators().add(new OperationGeoWithinFunction(property, points));
        }
        return this;
    }

    public Query intersect(String property, GeometryType geometryType, Point... points) {
        if (property != null) {
            this.getGeospatialOperators().add(new OperationGeoIntersectFunction(property, geometryType, points));
        }
        return this;
    }

    public Query andOr(Collection<Query> orQueries) {
        this.andOrs.addAll(orQueries);
        return this;
    }

    public Query or(Query anotherQuery) {
        this.ors.add(anotherQuery);
        return this;
    }

    public Query addOrderCriteria(String fieldName) {
        return this.addOrderCriteria(fieldName, OrderDirection.ASC);

    }

    @SuppressWarnings("unchecked")
    public Query addOrderCriteria(String fieldName, OrderDirection direction) {
        if (direction == null) {
            return this.addOrderCriteria(fieldName);
        }
        this.orderFields.put(fieldName, direction);
        return this;
    }

    public Query skip(Integer skip) {
        this.skip = skip;
        return this;
    }

    public Query limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Query crucialDataIntegration(Boolean crucialDataOperation) {
        this.crucialDataIntegration = crucialDataOperation;
        return this;
    }

    public Collection<OperationEqual> getFilters() {
        return this.filters;
    }

    public OrderedMap getOrderFields() {
        return this.orderFields;
    }

    public Collection<OperationWithRange> getRangeOperators() {
        return this.rangeOperators;
    }

    public Collection<OperationWithMathFunction> getMathOperators() {
        return this.mathOperators;
    }

    public Collection<OperationWithComparison> getComparisonOperators() {
        return this.comparisonOperators;
    }

    public Collection<OperationWithGeospatialFunction> getGeospatialOperators() {
        return this.geospatialOperators;
    }

    public List<Query> getOrs() {
        return this.ors;
    }

    public Boolean isCrucialDataIntegration() {
        return this.crucialDataIntegration;
    }

    public List<Query> getAndOrs() {
        return this.andOrs;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public Integer getSkip() {
        return this.skip;
    }

    public static class OperationWithComparison {
        private String property;
        private ComparisonOperation operation;
        private Object value;
        private boolean negation;

        public OperationWithComparison(String property, ComparisonOperation operation, Object value, boolean negation) {
            super();
            this.property = property;
            this.operation = operation;
            this.value = value;
            this.negation = negation;
        }

        public ComparisonOperation getOperation() {
            return this.operation;
        }

        public Object getValue() {
            return this.value.getClass().isEnum() ? this.value.toString() : this.value;
        }

        public boolean isNegation() {
            return this.negation;
        }

        public String getProperty() {
            return this.property;
        }
    }

    public static class OperationWithRange {
        private String property;
        private RangeOperation rangeOperation;
        private Collection<?> values;
        private boolean negation;

        public OperationWithRange(String property, RangeOperation rangeOperation, Collection<?> values, boolean negation) {
            super();
            this.property = property;
            this.rangeOperation = rangeOperation;
            this.values = values;
            this.negation = negation;
        }

        public RangeOperation getCollectionOperation() {
            return this.rangeOperation;
        }

        public Collection<?> getValues() {
            if (!this.values.isEmpty()) {
                Collection<Object> values = new ArrayList<Object>();
                for (Object obj : this.values) {
                    if (obj.getClass().isEnum()) {
                        values.add(obj.toString());
                    } else {
                        values.add(obj);
                    }
                }
                return this.values;
            } else {
                return this.values;
            }
        }

        public boolean isNegation() {
            return this.negation;
        }

        public String getProperty() {
            return this.property;
        }

    }

    public static class OperationWithMathFunction {
        private String property;
        private MathOperation mathOperation;
        private Object values;
        private boolean negation;

        public OperationWithMathFunction(String property, MathOperation mathOperation, Object values, boolean negation) {
            super();
            this.property = property;
            this.mathOperation = mathOperation;
            this.values = values;
            this.negation = negation;
        }

        public MathOperation getMathOperation() {
            return this.mathOperation;
        }

        public Object getValues() {
            return this.values;
        }

        public boolean isNegation() {
            return this.negation;
        }

        public String getProperty() {
            return this.property;
        }
    }

    public static class OperationGeoWithinFunction
        extends OperationWithGeospatialFunction {

        private OperationGeoWithinFunction(String property, Point... points) {
            super(property, GeometryOperation.WITH_IN, GeometryType.POLYGON, points);
        }
    }

    public static class OperationGeoIntersectFunction
        extends OperationWithGeospatialFunction {

        public OperationGeoIntersectFunction(String property, GeometryType type, Point[] coordinates) {
            super(property, GeometryOperation.INTERSECTS, type, coordinates);
        }

    }

    public static class OperationGeoNearFunction
        extends OperationWithGeospatialFunction {
        private Double maxDistance;
        private Double minDistance;

        public OperationGeoNearFunction(String property, Boolean sphere, Double maxDistance, Double minDistance, Point point) {
            super(property, sphere ? GeometryOperation.NEAR_SPHERE : GeometryOperation.NEAR, GeometryType.POINT, point);
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
        }

        public Double getMaxDistance() {
            return this.maxDistance;
        }

        public Double getMinDistance() {
            return this.minDistance;
        }
    }

    public static class OperationWithGeospatialFunction {
        private String property;
        private GeometryOperation geometryOperation;
        private GeometryType type;
        private Point[] coordinates;

        public OperationWithGeospatialFunction(String property, GeometryOperation geometryOperation, GeometryType type,
            Point... coordinates) {
            super();
            this.property = property;
            this.geometryOperation = geometryOperation;
            this.type = type;
            this.coordinates = coordinates;
        }

        public GeometryOperation getGeometryOperation() {
            return this.geometryOperation;
        }

        public GeometryType getType() {
            return this.type;
        }

        public Point[] getCoordinates() {
            return this.coordinates;
        }

        public String getProperty() {
            return this.property;
        }
    }

    public static class OperationEqual {
        private String property;
        private Object value;

        public OperationEqual(String property, Object value) {
            super();
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return this.property;
        }

        public Object getValue() {
            return this.value;
        }
    }

}
