package com.w11k.lsql.converter.types;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntConverter extends Converter {

    public static int[] SQL_TYPES = new int[]{
            Types.TINYINT, Types.SMALLINT, Types.INTEGER
    };

    public IntConverter(int sqlType) {
        super(Integer.class, sqlType);
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setInt(index, (Integer) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);

    }
}
