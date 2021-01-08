package com.programmer.zuul.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 登录用户实体
 *
 * @author dengweiping
 * @date 2021/1/8 16:30
 */
@Entity
@Data
@Table(name = "login_user")
public class LoginUser {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "is_valid")
    private boolean isValid;
}
