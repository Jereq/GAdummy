package se.jereq.ga_dummy;

import java.util.Vector;
import java.io.*;

/**
 * Main class for the GA implementation. It holds the population and
 * is responsible for the main GA process of evaluating hypotheses and
 * generating new populations.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class GAmain 
{
    private Vector<Hypothesis> population; //The population of hypotheses
    private CrossoverFunction crossoverFunc;
    private FitnessFunction fitnessFunc;
    private int[] fitnessValues; //Holds the fitness values from fitness evaluations
    private int populationSize = 20; //Size of population = number of hypotheses
    private int hypothesisLength = 42; //Length of the bitstring for a hypothesis
    //private int threshold = -1; //-1 -> no threshold stop value used
    private int iterations = 1000; //Number of iterations
    private boolean elitist = false; //True if elitist approach (best hypothesis is always selected)
    
    private double crossoverRate = 0.8; //Ratio for crossover
    private double mutationRate = 0.2; //Ratio for mutations
    
    private boolean debug = false; //Display debug info
    
    /**
     * Inits the main GA class.
     */
    public GAmain()
    {
        population = new Vector<>(populationSize);
        
        for (int i = 0; i < populationSize; i++)
        {
            population.add(new Hypothesis(hypothesisLength));
        }
        
        crossoverFunc = new CrossoverFunction(hypothesisLength);
        fitnessFunc = new FitnessFunction();
        fitnessValues = new int[populationSize];
    }
    
    /**
     * Runs the GA.
     */
    public void runGA()
    {
        //Init data files
        initCSV();
        
        for (int cIteration = 0; cIteration < iterations; cIteration++)
        {
            int sumFitness = 0;
            
            //Stats to save
            int avgFitness = 0;
            int bestFitness = -10000;
            int worstFitness = 10000;
            int bestFitIndex = -1;
            
            //Calculate fitness values for the whole population
            for (int i = 0; i < populationSize; i++)
            {
                int newFitness = fitnessFunc.calculateFitness(population.get(i));
                fitnessValues[i] = newFitness;
                sumFitness += newFitness;
                
                if (newFitness > bestFitness) 
                {
                    bestFitness = newFitness;
                    bestFitIndex = i;
                }
                if (newFitness < worstFitness) 
                {
                    worstFitness = newFitness;
                }
                
                if (debug) System.out.println(population.get(i).toString() + ": " + fitnessValues[i]);
            }
            
            System.out.println("Generation: " + cIteration + ", Best fitness: " + bestFitness);
            
            avgFitness = (int)((double)sumFitness / (double)populationSize);
            
            //Dump stats to file
            saveToCSV(cIteration, worstFitness, avgFitness, bestFitness);
            saveToPlainFile(cIteration, worstFitness, avgFitness, bestFitness);
            
            //Dump param data diffs
            fitnessFunc.saveParamDiffsData(cIteration, population.get(bestFitIndex));
            
            //Generate new population for next iteration
            genNewPopulation(sumFitness);
        }
    }
    
    /**
     * Generates a new GA population by recombination, selection and mutation.
     */
    public void genNewPopulation(int sumFitness)
    {
        if (debug) System.out.println("\nNew population");
        Vector<Hypothesis> newPopulation = new Vector<>(20);
        
        //Crossover
        int noCo = (int)(crossoverRate * (double)populationSize);
        if (debug) System.out.println("Crossover: " + crossoverRate + " -> "+ noCo);
        for (int i = 0; i < noCo / 2; i++)
        {
            Hypothesis h1 = probabilisticSelect(sumFitness);
            Hypothesis h2 = probabilisticSelect(sumFitness);
            Hypothesis[] res = crossoverFunc.recombine(h1, h2);
            
            if (debug) System.out.println(crossoverFunc.toString());
            if (debug) System.out.println(h1.toString() + "," + h2.toString() + " -> " + res[0].toString() + "," + res[1].toString());
            
            newPopulation.add(res[0]);
            newPopulation.add(res[1]);
        }
        
        //Elitist. Always select the best hypothesis for next generation
        if (elitist)
        {
            int bestF = -10000;
            int bestIndex = -1;
            
            //Find best hypothesis
            for (int i = 0; i < population.size(); i++)
            {
                if (fitnessValues[i] > bestF)
                {
                    bestF = fitnessValues[i];
                    bestIndex = i;
                }
            }
            
            //Add it to new population
            if (bestIndex >= 0)
            {
                newPopulation.add(population.get(bestIndex));
                if (debug) System.out.println("Selecting best h " + population.get(bestIndex).toString() + ": " + fitnessValues[bestIndex]);
            }
        }
        
        //Selection
        int noSelect = populationSize - newPopulation.size();
        if (debug) System.out.println("Selection: " + noSelect);
        for (int i = 0; i < noSelect; i++)
        {
            Hypothesis h = probabilisticSelect(sumFitness);
            
            newPopulation.add(h);
        }
        
        //Mutation
        int noMut = (int)(mutationRate * (double)populationSize);
        if (debug) System.out.println("Mutation: " + mutationRate + " -> " + noMut);
        for (int i = 0; i < noMut; i++)
        {
            int rndIndex = (int)(Math.random() * populationSize);
            String oldStr = newPopulation.get(rndIndex).toString();
            newPopulation.get(rndIndex).mutate();
            if (debug) System.out.println(oldStr + " -> " + newPopulation.get(rndIndex).toString());
        }
        
        //Assign the newly generated population
        population = newPopulation;
    }
    
    /**
     * Probabilistically selects a hypothesis from the current generation.
     * 
     * @return The selected hypothesis
     */
    public Hypothesis probabilisticSelect(int sumFitness)
    {
        boolean found = false;
        int cIndex = 0;
        Hypothesis h = null;
        while (!found)
        {
            double prob = (double)fitnessValues[cIndex] / (double)sumFitness;
            double rnd = Math.random();
            
            if (rnd <= prob)
            {
                //Found one
                found = true;
                h = population.get(cIndex);
                
                if (debug) System.out.println(rnd + "<" + prob + ": " + h.toString());
            }
            
            cIndex++;
            if (cIndex >= populationSize) cIndex = 0;
        }
        
        return h;
    }
    
    /**
     * Writes header stuff to the .csv file.
     */
    public void initCSV()
    {
        try
        {
            String filename = "GA_" + populationSize + "_" + iterations + "_" + crossoverRate + "_" + mutationRate + ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
            writer.write("Iteration;WorstFitness;AvgFitness;BestFitness");
            writer.newLine();
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Saves a data line to the .csv file
     * 
     * @param cIteration Current iteration
     * @param worstFitness Worst fitness this iteration
     * @param avgFitness Average fitness this iteration
     * @param bestFitness Best fitness this iteration
     */
    public void saveToCSV(int cIteration, int worstFitness, int avgFitness, int bestFitness)
    {
        try
        {
            String filename = "GA_" + populationSize + "_" + iterations + "_" + crossoverRate + "_" + mutationRate + ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(cIteration + ";" + worstFitness + ";" + avgFitness + ";" + bestFitness);
            writer.newLine();
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Saves a data line to the Gnuplot data file
     * 
     * @param cIteration Current iteration
     * @param worstFitness Worst fitness this iteration
     * @param avgFitness Average fitness this iteration
     * @param bestFitness Best fitness this iteration
     */
    public void saveToPlainFile(int cIteration, int worstFitness, int avgFitness, int bestFitness)
    {
        try
        {
            String filename = "GA_" + populationSize + "_" + iterations + "_" + crossoverRate + "_" + mutationRate + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(cIteration + " " + worstFitness + " " + avgFitness + " " + bestFitness);
            writer.newLine();
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

