package com.example.myapplication;

public class FavouritesList {

    String addresstitle, address, keyfavourites;

    public FavouritesList() {
    }

    public FavouritesList(String addresstitle, String address, String keyfavourites) {
        this.addresstitle = addresstitle;
        this.address = address;
        this.keyfavourites = keyfavourites;
    }

    public String getKeyfavourites() {
        return keyfavourites;
    }

    public void setKeyfavourites(String keyfavourites) {
        this.keyfavourites = keyfavourites;
    }


    public String getAddresstitle() {
        return addresstitle;
    }

    public void setAddresstitle(String addresstitle) {
        this.addresstitle = addresstitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
