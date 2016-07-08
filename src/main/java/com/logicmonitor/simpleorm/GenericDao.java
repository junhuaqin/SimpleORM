package com.logicmonitor.simpleorm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by rbtq on 7/7/16.
 */
public class GenericDao<T> {
    protected Class<T> bean = null;

    public GenericDao() {
        Type type = getClass().getGenericSuperclass();
        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        this.bean = (Class<T>) trueType;
    }

	private String getTableName() {
		DBTable table = bean.getAnnotation(DBTable.class);
		if (null == table) {
			return bean.getSimpleName();
		} else {
			return table.value();
		}
	}

	private String getFieldSqlDataType(Field field) {
		DBColumn column = field.getAnnotation(DBColumn.class);
		if (!column.dataType().isEmpty()) {
			return column.dataType();
		} else {
			// TODO: translate to sql data type
			return "text";
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
		DBTable table = bean.getAnnotation(DBTable.class);
		if (null == table) {
			throw new IllegalStateException("It's not a DB Bean");
		}
		String tableName = table.value().isEmpty()?bean.getSimpleName():table.value();
		sqlBuilder.append("Create table ").append(tableName).append(" (");
		final List<String> primaryKeys = new ArrayList<>();
		Field[] fields = bean.getFields();
		Stream<Field> fieldStream = Stream.of(fields);
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
