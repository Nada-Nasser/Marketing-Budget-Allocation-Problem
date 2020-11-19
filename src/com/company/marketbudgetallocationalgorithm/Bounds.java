package com.company.marketbudgetallocationalgorithm;

public class Bounds {

    private float upperBound;
    private float lowerBound;
    boolean hasUpper;
    boolean hasLower;

    public Bounds(float upperBound, float lowerBound , boolean hasUpper , boolean hasLower) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.hasUpper = false;
        this.hasLower = false;
    }



    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }
}
