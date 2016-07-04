package com.w11k.lsql.tests.pojo;

import com.google.common.base.Optional;
import com.w11k.lsql.*;
import com.w11k.lsql.typemapper.TypeMapper;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class PojoTableTest extends AbstractLSqlTest {

    public static class Table1Pojo {
        private int id;

        private String firstName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

    public static class Table1PojoSubclass extends Table1Pojo {
        private int ignore = 1;

        public int getIgnore() {
            return ignore;
        }

        public void setIgnore(int ignore) {
            this.ignore = ignore;
        }
    }

    public static class Table1WithAtomicInteger extends Table1Pojo {

        private AtomicInteger ai = new AtomicInteger(1);

        public AtomicInteger getAi() {
            return this.ai;
        }

        public void setAi(AtomicInteger ai) {
            this.ai = ai;
        }
    }

    private TypeMapper atomicIntegerTypeMapper = new TypeMapper(AtomicInteger.class, new int[]{Types.INTEGER}, Types.INTEGER) {
        @Override
        protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
            ps.setInt(index, ((AtomicInteger) val).get());
        }

        @Override
        protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
            return new AtomicInteger((int) rs.getInt(index));
        }
    };

    @Test
    public void insert() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "text1");
    }

    @Test
    public void update() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        t1.setFirstName("text2");
        table1Pojo.update(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "text2");
    }

    @Test
    public void delete() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        table1Pojo.delete(t1);
        Table table11Raw = lSql.table("table1");
        Optional<LinkedRow> load = table11Raw.load(1);
        assertFalse(load.isPresent());
    }

    @Test
    public void insertAssignsDefaultValue() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, first_name TEXT DEFAULT 'default value')");
        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        table1.insert(t1);
        assertEquals(t1.getFirstName(), "default value");
    }

    @Test
    public void insertIgnoresDefaultValueOnPureInsert() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, first_name TEXT DEFAULT 'default value')");
        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        table1.insert(t1, true);
        assertEquals(t1.getFirstName(), null);
    }

    @Test
    public void loadById() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        lSql.table("table1").insert(Row.fromKeyVals(
                "id", 1,
                "firstName", "name1"
        )).get();

        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = table1.load(1).get();
        assertEquals(t1.getId(), 1);
        assertEquals(t1.getFirstName(), "name1");
    }

    @Test
    public void insertIgnoresFieldsFromSubclass() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1PojoSubclass t1 = new Table1PojoSubclass();
        t1.setId(1);
        t1.setFirstName("text1");
        t1.setIgnore(123);
        table1Pojo.insert(t1);
    }

    @Test
    public void fieldsUseConverterRegistry() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai INTEGER)");
        this.lSql.getDialect().getConverterRegistry()
                .addConverter(this.atomicIntegerTypeMapper);
        PojoTable<Table1WithAtomicInteger> table1 = this.lSql.table("table1", Table1WithAtomicInteger.class);
        Table1WithAtomicInteger t1 = new Table1WithAtomicInteger();
        t1.setId(1);
        t1.setAi(new AtomicInteger(2));
        t1.setFirstName("Max");
        table1.insert(t1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*No converter.*")
    public void failOnFieldsWithMissingConverters() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai INTEGER)");
        this.lSql.table("table1", Table1WithAtomicInteger.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*converter.*not support.*")
    public void failWhenConverterCanNotConvertBetweenJavaAndSqlType() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai VARCHAR(10))");
        this.lSql.getDialect().getConverterRegistry()
                .addConverter(this.atomicIntegerTypeMapper);
        this.lSql.table("table1", Table1WithAtomicInteger.class);
    }

}
