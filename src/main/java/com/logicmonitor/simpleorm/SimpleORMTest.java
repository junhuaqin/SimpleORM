package com.logicmonitor.simpleorm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SimpleORMTest {
    public static void main(String args[]) throws InstantiationException, IllegalAccessException {
        List<Alert> ols = new ArrayList<>();
        Alert alert = new Alert();
        alert.id = 0;
        alert.name = "test";
        ols.add(alert);
        System.out.println(ols);
        List<Alert> newls = new ArrayList<>();
        for (Alert al : ols) {
            newls.add(al);
        }
        System.out.println(newls);
        Alert ass = newls.get(0);
        ass.name = "changed";
        System.out.println(newls);
        System.out.println(ols);
//        AlertDao orm = new AlertDao();
//        orm.createTable();
//        orm.dropTable();
//        orm.deleteAll();
//        orm.truncate();
//        Alert a = orm.load(9);
//        System.out.println(a);
//        a.name = "xxx";
//        System.out.println(a);
//        orm.dumpCache();
//        System.out.println(orm.loadAll());
    }

    @DBTable("alerts")
    public static class Alert implements Cacheable<Alert>, Cloneable{
        @Id
        @DBColumn
        public Integer id;

        @DBColumn
        @Constraint(allowNull = false)
        public String name;

        @DBColumn(length = 10)
        @Constraint(allowNull = false)
        @DefaultValue("test")
        private String desc;


        @Override
        public String toString() {
            return "Alert{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }

        @Override
        public Alert cloneObj() {
            Alert o = null;
            try {
                o = (Alert) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return o;
        }
    }

    private static class AlertDao extends GenericCachedDao<Alert> {
        public AlertDao() {
            super(new LRUCache<>(10));
        }
    }
}
