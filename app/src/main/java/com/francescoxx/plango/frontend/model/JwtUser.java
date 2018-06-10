package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JwtUser {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("dailyTours")
    @Expose
    private Object dailyTours;

    /**
     * No args constructor for use in serialization
     *
     */
    public JwtUser() {
    }

    public JwtUser(String userName, String password, String role, Object dailyTours) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.dailyTours = dailyTours;
    }

    public JwtUser(Integer id) {
        this.id = id;
    }

    /**
     *
     * @param id
     * @param dailyTours
     * @param role
     * @param userName
     * @param password
     */
    public JwtUser(Integer id, String userName, String password, String role, Object dailyTours) {
        super();
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.dailyTours = dailyTours;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Object getDailyTours() {
        return dailyTours;
    }

    public void setDailyTours(Object dailyTours) {
        this.dailyTours = dailyTours;
    }

    @Override
    public String toString() {
        return "JwtUser{" + "id=" + id + ", userName='" + userName + '\'' + ", password='" + password + '\'' + ", role='" + role + '\'' + ", dailyTours=" + dailyTours + '}';
    }
}
