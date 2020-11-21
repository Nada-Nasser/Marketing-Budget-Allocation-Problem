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
    final private static float b = 3.46f; //

    private static ArrayList<Chromosome> populationChromosomes;

    private static ArrayList<Chromosome> newGenSelectionPool;

    // user inputs
    private static float totalMarketingBudget;
    private static int nMarketingChannels, iIteration;
    private static ArrayList<InvestmentChannel> investmentChannelArrayList;



    public static void initializeAlgorithm(){
        nIterations = 1;
        nPopulation = 100;
        nParents = 10;

        int nTests = 1; // TODO nTests = 20;

        readInputs();
        buildChromosomes();

        for(int t = 0 ; t < nTests ; t++) {
            iIteration = 0;
            do {
                iIteration++;
                ArrayList<Chromosome> selectedParents = selection();// tournament selection
                ArrayList<Chromosome> offspringChromosomes = crossOver(selectedParents); // 2-point crossover

                // NOTE : offspringChromosomes.size != nParents

                System.out.println("\nOffSpring Chromosomes:\n");
                for (Chromosome chromosome : offspringChromosomes)
                {
                    System.out.println(chromosome.toString());
                }
                System.out.println("\n===================================================\n");


                mutation(offspringChromosomes);
                Replacement(offspringChromosomes, selectedParents);

            } while (iIteration <= nIterations);
            finalOutput(); // TODO
        }
    }

    private static void finalOutput() {

    }

    /**
     * @implNote Elitism Replacement
     *      * @implNote
     *      * Elitism Replacement :
     *      * 1- we select the best so far chromosomes between both parents and offsprings
     *      * 2- and send it to new generation
     *      *
     *      * at the end I edit the selectedParents array to include the best-so-far chromosomes for next gen
     *
     * **/

    static private void Replacement(ArrayList<Chromosome> offspringChromosomes, ArrayList<Chromosome> selectedParents) {
        newGenSelectionPool = new ArrayList<>();
        newGenSelectionPool.addAll(offspringChromosomes);
        newGenSelectionPool.addAll(selectedParents);
        ArrayList<Chromosome> newGenSelected = new ArrayList<>();
        Chromosome bestChromo = newGenSelectionPool.get(0);
        for(int i=0; i<nParents; i++){
            for(int y=0; y<newGenSelectionPool.size(); y++){
                if(newGenSelectionPool.get(y).getFitnessValue() > bestChromo.getFitnessValue()) bestChromo = newGenSelectionPool.get(y);
            }
            newGenSelected.add(bestChromo);
            newGenSelectionPool.remove(bestChromo);
            bestChromo = newGenSelectionPool.get(0);
        }
        selectedParents.clear();
        selectedParents.addAll(newGenSelected);
    }

    /**
     * @implNote mutation
     *      * @implNote
     *      * mutation :
     *      * 1- two mutation methods are implemented, uniform and non-uniform
     *      * 2- generate random number and compare it to Pm, if it is below Pm then mutation occurs.
     *      * 3- get the bounds for the selected gene
     *      * 4- send the selected gene to perform mutation to either uniform or nonuniform mutation functions.
     *
     * **/
    static private void mutation(ArrayList<Chromosome> offspringChromosomes) {
        ArrayList<Gene> ChromosomeGenes = new ArrayList<>();
        Gene t;
        for(Chromosome x : offspringChromosomes) {
            ChromosomeGenes = new ArrayList<>();
            for(Gene z: x.getChromosomeGenes()){
                t = new Gene(z.getBudget());
                ChromosomeGenes.add(t);
            }
            for(Gene y: ChromosomeGenes) {
                Random r = new Random();
                double MutProb = r.nextDouble();
                if (MutProb <= Pm) {
                    Bounds geneBounds = getChannelBounds(ChromosomeGenes.indexOf(y));
                    //ChromosomeGenes.set(ChromosomeGenes.indexOf(y), uniformMutation(y, geneBounds));
                    ChromosomeGenes.set(ChromosomeGenes.indexOf(y), nonUniformMutation(y, geneBounds));
                }
            }
            Chromosome newChromosome = new Chromosome(ChromosomeGenes);
            newChromosome.setFitnessValue(newChromosome.calculateFitnessValue());
            if(newChromosome.isFeasible()) {
                x.setChromosomeGenes(ChromosomeGenes);
                x.setFitnessValue(x.calculateFitnessValue());
            }
        }
    }

    /**
     * @param mutationGene
     * @param GeneBound
     * @return Gene
     * @implNote uniform
     *      *      * @implNote
     *      *      * uniform :
     *      *      * 1- generate random number to see if we will take the upper or lower bounds
     *      *      * 2- calculate delta value
     *      *      * 3- generate the new budget value
     */
    static private Gene uniformMutation(Gene mutationGene, Bounds GeneBound) {
        Random r = new Random();
        double r1Prob = r.nextDouble();
        float delta, r2Prob, newBudget;
        Random r2 = new Random();
        if(r1Prob > 0.5){
            delta = (GeneBound.getUpperBound()*totalMarketingBudget) - mutationGene.getBudget() ;
            r2Prob = r2.nextFloat() * (delta);
            newBudget = mutationGene.getBudget() + r2Prob;
        }else{
            delta = mutationGene.getBudget() - (GeneBound.getLowerBound()*totalMarketingBudget);
            r2Prob = r2.nextFloat() * (delta);
            newBudget = mutationGene.getBudget() - r2Prob;
        }
        mutationGene.setBudget(newBudget);
        return mutationGene;
    }


    /**
     * @param mutationGene
     * @param GeneBound
     * @return Gene
     *     *    *@implNote
     *     *    *nonUniform:
     *     *    *1- generate random number to know which bound to choose
     *     *    *2- calculate the y
     *     *    *3- calculate the value of mutation
     */
    static private Gene nonUniformMutation(Gene mutationGene, Bounds GeneBound) {
        Random r1 = new Random();
        Random rand = new Random();
        double r = rand.nextDouble();
        double r1Prob = r1.nextDouble();
        double valueOfMutation, newBudget;
        float y;
        if(r1Prob > 0.5){
            y = (GeneBound.getUpperBound()*totalMarketingBudget) - mutationGene.getBudget() ;
        }else{
            y = mutationGene.getBudget() - (GeneBound.getLowerBound()*totalMarketingBudget);
        }
        Random r2 = new Random();
        double genPower = (1-iIteration)/(double)nIterations;
        double rPower = Math.pow(genPower, b);
        valueOfMutation = y*(1-(Math.pow(rPower,r)));
        if(r1Prob > 0.5){
            newBudget = mutationGene.getBudget() + valueOfMutation;
        }else{
            newBudget = mutationGene.getBudget() - valueOfMutation;
        }
        mutationGene.setBudget((float)newBudget);
        return  mutationGene;
    }

    /**
     * @implNote 2-point crossover
     * @return ArrayList<Chromosome>, contains the result of cross over between the selected parents arraylist
     *
     *      * @implNote
     *      * 2-point crossover :
     *      * 1- two crossover points are picked randomly from the parent chromosomes.
     *      * 2- The bits in between the two points are swapped between the parent organisms.
     *      *
     *      * @return ArrayList<Chromosome>, contains the result of cross over between the selected parents arraylist
     *
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
           float lower = l.equalsIgnoreCase("x")? 0 : Float.parseFloat(l)/100;
           float upper = u.equalsIgnoreCase("x")? 1 : Float.parseFloat(u)/100;
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

