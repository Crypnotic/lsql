package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Row;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

public class RowTest extends AbstractLSqlTest {

    @Test
    public void addKeyVals() {
        Row r = new Row().addKeyVals("a", 1, "b", "val");
        assertEquals(r.get("a"), 1);
        assertEquals(r.get("b"), "val");
    }

    @Test
    public void constructorCreatesDefensiveCopy() {
        Row row1 = new Row().addKeyVals("key1", "value1");
        Row row2 = new Row(row1);

        row1.put("key2", "value2");
        assertFalse(row2.containsKey("key2"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getAsThrowsExceptionOnWrongKey() {
        Row r = new Row().addKeyVals("a", "1");
        assertEquals(r.getInt("x"), 1);
    }

    @Test
    public void testNull() {
        Row row = new Row();
        assertNull(row.get("x"));
        row.addKeyVals("x", 1d);
        assertEquals(1d, row.getDouble("x"));
    }

    @Test
    public void testNullValues() {
        Row row = new Row();
        row.put("string", null);
        assertNull(row.getString("string"));
    }

    @Test
    public void groupByTable() {
        createTable("CREATE TABLE city (id SERIAL PRIMARY KEY, zipcode TEXT, name TEXT)");
        createTable("CREATE TABLE person (id SERIAL PRIMARY KEY, name TEXT, zipcode INTEGER REFERENCES city (id))");

        Optional<Object> cityId = lSql.table("city").insert(Row.fromKeyVals("zipcode", "53721", "name", "Siegburg"));
        lSql.table("person").insert(Row.fromKeyVals("name", "John", "zipcode", cityId.get()));

        QueriedRow row = lSql.executeRawQuery("SELECT * FROM person, city").getFirstRow().get();
        assertEquals(row.getString("city.zipcode"), "53721");
        assertEquals(row.getString("city.name"), "Siegburg");
        assertEquals(row.getString("person.name"), "John");
        assertEquals(row.getInt("person.zipcode"), cityId.get());

        Map<String, Map<Object, LinkedRow>> byTables = row.groupByTables();
        assertEquals(byTables.get("city").get(1).getString("name"), "Siegburg");
        assertEquals(byTables.get("city").get(1).getString("zipcode"), "53721");
        assertEquals(byTables.get("person").get(1).getString("name"), "John");
        assertEquals(byTables.get("person").get(1).getInt("zipcode"), cityId.get());
    }

    @Test
    public void aConversionRemembersTheResult() {
        Row r = new Row().addKeyVals("a", "1");
        assertEquals(r.get("a"), "1");
        assertEquals(r.getInt("a"), 1);
        assertEquals(r.get("a"), 1);
    }

    @Test
    public void stringToIntConversion() {
        Row r = new Row().addKeyVals("a", "1");
        assertEquals(r.getInt("a"), 1);
    }

    @Test
    public void stringToDateTimeConversion() {
        LocalDateTime dt = new DateTime().toLocalDateTime();
        Row r = Row.fromKeyVals(
                "dateTime", dt.toString()
        );

        LocalDateTime dateTime = r.getDateTime("dateTime").toLocalDateTime();
        assertEquals(dt, dateTime);
    }

}
