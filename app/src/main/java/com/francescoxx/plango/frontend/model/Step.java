package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FrancescoXX on 11/09/17.
 */

public class Step {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("daytrip")
    @Expose
    private Daytrip daytrip;

    /**
     * No args constructor for use in serialization
     */
    public Step() {
    }

    /**
     * @param daytrip
     * @param description
     * @param name
     * @param longitude
     * @param latitude
     */

    //No need for id in the constructor, id is autoincremented
    public Step(String name, String description, Double latitude, Double longitude, Daytrip daytrip) {
        super();
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.daytrip = daytrip;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Daytrip getDaytrip() {
        return daytrip;
    }

    public void setDaytrip(Daytrip daytrip) {
        this.daytrip = daytrip;
    }

    @Override
    public String toString() {
        return "Step{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + ", daytrip=" + daytrip + '}';
    }
}
