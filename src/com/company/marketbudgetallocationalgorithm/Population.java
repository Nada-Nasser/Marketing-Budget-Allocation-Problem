package com.company.marketbudgetallocationalgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
        nIterations = 1000;
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

                /*
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

    static private ArrayList<Chromosome> crossOver(ArrayList<Chromosome> selectedParents) {
        return null;
    }

    static  private ArrayList<Chromosome> selection() {
        return null;
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
            System.out.println(chromosome.toString());
            populationChromosomes.add(chromosome);
        }
    }

    static  private void readInputs(){

        totalMarketingBudget = 100;
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
        investmentBoundsHashMap.put("Google" , new Bounds(0.205f,0.0f,true,false));
        investmentBoundsHashMap.put("Twitter" , new Bounds(0.0f,0.18f,false,true));
        investmentBoundsHashMap.put("Facebook" , new Bounds(0.10f,0.0f,true,false));
    }

    static  public String getChannelName(int i)
    {
        return investmentChannels.get(i);
    }

    static  public float getChannelROIlName(String channel)
    {
        return nameROIHashMap.get(channel);
    }



}

