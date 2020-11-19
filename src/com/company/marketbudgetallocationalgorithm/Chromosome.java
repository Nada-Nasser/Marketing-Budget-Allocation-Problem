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
                + "fitnessValue=" + fitnessValue + "\n" + "is Feasible = " + isFeasible() + "\n";
    }

    public ArrayList<Chromosome> crossOver(Chromosome other , int x) // TODO crossOver
    {
        int[] nPoints; // length = 2
        nPoints = ThreadLocalRandom.current().ints(0, 4).distinct().limit(2).toArray();
        Arrays.sort(nPoints);
/*
        for(int i = 0 ; i < nPoints.length ; i++)
        {
            System.out.print(nPoints[i] + " ");
        }
        System.out.println();
*/
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
            String channelName = Population.getChannelName(geneIndex);
            Bounds bounds = Population.getChannelBounds(channelName);
          //  System.out.println(channelName);
            if (bounds.hasUpper)
            {
           //     System.out.println("HAS UPPER  CONSTRAINTS");
                if (gene.getBudget() > bounds.getUpperBound()*totalBudget)
                {
           //         System.out.println("UPPER BOUND CONSTRAINTS for " + channelName);
                    return false;
                }
                else
                {
           //         System.out.println(gene.getBudget()+ " < " + bounds.getUpperBound()*totalBudget);
                }
            }

            if (bounds.hasLower)
            {
             //   System.out.println("has LOWER CONSTRAINTS");
                if (gene.getBudget() < bounds.getLowerBound()*totalBudget)
                {
            //        System.out.println("LOWER BOUND CONSTRAINTS for " + channelName);
                    return false;
                }
                else
                {
           //         System.out.println(gene.getBudget()+ " < " + bounds.getLowerBound()*totalBudget);
                }
            }

            sumBudget+= gene.getBudget();
            geneIndex++;
            if(sumBudget > totalBudget)
            {
            //    System.out.println("totalBudget CONSTRAINTS");
                return false;
            }
        }
        return true;
    }

}