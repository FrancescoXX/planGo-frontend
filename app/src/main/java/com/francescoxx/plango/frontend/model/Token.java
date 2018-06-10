package com.francescoxx.plango.frontend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("theToken")
    @Expose
    private String theToken;

    /**
     * No args constructor for use in serialization
     *
     */
    public Token() {
    }

    /**
     *
     * @param theToken
     */
    public Token(String theToken) {
        super();
        this.theToken = theToken;
    }

    public String getTheToken() {
        return theToken;
    }

    @Override
    public String toString() {
        return "Token.toString() = " + theToken;
    }
}
