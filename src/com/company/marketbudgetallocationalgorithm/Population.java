package com.company.marketbudgetallocationalgorithm;

import java.util.*;
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
    private static ArrayList<InvestmentChannel> investmentChannelArrayList;


    public static void initializeAlgorithm(){
        nIterations = 0;
        nPopulation = 1000;
        nParents = 100;

        int nTests = 1; // TODO nTests = 20;

        readInputs();
        buildChromosomes();

        int i = 0;
        for(int t = 0 ; t < nTests ; t++) {
            do {
                i++;
                ArrayList<Chromosome> selectedParents = selection();// tournament selection
                ArrayList<Chromosome> offspringChromosomes = crossOver(selectedParents); // 2-point crossover

                // NOTE : offspringChromosomes.size != nParents

                System.out.println("\nOffSpring Chromosomes:\n");
                for (Chromosome chromosome : offspringChromosomes)
                {
                    System.out.println(chromosome.toString());
                }
                System.out.println("\n===================================================\n");


                mutation(offspringChromosomes);//TODO both uniform and non-uniform mutation.
                Replacement(offspringChromosomes);//TODO elitist replacement.

            } while (i <= nIterations);
            finalOutput(); // TODO
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
                //perform crossover at Xc using  i and i+1 parents
                ArrayList<Chromosome> crossoverOutput = selectedParents.get(i)
                        .crossOver(selectedParents.get(i+1), xc);
                offspringChromosomes.addAll(crossoverOutput);
            }
            else //then No CrossOver
            {
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
                Bounds bounds = getChannelBounds(channel);
                float max = bounds.hasUpper ? (bounds.getUpperBound()*remainingBudget): remainingBudget;
                float min = bounds.hasLower ? (bounds.getLowerBound()*remainingBudget): 0.0f;

                float budget =   (float)Math.random() * (max - min) + min;
                genes.add(new Gene(budget));
                remainingBudget -= budget;
            }

            Chromosome chromosome = new Chromosome(genes); // in the constructor, the fitness value evaluated also
            populationChromosomes.add(chromosome);
        }
    }

    static  private void readInputs(){

        investmentChannelArrayList = new ArrayList<>();

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the marketing budget (in thousands):");
        totalMarketingBudget = in.nextFloat();
        System.out.println("\nEnter the number of marketing channels:");
        nMarketingChannels = in.nextInt();

        System.out.println("\nEnter the name and ROI (in %) of each channel separated by space:");
        for(int i = 0 ; i < nMarketingChannels ; i++)
        {
            String channelName = in.next();
            Float roi = in.nextFloat();

           investmentChannelArrayList.add(new InvestmentChannel(channelName));
           investmentChannelArrayList.get(i).setRoi(roi/100);
        }

        System.out.println("Enter the lower (k) and upper bounds (%) of investment in each channel:" +
                "(enter x if there is no bound)");
        for(int i = 0 ; i < nMarketingChannels ; i++)
        {
           String l = in.next();
           String u = in.next();
           float lower = l.equalsIgnoreCase("x")? Float.NEGATIVE_INFINITY : Float.parseFloat(l)/100;
           float upper = u.equalsIgnoreCase("x")?Float.POSITIVE_INFINITY : Float.parseFloat(u)/100;
           Bounds bounds = new Bounds(lower,upper,!l.equalsIgnoreCase("x"),!u.equalsIgnoreCase("x"));

           investmentChannelArrayList.get(i).setBounds(bounds);
        }
    }

    static  public Bounds getChannelBounds(int channelIndex)
    {
        return investmentChannelArrayList.get(channelIndex).getBounds();
    }

    static  public float getChannelROIlName(int channelIndex)
    {
        return investmentChannelArrayList.get(channelIndex).getRoi();
    }

    static  public String getChannelName(int i)
    {
        return investmentChannelArrayList.get(i).getName();
    }

    static  public float getTotalMarketingBudget()
    {
        return totalMarketingBudget;
    }

}

