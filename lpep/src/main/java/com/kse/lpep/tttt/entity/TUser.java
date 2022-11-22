package com.kse.lpep.tttt.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (TUser)实体类
 *
 * @author makejava
 * @since 2022-11-22 14:30:51
 */
public class TUser implements Serializable {
    private static final long serialVersionUID = -25274270080678977L;
    /**
     * 表主键，用户表，保存所有用户和管理员
     */
    private String id;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户密码，sha256加密
     */
    private String password;
    /**
     * 记录用户真实姓名
     */
    private String realname;
    /**
     * 判断用户是否为管理员
     */
    private Integer isAdmin;
    /**
     * 用户创建时间
     */
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}

