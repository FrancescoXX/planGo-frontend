package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Daytrip {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;


    /**
     * No args constructor for use in serialization
     *
     */
    public Daytrip() {
    }

    public Daytrip(Integer id) {
        this.id = id;
    }

    /**
     *
     * @param description
     * @param name
     */

    public Daytrip(String name, String description) {
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "Daytrip{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + '}';
    }
}