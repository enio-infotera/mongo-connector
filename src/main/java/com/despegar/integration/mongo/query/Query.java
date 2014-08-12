package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.util.Assert;

public class Query {

    public static enum OrderDirection {
        ASC, DESC
    }

    public static enum RangeOperation {
        IN, NOT_IN, ALL
    }

    public static enum MathOperation {
        MODULO
    }

    public static enum ComparisonOperation {
        GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL, EXISTS
    }

    public static enum GeometryOperation {
        WITH_IN, INTERSECTS, NEAR, NEAR_SPHERE
    }

    public static enum GeometrySpecifiers {
        GEOMETRY, MAX_DISTANCE, CENTER, CENTER_SPHERE, BOX, POLYGON, UNIQUE_DOCS
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

    private Integer limit = 10;
    private Integer skip = 0;

    public Query() {
    }

    /**
     * Search the key value (field filter)
     * @param key
     * @param value
     * @return
     */
    public Query put(String key, Object value) {

        if (key != null) {
            this.getFilters().put(key, value);
        }

        return this;
    }

    /**
     * Search the key - values in the provided filters map
     * @param filters
     * @return
     */
    public Query putAll(Map<String, Object> filters) {

        this.getFilters().putAll(filters);

        return this;
    }

    /**
     * Search the key value matching the range operation described over the values present in the collection
     * @param key
     * @param operator
     * @param values
     * @return
     */
    public Query put(String key, RangeOperation operator, Collection<?> values) {
        return this.put(key, operator, values, false);
    }

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

    /**
     * Search the key value matching the comparison operation with the value
     * @param key
     * @param operator
     * @param value
     * @return
     */
    public Query put(String key, ComparisonOperation operator, Object value) {
        return this.put(key, operator, value, false);
    }

    public Query put(String key, ComparisonOperation operator, Object value, boolean negation) {
        OperationWithComparison operationWithComparison = this.getComparisonOperators().get(key);

        if (operationWithComparison != null) {
            operationWithComparison.addComparision(new OperationWithComparison(operator, value, negation));
        } else {
            this.getComparisonOperators().put(key, new OperationWithComparison(operator, value, negation));
        }
        return this;
    }

    /**
     * Search the key value matching the math operation with the value
     * @param key
     * @param operator
     * @param value
     * @return
     */
    public Query put(String key, MathOperation operator, Object value) {
        return this.put(key, operator, value, false);
    }

    public Query put(String key, MathOperation operator, Object value, boolean negation) {
        this.getMathOperators().put(key, new OperationWithMathFunction(operator, value, negation));
        return this;
    }

    /**
     * Search the key value matching the geospatial operation with the value
     * @param key
     * @param operator
     * @param value
     * @return
     */
    public Query put(String key, GeometryOperation operator, Map<GeometrySpecifiers, Object> value) {
        return this.put(key, operator, value, false);
    }

    public Query put(String key, GeometryOperation operator, Map<GeometrySpecifiers, Object> value, boolean negation) {
        this.getGeospatialOperators().put(key, new OperationWithGeospatialFunction(operator, value, negation));
        return this;
    }

    /**
     * Add a field for sorting purpose, with a default direction (asc)
     */
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

    public Query or(Query anotherQuery) {
        this.ors.add(anotherQuery);
        return this;
    }

    public List<Query> getOrs() {
        return this.ors;
    }

    public Boolean isCrucialDataIntegration() {
        return this.crucialDataIntegration;
    }

    public void setCrucialDataIntegration(Boolean crucialDataOperation) {
        this.crucialDataIntegration = crucialDataOperation;
    }

    public Query andOr(Collection<Query> orQueries) {
        this.andOrs.addAll(orQueries);
        return this;
    }

    public List<Query> getAndOrs() {
        return this.andOrs;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return this.skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
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

    public static class OperationWithGeospatialFunction {
        private GeometryOperation geometryOperation;
        private Map<GeometrySpecifiers, Object> geometrySpecifiers;
        private boolean negation;

        public OperationWithGeospatialFunction(GeometryOperation geometryOperation,
            Map<GeometrySpecifiers, Object> gemetrySpecifiers, boolean negation) {
            super();
            this.geometryOperation = geometryOperation;
            this.geometrySpecifiers = gemetrySpecifiers;
            this.negation = negation;
        }

        public GeometryOperation getGeometryOperation() {
            return this.geometryOperation;
        }

        public Map<GeometrySpecifiers, Object> getGeometrySpecifiers() {
            return this.geometrySpecifiers;
        }

        public boolean isNegation() {
            return this.negation;
        }
    }

}
