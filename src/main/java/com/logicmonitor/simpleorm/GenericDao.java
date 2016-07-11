package com.logicmonitor.simpleorm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by rbtq on 7/7/16.
 */
public class GenericDao<T> {
    protected Class<T> bean = null;
	private static Map<String, String> type2DbType = new HashMap<>();
	static {
		type2DbType.put("Enum", "TINYINT");
		type2DbType.put("Short", "SMALLINT");
		type2DbType.put("Integer", "int");
		type2DbType.put("Long", "bigint");
		type2DbType.put("Float", "float");
		type2DbType.put("Double", "double");
		type2DbType.put("String", "text");
	}

    public GenericDao() {
        Type type = getClass().getGenericSuperclass();
        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        this.bean = (Class<T>) trueType;
		verifyValid();
    }

	private void verifyValid() {
		DBTable table = bean.getAnnotation(DBTable.class);
		if (null == table) {
			throw new IllegalStateException("It's not a DB Bean");
		}

		Stream<Field> fieldStream = Stream.of(bean.getFields());
		final List<Field> Ids = new ArrayList<>();
		boolean allMatch = fieldStream.peek(n->{
			if(n.getAnnotation(Id.class) != null) {
				Ids.add(n);
			}
		}).allMatch(n -> {
			Id id = n.getAnnotation(Id.class);
			if (id == null) {
				return true;
			} else {
				return n.getType().equals(Integer.class);
			}
		});

		if ((!allMatch) || (Ids.size() != 1)) {
			throw new IllegalStateException("Id is not an Integer or Should has one and only one id field");
		}
	}

	protected Field getIdField(){
		Stream<Field> fieldStream = Stream.of(bean.getFields());
		return fieldStream.filter(n->n.getAnnotation(Id.class)!=null)
				          .findFirst()
				          .get();
	}

    protected String getTableName() {
		DBTable table = bean.getAnnotation(DBTable.class);
		return table.value().isEmpty() ? bean.getSimpleName():table.value();
	}

    protected String getFieldSqlDataType(Field field) {
		DBColumn column = field.getAnnotation(DBColumn.class);
		if (!column.dataType().isEmpty()) {
            return column.dataType();
        } else if ((field.getType().equals(String.class)) && column.length() > 0) {
            return String.format("varchar(%d)", column.length());
        } else {
            return type2DbType.get(field.getType().getSimpleName());
        }
    }

    protected String getFieldConstraint(Field field) {
		Id id = field.getAnnotation(Id.class);
		if (null != id) {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("not null");
			switch (id.strategy()) {
				case AUTO_INCREMENT:
					sqlBuilder.append(" AUTO_INCREMENT");
					break;
				default:
					break;
			}

			return sqlBuilder.toString();
		}

		Constraint constraint = field.getAnnotation(Constraint.class);
		if ((null == constraint) || constraint.allowNull()) {
			return "";
		} else {
			return "not null";
		}
	}

    protected String getFieldDefaultValue(Field field) {
		DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
		if (null == defaultValue) {
			return "";
		} else {
			return "default \'" + defaultValue.value() + "\'";
		}
	}

    protected String getFieldName(Field field) {
		DBColumn column = field.getAnnotation(DBColumn.class);
		return column.name().equals("") ? field.getName() : column.name();
	}

    protected String getFieldColumnDesc(Field field) {
		StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder.append(" ").append(getFieldName(field))
				  .append(" ").append(getFieldSqlDataType(field))
				  .append(" ").append(getFieldConstraint(field))
				  .append(" ").append(getFieldDefaultValue(field));

		return sqlBuilder.toString();
	}

	public void createTable() {
		final StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("Create table ").append(getTableName()).append(" (");

		final List<String> primaryKeys = new ArrayList<>();
		Stream<Field> fieldStream = Stream.of(bean.getFields());
		fieldStream.filter(n -> n.getAnnotation(DBColumn.class) != null)
				.peek(n -> {
					Id id = n.getAnnotation(Id.class);
					Constraint constraint = n.getAnnotation(Constraint.class);
					if ((null != id)
					|| ((null != constraint) && constraint.isPrimary())) {
						primaryKeys.add(getFieldName(n));
					}
				})
				.forEach(n -> sqlBuilder.append(" ").append(getFieldColumnDesc(n)).append(','));

		sqlBuilder.append(" primary key (");
		primaryKeys.forEach(n->sqlBuilder.append(n).append(','));
		sqlBuilder.deleteCharAt(sqlBuilder.length()-1); // delete the last ','
		sqlBuilder.append(")) ENGINE=INNODB");

		System.out.println(sqlBuilder.toString());
	}

	public void dropTable() {
		final StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("Drop table ").append(getTableName());
		System.out.println(sqlBuilder.toString());
	}

	public void deleteAll() {
		final StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("Delete from table ").append(getTableName());
		System.out.println(sqlBuilder.toString());
	}

	public void truncate() {
		final StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("Truncate table ").append(getTableName());
		System.out.println(sqlBuilder.toString());
	}

	public List<T> loadAll() throws InstantiationException, IllegalAccessException {
		return load("");
	}

	public T load(Integer id) throws IllegalAccessException, InstantiationException {
		final StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(getFieldName(getIdField()))
		          .append(" = ")
		          .append(id);

        List<T> objects = load(sqlBuilder.toString());
		return objects.isEmpty()? null : objects.get(0);
	}

    public List<T> load(String constraint) throws IllegalAccessException, InstantiationException {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("Select * from table ").append(getTableName());
        if (!constraint.isEmpty()){
            sqlBuilder.append(" Where ").append(constraint);
        }

        System.out.println(sqlBuilder.toString());
        List<T> objects = new LinkedList<>();
        for (;;) {
            T obj = bean.newInstance();
            SetAllFieldValue(obj, null);
            objects.add(obj);
            break;
        }

        return objects;
    }

    protected Object getFieldValueFromRS(Field field, ResultSet resultSet) {
		//TODO: get it from real result set.
		if (field.getType().equals(String.class)) {
			return "test";
		} else if (field.getType().equals(Integer.class)) {
			return 0;
		} else {
			return null;
		}
	}

    protected void SetAllFieldValue(T obj, ResultSet resultSet) {
		Stream<Field> fieldStream = Stream.of(bean.getDeclaredFields());
		fieldStream.filter(n -> n.getAnnotation(DBColumn.class) != null)
				   .forEach(n->{
					   try {
                           n.setAccessible(true);
						   n.set(obj, getFieldValueFromRS(n, resultSet));
					   } catch (IllegalAccessException e) {
						   e.printStackTrace();
					   }
				   });
	}
}
