package com.company.marketbudgetallocationalgorithm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Chromosome {

    private ArrayList<Gene> genes; // array size = number of channel budgets
    private float fitnessValue;

    public Chromosome(ArrayList<Gene> genes) {
        this.genes = genes;
        this.fitnessValue = calculateFitnessValue();
    }

    public float calculateFitnessValue() {
        float fitness = 0.0f;

        int channelIndex = 0;

        for(Gene gene : genes)
        {
            float channelROI = Population.getChannelROIlName(channelIndex++);
            fitness+= (channelROI*gene.getBudget());
        }
        return fitness;
    }

    public ArrayList<Gene> getChromosomeGenes() {
        return genes;
    }

    public void setChromosomeGenes(ArrayList<Gene> chromosomeGenes) {
        this.genes = chromosomeGenes;
        this.fitnessValue = calculateFitnessValue();
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
                + "fitnessValue=" + fitnessValue + "\n" + "is Feasible = " + isFeasible() + "\n";
    }

    public ArrayList<Chromosome> crossOver(Chromosome other , int x) // TODO crossOver
    {
        int[] nPoints; // length = 2
        nPoints = ThreadLocalRandom.current().ints(0, 4).distinct().limit(2).toArray();
        Arrays.sort(nPoints);

        ArrayList<Gene> genesO1 = new ArrayList<Gene>();
        ArrayList<Gene> genesO2 = new ArrayList<Gene>();

        // apply cross over
        applyCrossOver(other , genesO1 , genesO2 , 0 , nPoints[0]);
        applyCrossOver(other , genesO2 , genesO1 , nPoints[0] , nPoints[1]);
        applyCrossOver(other , genesO1 , genesO2 , nPoints[1] , genes.size());

        Chromosome O1 = new Chromosome(genesO1);
        Chromosome O2 = new Chromosome(genesO2);
        ArrayList<Chromosome> output = new ArrayList<Chromosome>();
        output.add(O1);
        output.add(O2);

        return output;
    }

    private void applyCrossOver(Chromosome other, ArrayList<Gene> genesO1, ArrayList<Gene> genesO2, int startIndex, int endIndex) {
        for(int i = startIndex ; i < endIndex ; i++)
        {
            genesO1.add(this.genes.get(i));
            genesO2.add(other.genes.get(i));
        }
    }

    public boolean isFeasible()
    {
        float sumBudget = 0.0f;
        int geneIndex = 0;
        float totalBudget = Population.getTotalMarketingBudget();

        for (Gene gene : genes)
        {
            Bounds bounds = Population.getChannelBounds(geneIndex);
            if (gene.getBudget() > bounds.getUpperBound()*totalBudget)
                   return false;

           if (gene.getBudget() < bounds.getLowerBound()*totalBudget)
               return false;

           sumBudget+= gene.getBudget();

           if(sumBudget > totalBudget)
               return false;

           geneIndex++;
        }
        return true;
    }

}
