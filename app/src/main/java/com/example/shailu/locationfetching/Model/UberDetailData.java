package com.example.shailu.locationfetching.Model;

/**
 * Created by shailu on 5/5/16.
 */
public class UberDetailData {

    public String product_id;
    public String display_name;
    public String priceEstimate;
    public float surge_multiplier;
    public int pickupTime;
    public double perKm;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getPriceEstimate() {
        return priceEstimate;
    }

    public void setPriceEstimate(String priceEstimate) {
        this.priceEstimate = priceEstimate;
    }

    public float getSurge_multiplier() {
        return surge_multiplier;
    }

    public void setSurge_multiplier(float surge_multiplier) {
        this.surge_multiplier = surge_multiplier;
    }

    public int getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(int pickupTime) {
        this.pickupTime = pickupTime;
    }

    public double getPerKm() {
        return perKm;
    }

    public void setPerKm(double perKm) {
        this.perKm = perKm;
    }
}
