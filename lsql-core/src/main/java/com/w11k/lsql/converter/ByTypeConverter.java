package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

/**
 * Note: This Class must behave immutable because by default one instance is shared
 * between LSql, all Tables and all Column.
 */
public class ByTypeConverter implements Converter {

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();
    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ByTypeConverter() {
        addConverter(
                new int[]{Types.BOOLEAN},
                Boolean.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        ps.setBoolean(index, (Boolean) val);
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getBoolean(index);
                    }
                });
        addConverter(
                new int[]{Types.BIT, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT},
                Integer.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setInt(index, (Integer) val);
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getInt(index);
                    }
                });
        addConverter(
                new int[]{Types.FLOAT},
                Float.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setFloat(index, (Float) val);
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getFloat(index);
                    }
                });
        addConverter(
                new int[]{Types.DOUBLE, Types.REAL, Types.DECIMAL},
                Double.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setDouble(index, (Double) val);
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getDouble(index);
                    }
                });
        /*
        addConverter(
                new int[]{Types.CLOB},
                String.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setClob(index, new StringClob(val.toString()));
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
                        Clob clob = rs.getClob(index);
                        if (clob != null) {
                            Reader reader = clob.getCharacterStream();
                            return CharStreams.toString(reader);
                        } else {
                            return null;
                        }
                    }
                });
                */
        addConverter(
                new int[]{Types.CHAR},
                Character.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        ps.setByte(index, (byte) ((Character) val).charValue());
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return (char) rs.getByte(index);
                    }
                });
        addConverter(
                new int[]{Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR},
                String.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setString(index, val.toString());
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getString(index);
                    }
                });
        addConverter(
                new int[]{Types.TIMESTAMP},
                DateTime.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        DateTime dt = (DateTime) val;
                        Timestamp ts = new Timestamp(dt.getMillis());
                        ps.setTimestamp(index, ts);
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return new DateTime(rs.getTimestamp(index).getTime());
                    }
                });
        addConverter(
                new int[]{Types.BLOB},
                com.w11k.lsql.relational.Blob.class,
                new Converter() {
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        com.w11k.lsql.relational.Blob blob = (com.w11k.lsql.relational.Blob) val;
                        ps.setBlob(index, blob.getInputStream());
                    }

                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        Blob blob = rs.getBlob(index);
                        return new com.w11k.lsql.relational.Blob(blob.getBinaryStream());
                    }
                });


        // TODO add more types
        // static int 	DATALINK;
        // static int 	DISTINCT;
        // static int 	JAVA_OBJECT;
        // static int 	LONGVARBINARY;
        // static int 	NULL;
        // static int 	NUMERIC;
        // static int 	OTHER;
        // static int 	REF;
        // static int 	STRUCT;
        // static int 	TIME;
        // static int 	DATE;
        // static int 	VARBINARY;
    }

    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
        try {
            int columnType = rs.getMetaData().getColumnType(index);
            logger.trace("SQL type in ResultSet is {}", columnType);
            Converter converter = sqlValueToJavaConverters.get(columnType);
            //converter = converter == null ? defaultConverter : converter;
            if (converter == null) {
                throw new RuntimeException("No converter found for SQL type: " + columnType);
            }
            return converter.getValueFromResultSet(rs, index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
        Converter converter = javaValueToSqlConverters.get(val.getClass());
        //converter = converter == null ? defaultConverter : converter;
        if (converter == null) {
            throw new RuntimeException("No converter found for Java type: " + val.getClass());
        }
        converter.setValueInStatement(ps, index, val);
    }

    public void addConverter(int[] sqlTypes, Class javaType, Converter converter) {
        for (int sqlType : sqlTypes) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(javaType, converter);
    }

}
