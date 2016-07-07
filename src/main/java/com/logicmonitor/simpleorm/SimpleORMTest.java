package com.logicmonitor.simpleorm;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SimpleORMTest {
    public static void main(String args[]) {
        SimpleORM<Alert> orm = new SimpleORM<>();
        orm.createTable();
    }

    @DBTable
    private static class Alert{
        @Id
        @DBColumn
        public Integer id;

        @DBColumn
        @DefaultValue
        public String name;
    }
}
