package com.logicmonitor.simpleorm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by rbtq on 7/7/16.
 */
public class GenericDao<T> {
    protected Class<T> bean = null;
	private static Map<String, String> type2DbType = new HashMap<>();
	static {
		type2DbType.put("Integer", "int");
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

	private String getTableName() {
		DBTable table = bean.getAnnotation(DBTable.class);
		return table.value().isEmpty() ? bean.getSimpleName():table.value();
	}

	private String getFieldSqlDataType(Field field) {
		DBColumn column = field.getAnnotation(DBColumn.class);
		if (!column.dataType().isEmpty()) {
			return column.dataType();
		} else {
			if ((field.getType().equals(String.class)) && column.length() > 0) {
				return String.format("varchar(%d)", column.length());
			} else {
				return type2DbType.get(field.getType().getSimpleName());
			}
		}
	}

	private String getFieldConstraint(Field field) {
		Id id = field.getAnnotation(Id.class);
		if (null != id) {
			return "not null";
		}

		Constraint constraint = field.getAnnotation(Constraint.class);
		if ((null == constraint) || constraint.allowNull()) {
			return "";
		} else {
			return "not null";
		}
	}

	private String getFieldDefaultValue(Field field) {
		DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
		if (null == defaultValue) {
			return "";
		} else {
			return "default \'" + defaultValue.value() + "\'";
		}
	}

	private String getFieldName(Field field) {
		DBColumn column = field.getAnnotation(DBColumn.class);
		return column.name().equals("") ? field.getName() : column.name();
	}

	private String getFieldColumnDesc(Field field) {
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

	}

	public void deleteAll() {

	}

	public void trunct() {

	}

	public List<T> loadAll() {
		return null;
	}

	public T load(Integer id) throws IllegalAccessException, InstantiationException {
		return bean.newInstance();
	}
}
