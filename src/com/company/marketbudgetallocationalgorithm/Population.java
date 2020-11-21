package com.company.marketbudgetallocationalgorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    static final String OUTPUTS_FILE_PATH = "outputs.txt";
    final String BEST_OUTPUT_FILE_PATH = "best_output.txt";

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

    private  static String Lines = "";
    private static Chromosome bestSolution;


    public static void initializeAlgorithm(){
        nIterations = 10;
        nPopulation = 1000;
        nParents = 100;

        int nTests = 20; // TODO nTests = 20;

        readInputs();

        for(int t = 0 ; t < nTests ; t++) {
            buildChromosomes();
            ArrayList<Chromosome> selectedParents = selection();// tournament selection
            iIteration = 0;

            do {
                ArrayList<Chromosome> offspringChromosomes = crossOver(selectedParents); // 2-point crossover

                System.out.println(selectedParents.size() + " " + offspringChromosomes.size());

                mutation(offspringChromosomes);

                System.out.println("\n** offspringChromosomes: **\n");
                for (Chromosome chromosome : offspringChromosomes)
                {
                    System.out.println(chromosome.toString());
                }
                System.out.println("\n===================================================\n");

                Replacement(offspringChromosomes, selectedParents);
                iIteration++;
            } while (iIteration < nIterations);
            finalOutput(selectedParents,t+1); // TODO
        }

        Lines+="\n\n======================================================\n";
        Lines+="Best Solution after running algorithm for 20 Times:\n";
        Lines+=solutionToString(bestSolution);
        System.out.println(Lines);
        try {
            WriteOnFile(OUTPUTS_FILE_PATH , Lines);
            System.out.println("DONE");
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static void finalOutput(ArrayList<Chromosome> selectedParents, int itteration)
    {
        System.out.println("The final WTA solution is:");

        // 1- get the chromosome with best fitness value (minimum value)
        Chromosome bestChromosome = selectedParents.get(0);
        for(Chromosome x : selectedParents){
            if(x.getFitnessValue() < bestChromosome.getFitnessValue()){
                bestChromosome = x;
            }
        }

        Lines+= "Output# " + itteration + ":\n";
        Lines+= solutionToString(bestChromosome);
        Lines+="\n=================================================\n";

        if(bestSolution == null)
        {
            bestSolution = bestChromosome;
        }
        else {
            bestSolution = bestChromosome.getFitnessValue() > bestSolution.getFitnessValue() ? bestChromosome:bestSolution;
        }
    }

    private static String solutionToString(Chromosome chromosome)
    {
        String output = "";
        output+="The final marketing budget allocation is: \n";

        for (Gene gene : chromosome.getChromosomeGenes())
        {
            String channelName = getChannelName(chromosome.getChromosomeGenes().indexOf(gene));
            output+=channelName+" -> "+gene.getBudget()+" k\n";
        }
        output+="The total profit is " + chromosome.getFitnessValue() + "\n";

        return output;
    }

    static  private void WriteOnFile(String FilePath , String Lines)
    {
        PrintWriter out;
        try {
            //System.out.println(Parser.OperatorFileName);
            out = new PrintWriter(FilePath);
            out.println(Lines);
            out.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
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
        ArrayList<Gene> mutatedGenes = new ArrayList<>();
        Gene tempGene;
        for(Chromosome chromosome : offspringChromosomes) {

            mutatedGenes = new ArrayList<>();
            for(Gene gene: chromosome.getChromosomeGenes()){ // copy the genes in the mutatedGenes list
                tempGene = new Gene(gene.getBudget());
                mutatedGenes.add(tempGene);
            }

            for(Gene gene: mutatedGenes) { // apply mutation
                Random r = new Random();
                float mutProb = r.nextFloat();

                if (mutProb <= Pm) {
                    Bounds geneBounds = getChannelBounds(mutatedGenes.indexOf(gene));
                    //mutatedGenes.set(mutatedGenes.indexOf(gene), uniformMutation(gene, geneBounds));
                    mutatedGenes.set(mutatedGenes.indexOf(gene), nonUniformMutation(gene, geneBounds));
                }
            }

            Chromosome mutatedChromosome = new Chromosome(mutatedGenes);

            mutatedChromosome.setFitnessValue(mutatedChromosome.calculateFitnessValue());

            if(mutatedChromosome.isFeasible()) {
                chromosome.setChromosomeGenes(mutatedGenes);

               // chromosome.setFitnessValue(chromosome.calculateFitnessValue());
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
        float r = rand.nextFloat();
        float r1Prob = r1.nextFloat();

        float valueOfMutation, newBudget;

        float y;
        if(r1Prob > 0.5f){
            y = (GeneBound.getUpperBound()*totalMarketingBudget) - mutationGene.getBudget() ;
        }else{
            y = mutationGene.getBudget() - (GeneBound.getLowerBound()*totalMarketingBudget);
        }

        Random r2 = new Random();

        float genPower = 1-(iIteration/(float)nIterations);

        float rPower = (float) Math.pow(genPower, b);
        valueOfMutation = (float) (y*(1-(Math.pow(rPower,r))));

        if(r2.nextFloat() > 0.5f){
            newBudget = mutationGene.getBudget() + valueOfMutation;
        }else{
            newBudget = mutationGene.getBudget() - valueOfMutation;
        }

        mutationGene.setBudget(newBudget);

        return  mutationGene;
    }

    /**
     * @param selectedParents
     *
     * @implNote
     * 2-point crossover :
     * <p>
     * <li>1- two crossover points are picked randomly from the parent chromosomes.</ul>
     * <li>2- The bits in between the two points are swapped between the parent organisms.</ul>
     *
     *  @return {@code  ArrayList<Chromosome>} offspringChromosomes, contains the result of cross over between the selected parents arraylist
     * **/
    static private ArrayList<Chromosome> crossOver(ArrayList<Chromosome> selectedParents) {
        ArrayList<Chromosome> offspringChromosomes = new ArrayList<Chromosome>();

        for(int i = 0  ; i < nParents ; i+=2) // cross over between i and i+1
        {
            Random r = new Random();
            int xc = r.nextInt(selectedParents.size());
            float rc =  r.nextFloat();

            if(rc <= Pc) //then Cross over occurs
            {
                //perform crossover at Xc using  i and i+1 parents
                ArrayList<Chromosome> crossoverOutput = selectedParents.get(i)
                        .crossOver(selectedParents.get(i+1), xc);

                if(crossoverOutput.get(0).isFeasible()){
                    offspringChromosomes.add(crossoverOutput.get(0));
                }
                else {
                    offspringChromosomes.add(selectedParents.get(i));
                }
                if(crossoverOutput.get(1).isFeasible()){
                    offspringChromosomes.add(crossoverOutput.get(1));
                }
                else {
                    offspringChromosomes.add(selectedParents.get(i+1));
                }
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
            ArrayList<Gene> genes = new ArrayList<Gene>();

            for(int channel = 0 ; channel < nMarketingChannels; channel++) // generate random genes
            {
                Bounds bounds = getChannelBounds(channel);

                float max = bounds.getUpperBound()*totalMarketingBudget;
                float min = bounds.getLowerBound()*totalMarketingBudget;

                float budget =   (float)Math.random() * (max - min) + min;
                genes.add(new Gene(budget));
            }

            Chromosome chromosome = new Chromosome(genes); // in the constructor, the fitness value evaluated also
            if(!chromosome.isFeasible()){
                i--;
            }else {
                populationChromosomes.add(chromosome);
            }
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
