package com.programmer.util.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.*;

/**
 * @description:
 * @author: DengWeiPing
 * @time: 2020/8/21 09:42
 */
@Entity
@Data
@Table(name = "driver_path")
public class DriverPath {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @javax.persistence.Column(name = "id")
    private String id;

    @javax.persistence.Column(name = "driver")
    private String driver;

    @javax.persistence.Column(name = "path")
    private String path;

    @Column(name = "type")
    private String type;
}
