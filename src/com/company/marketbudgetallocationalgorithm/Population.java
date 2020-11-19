package com.company.marketbudgetallocationalgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    final String OUTPUT_FILE_PATH = "Output.txt";

    private static int nPopulation;
    private static int nParents;
    private static int nIterations;

    final private static float Pc =  0.7f; //Crossover probability
    final private static float Pm = 0.1f; // mutation value

    private static ArrayList<Chromosome> populationChromosomes;

    // user inputs
    private static float totalMarketingBudget;
    private static int nMarketingChannels;
    private static HashMap<String , Float> nameROIHashMap;
    private static HashMap<String , Bounds> investmentBoundsHashMap;
    private static ArrayList<String> investmentChannels;


    public static void initializeAlgorithm(){
        nIterations = 0;
        nPopulation = 100;
        nParents = 10;

        int nTests = 1; // TODO nTests = 20;

        readInputs();
        buildChromosomes();

        int i = 0;
        for(int t = 0 ; t < nTests ; t++) {
            do {
                i++;
                ArrayList<Chromosome> selectedParents = selection();// tournament selection
                ArrayList<Chromosome> offspringChromosomes = crossOver(selectedParents); // 2-point crossover

                for (Chromosome chromosome : offspringChromosomes)
                {
                    System.out.println(chromosome.toString());
                }

                /**
                mutation(offspringChromosomes);//TODO both uniform and non-uniform mutation.
                Replacement(offspringChromosomes);//TODO elitist replacement.
                */
            } while (i <= nIterations);
            finalOutput();
        }
    }

    private static void finalOutput() {
    }

    static private void Replacement(ArrayList<Chromosome> offspringChromosomes) {
    }

    static private void mutation(ArrayList<Chromosome> offspringChromosomes) {
    }

    /**
     * @implNote 2-point crossover
     * @return ArrayList<Chromosome>, contains the result of cross over between the selected parents arraylist
     * **/
    static private ArrayList<Chromosome> crossOver(ArrayList<Chromosome> selectedParents) {
        ArrayList<Chromosome> offspringChromosomes = new ArrayList<Chromosome>();

        for(int i = 0  ; i <nParents/2 ; i+=2) // cross over between i and i+1
        {
            Random r = new Random();
            int xc = r.nextInt(selectedParents.size());
            float rc =  r.nextFloat();

            if(rc <= Pc) //then Cross over occurs
            {
        //        System.out.println("Apply Cross Over");
                //perform crossover at Xc using  i and i+1 parents
                ArrayList<Chromosome> crossoverOutput = selectedParents.get(i)
                        .crossOver(selectedParents.get(i+1), xc);
                offspringChromosomes.addAll(crossoverOutput);
            }
            else //then No CrossOver
            {
         //       System.out.println("DON'T Apply Cross Over");
                offspringChromosomes.add(selectedParents.get(i));
                offspringChromosomes.add(selectedParents.get(i+1));
            }
        }
        return offspringChromosomes;
    }

    /**
     * @implNote
     * 1- selecting 2 Chromosomes from the population randomly
     * 2- select the best of them (max fitness value) to be as one of selectedParents
     * 3- repeat 1,2 until you fill selectedParents array (selectedParents = nParents)
     *
     * @return selected parents from population Chromosomes, using 2-way tournament selection method
     * **/
    static  private ArrayList<Chromosome> selection() {
        ArrayList<Chromosome> selectedParents = new ArrayList<>();
        while(selectedParents.size() < nParents)
        {
            Random rand = new Random();
            int i = rand.nextInt(populationChromosomes.size());
            int j = rand.nextInt(populationChromosomes.size());

            Chromosome parentChromosome = populationChromosomes.get(i).getFitnessValue() > populationChromosomes.get(j).getFitnessValue()
                                         ?populationChromosomes.get(i) : populationChromosomes.get(j);
            selectedParents.add(parentChromosome);
        }

        return selectedParents;
    }

    static private void buildChromosomes() {
        populationChromosomes = new ArrayList<Chromosome>();

        for(int i = 0 ; i < nPopulation ; i++) // for each chromosome generate random genes.
        {
            float remainingBudget = totalMarketingBudget;
            ArrayList<Gene> genes = new ArrayList<Gene>();

            for(int channel = 0 ; channel < nMarketingChannels; channel++) // generate random genes
            {
                Bounds bounds = investmentBoundsHashMap.get(getChannelName(channel));
                float max = bounds.hasUpper ? (bounds.getUpperBound()*remainingBudget): remainingBudget;
                float min = bounds.hasLower ? (bounds.getLowerBound()*remainingBudget): 0.0f;

                float budget =   (float)Math.random() * (max - min) + min;
                genes.add(new Gene(budget));
                remainingBudget -= budget;
            }

            Chromosome chromosome = new Chromosome(genes); // in the constructor, the fitness value evaluated also
         //   System.out.println(chromosome.toString());
            populationChromosomes.add(chromosome);
        }
    }

    static  private void readInputs(){

        totalMarketingBudget = 1000;
        nMarketingChannels = 4;

        investmentChannels = new ArrayList<>();
        investmentChannels.add("TV");
        investmentChannels.add("Google");
        investmentChannels.add("Twitter");
        investmentChannels.add("Facebook");

        nameROIHashMap = new HashMap<>();
        nameROIHashMap.put("TV" , 0.08f);
        nameROIHashMap.put("Google" , 0.12f);
        nameROIHashMap.put("Twitter" , 0.07f);
        nameROIHashMap.put("Facebook" , 0.11f);

        investmentBoundsHashMap = new HashMap<>();
        investmentBoundsHashMap.put("TV" , new Bounds(0.027f,0.58f,true,true));
        investmentBoundsHashMap.put("Google" , new Bounds(0.205f,Float.POSITIVE_INFINITY,true,false));
        investmentBoundsHashMap.put("Twitter" , new Bounds(Float.NEGATIVE_INFINITY,0.18f,false,true));
        investmentBoundsHashMap.put("Facebook" , new Bounds(0.10f,Float.POSITIVE_INFINITY,true,false));
    }

    static  public String getChannelName(int i)
    {
        return investmentChannels.get(i);
    }

    static  public float getChannelROIlName(String channel)
    {
        return nameROIHashMap.get(channel);
    }

    static  public Bounds getChannelBounds(String channel)
    {
        return investmentBoundsHashMap.get(channel);
    }

    static  public float getTotalMarketingBudget()
    {
        return totalMarketingBudget;
    }


}

