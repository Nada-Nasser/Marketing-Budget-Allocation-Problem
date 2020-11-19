package com.company.marketbudgetallocationalgorithm;

public class Bounds {

    private float upperBound;
    private float lowerBound;
    boolean hasUpper;
    boolean hasLower;

    public Bounds(float lowerBound, float upperBound , boolean hasLower , boolean hasUpper) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.hasUpper = hasUpper;
        this.hasLower = hasLower;
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
