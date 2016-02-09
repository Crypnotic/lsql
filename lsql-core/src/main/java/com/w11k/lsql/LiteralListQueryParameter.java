package com.w11k.lsql;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LiteralListQueryParameter<T> extends DynamicQueryParameter {

    public static <A> LiteralListQueryParameter<A> of(Iterable<A> values) {
        return new LiteralListQueryParameter<A>(values);
    }

    public static <A> LiteralListQueryParameter<A> of(A... values) {
        return new LiteralListQueryParameter<A>(Lists.newArrayList(values));
    }

    protected List<T> values;

    public LiteralListQueryParameter(Iterable<T> values) {
        this.values = Lists.newCopyOnWriteArrayList(values);
    }

    @Override
    public String getSqlString() {
        if (getNumberOfQueryParameters() == 0) {
            return "";
        }
        return Strings.repeat("?,", getNumberOfQueryParameters() - 1) + "?";
    }

    @Override
    public int getNumberOfQueryParameters() {
        return this.values.size();
    }

    @Override
    public void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException {
        ps.setObject(preparedStatementIndex, this.values.get(localIndex));
    }

    @Override
    public String toString() {
        return "LiteralListQueryParameter[" + Joiner.on(",").join(values) + "]";
    }
}