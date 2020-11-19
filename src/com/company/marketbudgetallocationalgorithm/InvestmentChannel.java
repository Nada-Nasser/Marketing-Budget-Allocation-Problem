package com.company.marketbudgetallocationalgorithm;

public class InvestmentChannel
{
    private String name;
    private Bounds bounds;
    private float roi;

    public InvestmentChannel(String name, Bounds bounds, float roi) {
        this.name = name;
        this.bounds = bounds;
        this.roi = roi;
    }

    public InvestmentChannel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public float getRoi() {
        return roi;
    }

    public void setRoi(float roi) {
        this.roi = roi;
    }

    @Override
    public String toString() {
        return "InvestmentChannel{" +
                "name='" + name + '\'' +
                ", bounds=" + bounds.toString() +
                ", roi=" + roi +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvestmentChannel)) return false;

        InvestmentChannel that = (InvestmentChannel) o;

        if (Float.compare(that.getRoi(), getRoi()) != 0) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getBounds() != null ? getBounds().equals(that.getBounds()) : that.getBounds() == null;
    }
}
