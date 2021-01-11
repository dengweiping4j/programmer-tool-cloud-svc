package com.programmer.util.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * 数据连接
 *
 * @author dengweiping
 * @date 2021-01-11
 */
@Entity
@Data
@Table(name = "data_connection")
public class DataConnection implements Serializable, Cloneable {

    private static final long serialVersionUID = -1658610148237674132L;

    public static final String PROPERTY_IP = "ip";
    public static final String PROPERTY_PORT = "port";
    public static final String PROPERTY_DATABASE = "database";
    public static final String PROPERTY_USERNAME = "username";
    public static final String PROPERTY_SCHEMA = "schema";

    public static final String MYSQL = "MySQL";
    public static final String POSTGRESQL = "PostgreSQL";
    public static final String ORACLE = "Oracle";
    public static final String SQL_SERVER = "SQLServer";
    public static final String T_BASE = "TBase";

    public static final String ODS = "ODS";
    public static final String BIZ = "BIZ";
    public static final String STS = "STS";
    public static final String DWS = "DWS";

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @javax.persistence.Column(name = "id")
    private String id;

    /**
     * 命名
     */
    @javax.persistence.Column(name = "name")
    private String name;

    /**
     * 类型
     */
    @javax.persistence.Column(name = "type")
    private String type;

    /**
     * 描述
     */
    @javax.persistence.Column(name = "description")
    private String description;

    /**
     * 连接属性
     */
    @Column(name = "property")
    private String property;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataConnection that = (DataConnection) o;

        JSONObject object = JSONObject.parseObject(property);
        JSONObject thatObject = JSONObject.parseObject(that.property);

        return Objects.equals(type, that.type) &&
                Objects.equals(object.getString(DataConnection.PROPERTY_IP), thatObject.getString(DataConnection.PROPERTY_IP)) &&
                Objects.equals(object.getString(DataConnection.PROPERTY_PORT), thatObject.getString(DataConnection.PROPERTY_PORT)) &&
                Objects.equals(object.getString(DataConnection.PROPERTY_DATABASE), thatObject.getString(DataConnection.PROPERTY_DATABASE)) &&
                Objects.equals(object.getString(DataConnection.PROPERTY_USERNAME), thatObject.getString(DataConnection.PROPERTY_USERNAME)) &&
                Objects.equals(object.getString(DataConnection.PROPERTY_SCHEMA), thatObject.getString(DataConnection.PROPERTY_SCHEMA));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, type, property);
    }

}
