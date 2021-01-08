package com.programmer.zuul.domain;

import lombok.Data;

import java.util.Date;

/**
 * token实体
 *
 * @author dengweiping
 * @date 2021/1/8 17:00
 */
@Data
public class Token {
    private Integer id;
    private String openId;
    private String role;
    private Date lastLogin;
}
