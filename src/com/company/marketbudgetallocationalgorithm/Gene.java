package com.company.marketbudgetallocationalgorithm;

public class Gene {
    private float budget;

    public Gene(float budget) {
        this.budget = budget;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "budget=" + budget +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gene)) return false;

        Gene gene = (Gene) o;

        return Float.compare(gene.getBudget(), getBudget()) == 0;
    }
}
