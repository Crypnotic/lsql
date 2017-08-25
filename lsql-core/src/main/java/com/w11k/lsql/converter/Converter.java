package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;
import com.w11k.lsql.utils.SqlTypesNames;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Converter {

    public static Converter withDefaultValueForNull(final Converter delegate, final Object defaultValueForNull) {
        return new Converter(delegate.getJavaType(), delegate.getSqlType()) {
            @Override
            protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
                delegate.setValue(lSql, ps, index, val);
            }

            @Override
            protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
                return delegate.getValue(lSql, rs, index);
            }

            @Override
            protected Object getDefaultValueForNull(LSql lSql, ResultSet rs, int index) {
                return defaultValueForNull;
            }
        };
    }

    private final Class<?> javaType;

    private final int sqlType;

    public Converter(Class<?> javaType, int sqlType) {
        this.javaType = javaType;
        this.sqlType = sqlType;
    }


    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        if (val != null) {
            failOnWrongValueType(val);
            setValue(lSql, ps, index, val);
        } else {
            ps.setNull(index, sqlType);
        }
    }

    public void failOnWrongValueType(Object val) {
        if (!isValueValid(val)) {
            throw new IllegalArgumentException(
                    "value '" + val + "' of type '" + val.getClass().getCanonicalName() + "' " +
                            "does not match expected type " +
                            "'" + getJavaType().getCanonicalName() + "'"
            );
        }
    }

    public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException {
        rs.getObject(index);
        if (rs.wasNull()) {
            return this.getDefaultValueForNull(lSql, rs, index);
        }
        return getValue(lSql, rs, index);
    }

    public boolean isValueValid(Object value) {
        if (value == null) {
            return isNullValid();
        }

        return javaType.isAssignableFrom(value.getClass());
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public int getSqlType() {
        return sqlType;
    }

    @Override
    public String toString() {
        return "Converter{" +
                "javaType=" + javaType +
                ", sqlType=" + SqlTypesNames.getName(sqlType) +
                '}';
    }

    protected abstract void setValue(LSql lSql, PreparedStatement ps, int index,
                                     Object val) throws SQLException;

    protected abstract Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException;

    protected boolean isNullValid() {
        return true;
    }

    protected Object getDefaultValueForNull(LSql lSql, ResultSet rs, int index) {
        return null;
    }

}
