package com.w11k.lsql.cli.tests.schema_public;

public class Checks_Table extends com.w11k.lsql.TypedTable<Checks_Row, com.w11k.lsql.NoPrimaryKeyColumn>  {

    @com.google.inject.Inject
    public Checks_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "checks", Checks_Row.class);
    }

    public static final String NAME = "checks";

    protected Checks_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Checks_Row.fromInternalMap(internalMap);
    }

}
