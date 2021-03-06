package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class DummyDto implements com.w11k.lsql.TableRow, Field_AString {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_AString> DummyDto from(T source) {
        Object target = new DummyDto();
        target = ((Field_AString) target).withFieldA(source.getFieldA());
        return (DummyDto) target;
    }

    @SuppressWarnings("unused")
    public static DummyDto fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new DummyDto((java.lang.String) internalMap.get("fieldA"));
    }

    @SuppressWarnings("unused")
    public static DummyDto fromMap(java.util.Map<String, Object> map) {
        return new DummyDto((java.lang.String) map.get("fieldA"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public DummyDto() {
        this.fieldA = null;
    }

    @SuppressWarnings("NullableProblems")
    private DummyDto(
            java.lang.String fieldA) {
        this.fieldA = fieldA;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_FIELDA = "fieldA";

    @SuppressWarnings("unused")
    public static final String FIELD_FIELDA = "fieldA";

    @javax.annotation.Nullable public final java.lang.String fieldA;

    @javax.annotation.Nullable public java.lang.String getFieldA() {
        return this.fieldA;
    }

    public DummyDto withFieldA(@javax.annotation.Nullable java.lang.String fieldA) {
        return new DummyDto(fieldA);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field_AString) target).withFieldA(this.getFieldA());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("fieldA", this.fieldA);
        return map;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("fieldA", this.fieldA);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyDto that = (DummyDto) o;
        return     Objects.equals(fieldA, that.fieldA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldA);
    }

    @Override
    public String toString() {
        return "DummyDto{" + "fieldA=" + fieldA + "}";
    }

}
