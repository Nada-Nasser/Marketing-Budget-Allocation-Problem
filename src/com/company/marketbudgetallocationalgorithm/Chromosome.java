package com.company.marketbudgetallocationalgorithm;

import java.util.ArrayList;

public class Chromosome {

    private ArrayList<Gene> genes; // array size = number of channel budgets
    private float fitnessValue;

    public Chromosome(ArrayList<Gene> genes) {
        this.genes = genes;
        this.fitnessValue = calculateFitnessValue();
    }

    private float calculateFitnessValue() {
        float fitness = 0.0f;

        int channelIndex = 0;

        for(Gene gene : genes)
        {
            String channelName = Population.getChannelName(channelIndex++);
            float channelROI = Population.getChannelROIlName(channelName);
            fitness+= (channelROI*gene.getBudget());
        }
        return fitness;
    }

    public ArrayList<Gene> getChromosomeGenes() {
        return genes;
    }

    public void setChromosomeGenes(ArrayList<Gene> chromosomeGenes) {
        this.genes = chromosomeGenes;
    }

    public float getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(float fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    @Override
    public String toString()
    {
        String genesStr = "[";

        for(int i = 0 ; i < genes.size() ; i++)
        {
            genesStr+= (genes.get(i).getBudget())+"|";
        }

        return "Chromosome: genes=" + genesStr + "]\n"
                + "fitnessValue=" + fitnessValue + "\n";
    }

    public ArrayList<Chromosome> crossOver(Chromosome other , int x) // TODO crossOver
    {
        return null;
    }

}
