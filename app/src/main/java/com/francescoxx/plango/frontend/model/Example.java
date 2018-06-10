package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FrancescoXX on 11/09/17.
 */

public class Example {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    /**
     * No args constructor for use in serialization
     */
    public Example() {
    }

    /**
     * @param id
     * @param description
     * @param userId
     * @param name
     */
    public Example(Integer id, String name, String description, Integer userId) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Example{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", userId=" + userId + '}';
    }
}

