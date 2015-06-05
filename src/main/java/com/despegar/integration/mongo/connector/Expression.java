package com.despegar.integration.mongo.connector;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public abstract class Expression
    implements Bson {

    static enum ExpressionOperator {
        CONDITIONAL("$cond"),
        MULTIPLY("$multiply"),
        ADD("$add"),
        DIVIDE("$divide"),
        SUBTRACT("$subtract"),
        GREAT_THAN_EQUAL("$gte"),
        GREAT_THAN("$gt"),
        LESS_THAN("$lt"),
        LESS_THAN_EQUAL("$lte"),
        EQUAL("$eq"),
        NON_EQUAL("$ne"),
        SIZE("$size"),
        AVG("$avg"),
        FIRST("$first"),
        LAST("$last"),
        SUM("$sum"),
        PUSH("$push"),
        ADD_TO_SET("$addToSet"),
        MAX("$max"),
        MIN("$min"),
        HOUR("$hour"),
        MINUTES("$minute"),
        YEAR("$year"),
        MONTH("$month"),
        DAY("$dayOfMonth");

        private String operator;

        ExpressionOperator(String operator) {
            this.operator = operator;
        }
    };

    protected ExpressionOperator operator;

    public static Arithmetical multiply(Object... parameters) {
        return new Arithmetical(ExpressionOperator.MULTIPLY, parameters);
    }

    public static Arithmetical add(Object... parameters) {
        return new Arithmetical(ExpressionOperator.ADD, parameters);
    }

    public static Arithmetical subtract(Object... parameters) {
        return new Arithmetical(ExpressionOperator.SUBTRACT, parameters);
    }

    public static Arithmetical divide(Object dividend, Object divisor) {
        return new Arithmetical(ExpressionOperator.DIVIDE, dividend, divisor);
    }

    public static Conditional cond(Comparison comparison, Object then, Object els) {
        return new Conditional(ExpressionOperator.CONDITIONAL, comparison, then, els);
    }

    public static Conditional cond(String property, Object then, Object els) {
        return new Conditional(ExpressionOperator.CONDITIONAL, property, then, els);
    }

    public static Comparison greatThanEqual(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.GREAT_THAN_EQUAL, value1, value2);
    }

    public static Comparison greatThan(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.GREAT_THAN, value1, value2);
    }

    public static Comparison lessThanEqual(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.LESS_THAN_EQUAL, value1, value2);
    }

    public static Comparison lessThan(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.LESS_THAN, value1, value2);
    }

    public static Comparison equal(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.EQUAL, value1, value2);
    }

    public static Comparison nonEqual(Object value1, Object value2) {
        return new Comparison(ExpressionOperator.NON_EQUAL, value1, value2);
    }

    public static Array size(Object expression) {
        return new Array(ExpressionOperator.SIZE, expression);
    }

    public static Accumulators first(Object expression) {
        return new Accumulators(ExpressionOperator.FIRST, expression);
    }

    public static Accumulators last(Object expression) {
        return new Accumulators(ExpressionOperator.LAST, expression);
    }

    public static Accumulators avg(Object expression) {
        return new Accumulators(ExpressionOperator.AVG, expression);
    }

    public static Accumulators min(Object expression) {
        return new Accumulators(ExpressionOperator.MIN, expression);
    }

    public static Accumulators max(Object expression) {
        return new Accumulators(ExpressionOperator.MAX, expression);
    }

    public static Accumulators sum(Object expression) {
        return new Accumulators(ExpressionOperator.SUM, expression);
    }

    public static Accumulators push(Object expression) {
        return new Accumulators(ExpressionOperator.PUSH, expression);
    }

    public static Accumulators addToSet(Object expression) {
        return new Accumulators(ExpressionOperator.ADD_TO_SET, expression);
    }

    public static Date hour(Object expression) {
        return new Date(ExpressionOperator.HOUR, expression);
    }

    public static Date minutes(Object expression) {
        return new Date(ExpressionOperator.MINUTES, expression);
    }

    public static Date year(Object expression) {
        return new Date(ExpressionOperator.YEAR, expression);
    }

    public static Date month(Object expression) {
        return new Date(ExpressionOperator.MONTH, expression);
    }

    public static Date day(Object expression) {
        return new Date(ExpressionOperator.DAY, expression);
    }

    static class Arithmetical
        extends Expression {

        private Object[] operators;

        public Arithmetical(ExpressionOperator op, Object... operators) {
            this.operator = op;
            this.operators = operators;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

            writer.writeStartDocument();
            writer.writeStartArray(this.operator.operator);
            for (Object opt : this.operators) {
                if (opt instanceof Expression) {
                    BsonDocument exp = ((Expression) opt).toBsonDocument(documentClass, codecRegistry);
                    writer.writeBinaryData(exp.asBinary());
                } else {
                    encodeValue(writer, opt, codecRegistry);
                }
            }
            writer.writeEndArray();
            writer.writeEndDocument();

            return writer.getDocument();
        }
    }

    static class Comparison
        extends Expression {

        private Object opt1;
        private Object opt2;

        public Comparison(ExpressionOperator op, Object value1, Object value2) {
            this.operator = op;
            this.opt1 = value1;
            this.opt2 = value2;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

            writer.writeStartDocument();
            writer.writeStartArray(this.operator.operator);

            if (this.opt1 instanceof Expression) {
                BsonDocument exp = ((Expression) this.opt1).toBsonDocument(documentClass, codecRegistry);
                writer.writeBinaryData(exp.asBinary());
            } else {
                encodeValue(writer, this.opt1, codecRegistry);
            }

            if (this.opt2 instanceof Expression) {
                BsonDocument exp = ((Expression) this.opt2).toBsonDocument(documentClass, codecRegistry);
                writer.writeBinaryData(exp.asBinary());
            } else {
                encodeValue(writer, this.opt2, codecRegistry);
            }

            writer.writeEndArray();
            writer.writeEndDocument();

            return writer.getDocument();
        }
    }

    static class Conditional
        extends Expression {

        private Object ifOp;
        private Object thenOp;
        private Object elsOp;

        public Conditional(ExpressionOperator op, Object i, Object then, Object els) {
            this.operator = op;
            this.ifOp = i;
            this.thenOp = then;
            this.elsOp = els;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {

            BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

            writer.writeStartDocument();
            writer.writeStartArray(this.operator.operator);

            if (this.ifOp instanceof Expression) {
                BsonDocument exp = ((Expression) this.ifOp).toBsonDocument(documentClass, codecRegistry);
                writer.writeBinaryData(exp.asBinary());
            } else {
                encodeValue(writer, this.ifOp, codecRegistry);
            }

            if (this.thenOp instanceof Expression) {
                BsonDocument exp = ((Expression) this.thenOp).toBsonDocument(documentClass, codecRegistry);
                writer.writeBinaryData(exp.asBinary());
            } else {
                encodeValue(writer, this.thenOp, codecRegistry);
            }

            if (this.elsOp instanceof Expression) {
                BsonDocument exp = ((Expression) this.elsOp).toBsonDocument(documentClass, codecRegistry);
                writer.writeBinaryData(exp.asBinary());
            } else {
                encodeValue(writer, this.elsOp, codecRegistry);
            }

            writer.writeEndArray();
            writer.writeEndDocument();

            return writer.getDocument();

        }
    }

    static class Array
        extends Expression {

        private Object expression;

        public Array(ExpressionOperator op, Object expression) {
            this.operator = op;
            this.expression = expression;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            if (this.expression instanceof Expression) {
                return new Document(this.operator.operator, ((Expression) this.expression).toBsonDocument(documentClass,
                    codecRegistry)).toBsonDocument(documentClass, codecRegistry);
            } else {
                return new Document(this.operator.operator, this.expression).toBsonDocument(documentClass, codecRegistry);
            }
        }
    }

    static class Accumulators
        extends Expression {

        private Object expression;

        public Accumulators(ExpressionOperator op, Object expression) {
            this.operator = op;
            this.expression = expression;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            if (this.expression instanceof Expression) {
                return new Document(this.operator.operator, ((Expression) this.expression).toBsonDocument(documentClass,
                    codecRegistry)).toBsonDocument(documentClass, codecRegistry);
            } else {
                return new Document(this.operator.operator, this.expression).toBsonDocument(documentClass, codecRegistry);
            }
        }
    }

    static class Date
        extends Expression {

        private Object expression;

        public Date(ExpressionOperator op, Object expression) {
            this.operator = op;
            this.expression = expression;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            if (this.expression instanceof Expression) {
                return new Document(this.operator.operator, ((Expression) this.expression).toBsonDocument(documentClass,
                    codecRegistry)).toBsonDocument(documentClass, codecRegistry);
            } else {
                return new Document(this.operator.operator, this.expression).toBsonDocument(documentClass, codecRegistry);
            }
        }
    }

    public ExpressionOperator getOperator() {
        return this.operator;
    }

    @SuppressWarnings("unchecked")
    private static <TItem> void encodeValue(final BsonDocumentWriter writer, final TItem value,
        final CodecRegistry codecRegistry) {
        if (value == null) {
            writer.writeNull();
        } else if (value instanceof Bson) {
            ((Encoder) codecRegistry.get(BsonDocument.class)).encode(writer,
                ((Bson) value).toBsonDocument(BsonDocument.class, codecRegistry), EncoderContext.builder().build());
        } else {
            ((Encoder) codecRegistry.get(value.getClass())).encode(writer, value, EncoderContext.builder().build());
        }
    }

}
