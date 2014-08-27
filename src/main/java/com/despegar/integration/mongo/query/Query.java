package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.util.Assert;

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

    @Deprecated
    // delete public... only static enum
    public static enum RangeOperation {
        IN, NOT_IN, ALL
    }

    @Deprecated
    // delete public... only static enum
    public static enum MathOperation {
        MODULO
    }

    @Deprecated
    // delete public... only static enum
    public static enum ComparisonOperation {
        GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL, EXISTS
    }

    static enum GeometryOperation {
        WITH_IN, INTERSECTS, NEAR, NEAR_SPHERE
    }

    private Map<String, OperationWithComparison> comparisonOperators = new HashMap<String, OperationWithComparison>();
    private Map<String, OperationWithRange> rangeOperators = new HashMap<String, OperationWithRange>();
    private Map<String, OperationWithGeospatialFunction> geospatialOperators = new HashMap<String, OperationWithGeospatialFunction>();
    private Map<String, OperationWithMathFunction> mathOperators = new HashMap<String, OperationWithMathFunction>();
    private Map<String, Object> filters = new HashMap<String, Object>();
    private OrderedMap orderFields = new ListOrderedMap();
    private Boolean crucialDataIntegration = Boolean.FALSE;

    private List<Query> ors = new ArrayList<Query>();
    private List<Query> andOrs = new ArrayList<Query>();

    private Integer limit = null;
    private Integer skip = 0;

    public Query() {
    }

    @Deprecated
    /*
     * use equals
     */
    public Query put(String key, Object value) {

        if (key != null) {
            this.getFilters().put(key, value);
        }

        return this;
    }

    public Query equals(String property, Object value) {
        if (property != null) {
            this.getFilters().put(property, value);
        }

        return this;
    }

    public Query putAll(Map<String, Object> filters) {

        this.getFilters().putAll(filters);

        return this;
    }

    @Deprecated
    /*
     * use in, all or not in
     */
    public Query put(String key, RangeOperation operator, Collection<?> values) {
        return this.put(key, operator, values, false);
    }

    @Deprecated
    // this pass to private and not return
    /*
     * use in, all or not in
     */
    public Query put(String key, RangeOperation operator, Collection<?> values, boolean negation) {
        if (values == null) {
            return this;
        } else if (values.size() == 1 && !negation) {
            if (operator == RangeOperation.NOT_IN) {
                this.put(key, ComparisonOperation.NOT_EQUAL, values.iterator().next());
            } else {
                this.put(key, values.iterator().next());
            }

            return this;
        }
        this.getRangeOperators().put(key, new OperationWithRange(operator, values, negation));
        return this;
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

    @Deprecated
    /*
     * use greater, greaterThan, less, lessThan, or others
     */
    public Query put(String key, ComparisonOperation operator, Object value) {
        return this.put(key, operator, value, false);
    }

    @Deprecated
    // this pass to private and not return
    /*
     * use greater, greaterThan, less, lessThan, or others
     */
    public Query put(String key, ComparisonOperation operator, Object value, boolean negation) {
        OperationWithComparison operationWithComparison = this.getComparisonOperators().get(key);

        if (operationWithComparison != null) {
            operationWithComparison.addComparision(new OperationWithComparison(operator, value, negation));
        } else {
            this.getComparisonOperators().put(key, new OperationWithComparison(operator, value, negation));
        }
        return this;
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

    @Deprecated
    /*
     * use module
     */
    public Query put(String key, MathOperation operator, Object value) {
        return this.put(key, operator, value, false);
    }

    @Deprecated
    /*
     * use module
     */
    // this pass to private and not return
    public Query put(String key, MathOperation operator, Object value, boolean negation) {
        this.getMathOperators().put(key, new OperationWithMathFunction(operator, value, negation));
        return this;
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
        this.getGeospatialOperators().put(property,
            new OperationGeoNearFunction(Boolean.FALSE, maxDistance, minDistance, point));
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
        this.getGeospatialOperators().put(property,
            new OperationGeoNearFunction(Boolean.TRUE, maxDistance, minDistance, point));
        return this;
    }

    public Query within(String property, Point... points) {
        this.getGeospatialOperators().put(property, new OperationGeoWithinFunction(points));
        return this;
    }

    public Query intersect(String property, GeometryType geometryType, Point... points) {
        this.getGeospatialOperators().put(property, new OperationGeoIntersectFunction(geometryType, points));
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
        Assert.notNull(fieldName, "Field name for sorting criteria is required.");
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

    public Map<String, Object> getFilters() {
        return this.filters;
    }

    public OrderedMap getOrderFields() {
        return this.orderFields;
    }

    public Map<String, OperationWithRange> getRangeOperators() {
        if (this.rangeOperators == null) {
            this.rangeOperators = new HashMap<String, OperationWithRange>();
        }
        return this.rangeOperators;
    }

    public Map<String, OperationWithMathFunction> getMathOperators() {
        if (this.mathOperators == null) {
            this.mathOperators = new HashMap<String, OperationWithMathFunction>();
        }
        return this.mathOperators;
    }

    public Map<String, OperationWithComparison> getComparisonOperators() {
        if (this.comparisonOperators == null) {
            this.comparisonOperators = new HashMap<String, Query.OperationWithComparison>();
        }
        return this.comparisonOperators;
    }

    public Map<String, OperationWithGeospatialFunction> getGeospatialOperators() {
        if (this.geospatialOperators == null) {
            this.geospatialOperators = new HashMap<String, Query.OperationWithGeospatialFunction>();
        }
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
        private ComparisonOperation operation;
        private Object value;
        private boolean negation;

        private List<OperationWithComparison> moreComparisions = new ArrayList<Query.OperationWithComparison>();

        public OperationWithComparison(ComparisonOperation operation, Object value, boolean negation) {
            super();
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

        public OperationWithComparison addComparision(OperationWithComparison another) {
            this.moreComparisions.add(another);
            return this;
        }

        public List<OperationWithComparison> getMoreComparisions() {
            return this.moreComparisions;
        }

        public boolean isNegation() {
            return this.negation;
        }
    }

    public static class OperationWithRange {
        private RangeOperation rangeOperation;
        private Collection<?> values;
        private boolean negation;

        public OperationWithRange(RangeOperation rangeOperation, Collection<?> values, boolean negation) {
            super();
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

    }

    public static class OperationWithMathFunction {
        private MathOperation mathOperation;
        private Object values;
        private boolean negation;

        public OperationWithMathFunction(MathOperation mathOperation, Object values, boolean negation) {
            super();
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
    }

    public static class OperationGeoWithinFunction
        extends OperationWithGeospatialFunction {

        private OperationGeoWithinFunction(Point... points) {
            super(GeometryOperation.WITH_IN, GeometryType.POLYGON, points);
        }
    }

    public static class OperationGeoIntersectFunction
        extends OperationWithGeospatialFunction {

        public OperationGeoIntersectFunction(GeometryType type, Point[] coordinates) {
            super(GeometryOperation.INTERSECTS, type, coordinates);
        }

    }

    public static class OperationGeoNearFunction
        extends OperationWithGeospatialFunction {
        private Double maxDistance;
        private Double minDistance;

        public OperationGeoNearFunction(Boolean sphere, Double maxDistance, Double minDistance, Point point) {
            super(sphere ? GeometryOperation.NEAR_SPHERE : GeometryOperation.NEAR, GeometryType.POINT, point);
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
        private GeometryOperation geometryOperation;
        private GeometryType type;
        private Point[] coordinates;

        protected OperationWithGeospatialFunction() {

        }

        public OperationWithGeospatialFunction(GeometryOperation geometryOperation, GeometryType type, Point... coordinates) {
            super();
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
    }

}
