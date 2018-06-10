package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Secret {

    @SerializedName("message")
    @Expose
    private int message;

    /**
     * No args constructor for use in serialization
     *
     */
    public Secret() {
    }

    /**
     *
     * @param message
     */
    public Secret(int message) {
        super();
        this.message = message;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Secret{" + "message=" + message + '}';
    }
}
