package br.com.technologgy.integracao.mongo.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import br.com.technologgy.integracao.mongo.query.Query.OrderDirection;


public class QueryBuilder {

    public static final String DEFAULT_OPERATOR_ORDER_COLUMN = "sort";
    public static final String DEFAULT_OPERATOR_ORDER_DIR = "order";
    public static final String DEFAULT_OPERATOR_ORDER_DIR_ASC = "asc";
    public static final String DEFAULT_OPERATOR_ORDER_DIR_DESC = "desc";
    public static final String DEFAULT_OPERATOR_OR_BETWEEN_FIELDS = "OR";

    public static final String DEFAULT_OPERATOR_LIKE = "*";
    public static final String DEFAULT_OPERATOR_OR = "|";


    private QueryBuilder() {
    }

    /**
     * Builds a HandlerQuery from a query string parameters map.
     * <br>For example, this query string<br>
     * <b> items?group=&#42;alfre&#42;&amp;country=argentina&amp;language=pt&amp;OR=country,language&amp;sort=country&amp;order=asc</b>
     * generates a HandlerQuery that filters items that have the "alfre" group (using wildcards)
     * and also have either "argentina" in country or "pt" in the language field, sorted by country in ascending order.
     * @param queryStringMap Query string parameters.
     * @return Query
     */
    public static Query buildFromQueryStringMap(Map<String, String> queryStringMap) {

        Query handlerQuery = new Query();
        String orderField = null;
        OrderDirection orderDirection = null;
        Map<String, Object> orFields = new HashMap<String, Object>();
        Map<String, Object> fields = new HashMap<String, Object>();

        // Process key -> values, building operators and filters
        for (String key : queryStringMap.keySet()) {
            String value = queryStringMap.get(key);

            if (key != null && value != null) {

                if (key.equals(DEFAULT_OPERATOR_ORDER_COLUMN)) {
                    orderField = value;
                } else if (key.equals(DEFAULT_OPERATOR_ORDER_DIR)) {
                    orderDirection = value.equals(DEFAULT_OPERATOR_ORDER_DIR_ASC) ? OrderDirection.ASC : OrderDirection.DESC;
                } else {
                    // ************* Common fields **************
                    if (key.equals(DEFAULT_OPERATOR_OR_BETWEEN_FIELDS)) {
                        for (String orField : Arrays.asList(value.split(","))) {
                            orFields.put(orField, null);
                        }
                    } else {
                        // Add field
                        if (isOr(value)) {
                            List<Query> sameFieldOrQueries = new ArrayList<Query>();
                            for (String splittedOrValue : value.split(Pattern.quote(DEFAULT_OPERATOR_OR))) {
                                Query sameFieldOrQuery = new Query();
                                sameFieldOrQuery.equals(key,
                                    isLike(splittedOrValue) ? evaluateLikes(splittedOrValue) : splittedOrValue);
                                sameFieldOrQueries.add(sameFieldOrQuery);
                            }
                            handlerQuery.andOr(sameFieldOrQueries);
                        } else {
                            fields.put(key, isLike(value) ? evaluateLikes(value) : value);
                        }
                    }
                }
            }
        }

        if (orderField != null) {
            handlerQuery.addSort(orderField, orderDirection);
        }

        // Gather all OR fields
        for (String orField : orFields.keySet()) {
            if (fields.containsKey(orField)) {
                orFields.put(orField, fields.get(orField));
                fields.remove(orField);
            }
        }

        // Set all AND fields
        handlerQuery.putAll(fields);

        // Perform the logical OR in the HandlerQuery
        List<Query> handlerQueriesOrs = new ArrayList<Query>();
        for (String orField : orFields.keySet()) {
            Query handlerQueryOr = new Query();
            handlerQueryOr.equals(orField, orFields.get(orField));
            handlerQueriesOrs.add(handlerQueryOr);
        }
        handlerQuery.andOr(handlerQueriesOrs);

        return handlerQuery;
    }

    private static Pattern evaluateLikes(String value) {

        boolean likeAtStart = value.startsWith(DEFAULT_OPERATOR_LIKE);
        boolean likeAtEnd = value.endsWith(DEFAULT_OPERATOR_LIKE);

        StringBuffer filter = new StringBuffer();
        value = value.replace(DEFAULT_OPERATOR_LIKE, "");

        if (likeAtStart && likeAtEnd) {
            filter.append(value);
        } else {
            if (likeAtEnd) {
                filter.append("^");
            }

            filter.append(value);

            if (likeAtStart) {
                filter.append("$");
            }
        }

        return Pattern.compile(filter.toString(), Pattern.CASE_INSENSITIVE);
    }

    private static boolean isLike(String value) {
        return value.endsWith(DEFAULT_OPERATOR_LIKE) || value.startsWith(DEFAULT_OPERATOR_LIKE);
    }

    private static boolean isOr(String value) {
        return value.contains(DEFAULT_OPERATOR_OR);
    }
}
