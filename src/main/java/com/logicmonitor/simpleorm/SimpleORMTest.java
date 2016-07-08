package com.logicmonitor.simpleorm;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SimpleORMTest {
    public static void main(String args[]) throws InstantiationException, IllegalAccessException {
        AlertDao orm = new AlertDao();
        orm.createTable();
        orm.dropTable();
        orm.deleteAll();
        orm.truncate();
        System.out.println(orm.load(9));
        System.out.println(orm.loadAll());
    }

    @DBTable("alerts")
    public static class Alert{
        @Id
        @DBColumn
        public Integer id;

        @DBColumn
        @Constraint(allowNull = false)
        public String name;

        @DBColumn(length = 10)
        @Constraint(allowNull = false)
        @DefaultValue("test")
        public String desc;


        @Override
        public String toString() {
            return "Alert{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }

    private static class AlertDao extends GenericDao<Alert> {

    }
}
