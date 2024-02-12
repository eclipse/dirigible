package org.eclipse.dirigible.components.api.db;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.sql.DataTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class ParametersSetter {

    private static final Set<ParamSetter> paramSetters = Set.of(//
            new BooleanParamSetter(), //
            new TinyIntParamSetter(), //
            new IntegerParamSetter(), //
            new DoubleParamSetter(), //
            new TextParamSetter(), //
            new DateParamSetter(), //
            new TimestampParamSetter(), //
            new TimeParamSetter(), //
            new SmallIntParamSetter(), //
            new BigIntParamSetter(), //
            new RealParamSetter(), //
            new BlobParamSetter());

    static void setParameters(String parameters, PreparedStatement preparedStatement) throws SQLException {
        JsonElement parametersElement = GsonHelper.parseJson(parameters);
        if (!(parametersElement instanceof JsonArray parametersArray)) {
            throw new IllegalArgumentException("Parameters must be provided as a JSON array, e.g. [1, 'John', 9876]");
        }

        int paramIndex = 1;
        for (JsonElement parameterElement : parametersArray) {
            setParameter(preparedStatement, paramIndex, parameterElement);
            paramIndex++;
        }
    }

    private static void setParameter(PreparedStatement preparedStatement, int paramIndex, JsonElement parameterElement)
            throws SQLException {
        if (parameterElement.isJsonPrimitive()) {
            setJsonPrimitiveParam(preparedStatement, paramIndex, parameterElement);
            return;
        }

        if (parameterElement.isJsonObject()) {
            setJsonObjectParam(preparedStatement, paramIndex, parameterElement);
            return;
        }

        throw new IllegalArgumentException("Parameters must contain primitives and objects only");
    }

    private static void setJsonPrimitiveParam(PreparedStatement preparedStatement, int paramIndex, JsonElement parameterElement)
            throws SQLException {
        if (parameterElement.getAsJsonPrimitive()
                            .isBoolean()) {
            preparedStatement.setBoolean(paramIndex, parameterElement.getAsBoolean());
            return;
        }

        if (parameterElement.getAsJsonPrimitive()
                            .isString()) {
            preparedStatement.setString(paramIndex, parameterElement.getAsString());
            return;
        }

        if (parameterElement.getAsJsonPrimitive()
                            .isNumber()) {
            setNumber(preparedStatement, paramIndex, parameterElement);
            return;
        }

        throw new IllegalArgumentException("Parameter type unkown");
    }

    private static void setNumber(PreparedStatement preparedStatement, int paramIndex, JsonElement parameterElement) throws SQLException {
        boolean isNumberParameterSet = false;
        int numberIndex = paramIndex;
        try {
            preparedStatement.setInt(numberIndex, parameterElement.getAsInt());
            isNumberParameterSet = true;
        } catch (SQLException | ClassCastException e) {
            // Do nothing
        }

        if (!isNumberParameterSet) {
            try {
                preparedStatement.setShort(numberIndex, parameterElement.getAsShort());
                isNumberParameterSet = true;
            } catch (SQLException | ClassCastException e) {
                // Do nothing
            }
        }
        if (!isNumberParameterSet) {
            try {
                preparedStatement.setLong(numberIndex, parameterElement.getAsLong());
                isNumberParameterSet = true;
            } catch (SQLException | ClassCastException e) {
                // Do nothing
            }
        }
        if (!isNumberParameterSet) {
            try {
                preparedStatement.setBigDecimal(numberIndex, parameterElement.getAsBigDecimal());
                isNumberParameterSet = true;
            } catch (SQLException | ClassCastException e) {
                // Do nothing
            }
        }
        if (!isNumberParameterSet) {
            preparedStatement.setObject(numberIndex, parameterElement.getAsNumber()
                                                                     .toString());
        }
    }

    private static void setJsonObjectParam(PreparedStatement preparedStatement, int paramIndex, JsonElement parameterElement)
            throws SQLException {
        JsonObject jsonObject = parameterElement.getAsJsonObject();
        JsonElement typeElement = jsonObject.get("type");

        if (!typeElement.isJsonPrimitive() || !typeElement.getAsJsonPrimitive()
                                                          .isString()) {
            throw new IllegalArgumentException("Parameter 'type' must be a string representing the database type name");
        }

        String dataType = typeElement.getAsJsonPrimitive()
                                     .getAsString();
        JsonElement valueElement = jsonObject.get("value");
        if (null == valueElement || valueElement.isJsonNull()) {
            Integer sqlType = DataTypeUtils.getSqlTypeByDataType(dataType);
            preparedStatement.setNull(paramIndex, sqlType);
            return;
        }

        ParamSetter paramSetter = paramSetters.stream()
                                              .filter(ps -> ps.isApplicable(dataType))
                                              .findFirst()
                                              .orElseThrow(() -> new IllegalArgumentException("Parameter 'type'[" + dataType
                                                      + "] must be a string representing a valid database type name"));
        paramSetter.setParam(valueElement, paramIndex, preparedStatement, dataType);

    }

    private interface ParamSetter {

        boolean isApplicable(String dataType);

        void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType) throws SQLException;
    }

    private static abstract class BaseParamSetter implements ParamSetter {

        protected void throwWrongValue(JsonElement sourceParam, String dataType) {
            throw new IllegalArgumentException("Wrong value [" + sourceParam + "] for parameter of type " + dataType);
        }
    }

    private static class TextParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isVarchar(dataType) || DataTypeUtils.isText(dataType) || DataTypeUtils.isChar(dataType)
                    || DataTypeUtils.isNvarchar(dataType) || DataTypeUtils.isCharacterVarying(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (!sourceParam.isJsonPrimitive() || !sourceParam.getAsJsonPrimitive()
                                                              .isString()) {
                throwWrongValue(sourceParam, dataType);
            }
            String value = sourceParam.getAsJsonPrimitive()
                                      .getAsString();
            preparedStatement.setString(paramIndex, value);
        }
    }

    private static class DateParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isDate(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Date value = new Date(sourceParam.getAsJsonPrimitive()
                                                 .getAsLong());
                preparedStatement.setDate(paramIndex, value);
                return;
            }

            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Date value = null;
                try {
                    value = new Date(Long.parseLong(sourceParam.getAsJsonPrimitive()
                                                               .getAsString()));
                } catch (NumberFormatException e) {
                    // assume date string in ISO format e.g. 2018-05-22T21:00:00.000Z
                    value = new Date(jakarta.xml.bind.DatatypeConverter.parseDateTime(sourceParam.getAsJsonPrimitive()
                                                                                                 .getAsString())
                                                                       .getTime()
                                                                       .getTime());
                }
                preparedStatement.setDate(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class TimestampParamSetter extends BaseParamSetter {
        private static final Logger logger = LoggerFactory.getLogger(TimestampParamSetter.class);

        private static final SimpleDateFormat SIMPLE_DATE_FORMAT_WITHOUT_ZONE =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isTimestamp(dataType) || DataTypeUtils.isDateTime(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Timestamp value = new Timestamp(sourceParam.getAsJsonPrimitive()
                                                           .getAsLong());
                preparedStatement.setTimestamp(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Timestamp value = null;
                try {
                    value = new Timestamp(Long.parseLong(sourceParam.getAsJsonPrimitive()
                                                                    .getAsString()));
                } catch (NumberFormatException e) {
                    String timestampString = sourceParam.getAsJsonPrimitive()
                                                        .getAsString();
                    value = new Timestamp(getTime(timestampString));
                }
                preparedStatement.setTimestamp(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }

        private long getTime(String timestampString) {
            try {
                // assume date string in ISO format e.g. 2018-05-22T21:00:00.000Z
                Calendar calendar = jakarta.xml.bind.DatatypeConverter.parseDateTime(timestampString);
                return calendar.getTime()
                               .getTime();
            } catch (IllegalArgumentException ex) {
                logger.debug("Failed to parse timestamp string [{}]", timestampString, ex);

                try {
                    java.util.Date date = SIMPLE_DATE_FORMAT_WITHOUT_ZONE.parse(timestampString);
                    return date.getTime();
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Cannot get time from timestamp string " + timestampString, e);
                }
            }
        }
    }

    private static class TimeParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isTime(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Time value = new Time(sourceParam.getAsJsonPrimitive()
                                                 .getAsLong());
                preparedStatement.setTime(paramIndex, value);
                return;
            }

            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Time value = null;
                try {
                    value = new Time(Long.parseLong(sourceParam.getAsJsonPrimitive()
                                                               .getAsString()));
                } catch (NumberFormatException e) {
                    // assume XSDTime
                    value = new Time(jakarta.xml.bind.DatatypeConverter.parseTime(sourceParam.getAsJsonPrimitive()
                                                                                             .getAsString())
                                                                       .getTime()
                                                                       .getTime());
                }
                preparedStatement.setTime(paramIndex, value);
                return;
            }

            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class IntegerParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isInteger(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Integer value = sourceParam.getAsJsonPrimitive()
                                           .getAsInt();
                preparedStatement.setInt(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Integer value = Integer.parseInt(sourceParam.getAsJsonPrimitive()
                                                            .getAsString());
                preparedStatement.setInt(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class TinyIntParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isTinyint(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                byte value = (byte) sourceParam.getAsJsonPrimitive()
                                               .getAsInt();
                preparedStatement.setByte(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                byte value = (byte) Integer.parseInt(sourceParam.getAsJsonPrimitive()
                                                                .getAsString());
                preparedStatement.setByte(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class SmallIntParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isSmallint(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                short value = (short) sourceParam.getAsJsonPrimitive()
                                                 .getAsInt();
                preparedStatement.setShort(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                short value = (short) Integer.parseInt(sourceParam.getAsJsonPrimitive()
                                                                  .getAsString());
                preparedStatement.setShort(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class BigIntParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isBigint(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Long value = sourceParam.getAsJsonPrimitive()
                                        .getAsBigInteger()
                                        .longValue();
                preparedStatement.setLong(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Long value = Long.parseLong(sourceParam.getAsJsonPrimitive()
                                                       .getAsString());
                preparedStatement.setLong(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class RealParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isReal(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Float value = sourceParam.getAsJsonPrimitive()
                                         .getAsNumber()
                                         .floatValue();
                preparedStatement.setFloat(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Float value = Float.parseFloat(sourceParam.getAsJsonPrimitive()
                                                          .getAsString());
                preparedStatement.setFloat(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class DoubleParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isDouble(dataType) | DataTypeUtils.isDecimal(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Double value = sourceParam.getAsJsonPrimitive()
                                          .getAsNumber()
                                          .doubleValue();
                preparedStatement.setDouble(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Double value = Double.parseDouble(sourceParam.getAsJsonPrimitive()
                                                             .getAsString());
                preparedStatement.setDouble(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class BooleanParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isBoolean(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isNumber()) {
                Boolean value = sourceParam.getAsJsonPrimitive()
                                           .getAsBoolean();
                preparedStatement.setBoolean(paramIndex, value);
                return;
            }
            if (sourceParam.isJsonPrimitive() && sourceParam.getAsJsonPrimitive()
                                                            .isString()) {
                Boolean value = Boolean.parseBoolean(sourceParam.getAsJsonPrimitive()
                                                                .getAsString());
                preparedStatement.setBoolean(paramIndex, value);
                return;
            }
            throwWrongValue(sourceParam, dataType);
        }
    }

    private static class BlobParamSetter extends BaseParamSetter {

        @Override
        public boolean isApplicable(String dataType) {
            return DataTypeUtils.isBlob(dataType);
        }

        @Override
        public void setParam(JsonElement sourceParam, int paramIndex, PreparedStatement preparedStatement, String dataType)
                throws SQLException {
            if (sourceParam.isJsonArray()) {
                byte[] bytes = BytesHelper.jsonToBytes(sourceParam.getAsJsonArray()
                                                                  .toString());
                preparedStatement.setBinaryStream(paramIndex, new ByteArrayInputStream(bytes), bytes.length);
                return;
            }

            throwWrongValue(sourceParam, dataType);
        }
    }

}
