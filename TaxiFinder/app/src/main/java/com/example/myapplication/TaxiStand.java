package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;

public class TaxiStand {
    public double lng;
    public double lat;
    String description ;

    public TaxiStand(){}
    public TaxiStand(LatLng Coordinate, String description){
        this.lng = Coordinate.longitude;
        this.lat = Coordinate.latitude;
        this.description = description;
    }

    public String getdescription(){
        return description;
    }
}
