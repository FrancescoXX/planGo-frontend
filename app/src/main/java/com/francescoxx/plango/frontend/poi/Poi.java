package com.francescoxx.plango.frontend.poi;

import android.support.annotation.NonNull;

import com.francescoxx.plango.frontend.model.Step;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FrancescoXX on 11/09/17.
 */

public class Poi implements Comparable<Poi>{

    //distance from user
    private double distanzaDaUtente;

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

    /**
     * No args constructor for use in serialization
     */
    public Poi() {
    }

    /**
     * @param id
     * @param description
     * @param name
     * @param longitude
     * @param latitude
     */
    public Poi(Integer id, String name, String description, Double latitude, Double longitude) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "Poi{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    //utilizzato da Collections.sort in ListActivity
    //confronta due Colonnine con il campo distanzaUtente
    @Override
    public int compareTo(@NonNull Poi altraColonnina) {
        return Double.compare(this.getDistanzaDaUtente(), altraColonnina.getDistanzaDaUtente());
    }

    //Calcola la distanza da un POI. in questo progetto viene utilizzata solamente per la distanza dall'utente
    //Metodo chiamato in listActivity
    public double calcolaDist(Step u){

        double to_ret;
        double latUtente = Math.toRadians(u.getLatitude());
        double lonUtente = Math.toRadians(u.getLongitude());

        double lat2 = Math.toRadians(this.getLatitude());
        double lon2 = Math.toRadians(this.getLongitude());

        //formula dell'emisenoverso
        to_ret = 6371000 * Math.acos(Math.cos(lat2) * Math.cos(latUtente) * Math.cos(lonUtente - lon2) + Math.sin(lat2) * Math.sin(latUtente));
        return (to_ret/1000); //per esprimere in km
    }

    public double getDistanzaDaUtente() {
        return distanzaDaUtente;
    }

    public void setDistanzaDaUtente(double distanzaDaUtente) {
        this.distanzaDaUtente = distanzaDaUtente;
    }
}
