package com.logicmonitor.simpleorm;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SimpleORMTest {
    public static void main(String args[]) {
        AlertDao orm = new AlertDao();
        orm.createTable();
    }

    @DBTable
    private static class Alert{
        @Id
        @DBColumn
        public Integer id;

        @DBColumn
        @Constraint(allowNull = false)
        @DefaultValue
        public String name;
    }

    private static class AlertDao extends GenericDao<Alert> {

    }
}
