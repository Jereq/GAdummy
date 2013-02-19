/*
 * 
 */
package se.jereq.ga_dummy;

import java.io.*;

/**
 * This class evaluates the fitness value for a hypothesis. It is currently
 * only using a dummy evaluation for display and test purpose. It is easy
 * to modify to use a real problem.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class FitnessFunction
{
	public static final String PLAIN_FILENAME = "GA_param_diffs.txt";
    public static final int RANDOM = 0;
    public static final int TARGET = 1;
    private boolean rndFitness = false;
    
    private int type = TARGET;
    private int[] targetValues;
    
    private boolean debug = false;
    
    /**
     * Inities a new dummy fitness function by generating
     * for random numbers as the target.
     */
    public FitnessFunction()
    {
    	initPlainFile();
    	
        targetValues = new int[6];
        
        switch (type)
        {
        case TARGET:
            targetValues[0] = 5;
            targetValues[1] = 125;
            targetValues[2] = 64;
            targetValues[3] = 101;
            targetValues[4] = 9;
            targetValues[5] = 113;
            break;
            
        case RANDOM:
            for (int i = 0; i < targetValues.length; i++)
            {
                targetValues[i] = (int)(Math.random() * 128);
            }
        	break;
        	
        default:
        	throw new IllegalStateException("Type is incorrectly set.");
        }
    }
    
    /**
     * Calculates the fitness value for a hypothesis.
     * 
     * @param h The hypothesis
     * @return The calculated fitness value
     */
    public int calculateFitness(Hypothesis h)
    {
        if (type == RANDOM)
        {
            //Just return a random fitness
            return (int)(Math.random() * 100);
        }
        else if (type == TARGET)
        {
            double sumPerc = 0;
            int hIndex = 0;
            for (int i = 0; i < targetValues.length; i++)
            {
                //Convert each block of seven bits to an integer value
                String binStr = "";
                for (int j = 0; j < 7; j++)
                {
                    binStr += h.bitstring[hIndex] + "";
                    hIndex++;
                }
                int cVal = Integer.parseInt(binStr, 2); //2 for binary
                
                if (debug) System.out.println(binStr + " -> " + cVal);
                
                //Calculate how much the hypothesis fitness value
                //differs from the target value.
                int diff = Math.abs(cVal - targetValues[i]);
                double perc = (double)100 - (double)diff / (double)128 * (double)100;
                
                //Add a small random diff to fitness for testing purpose
                if (rndFitness)
                {
                    double rndDiff = Math.random() * 10 - 5;
                    perc += rndDiff;
                    if (perc < 0) perc = 0;
                    if (perc >= 100) perc = 100;
                }
                
                sumPerc += perc;
                
                if (debug) System.out.println(targetValues[i] + "/" + cVal + ": diff=" + diff + " perc=" + perc);
            }
            
            //Return average diff (in percent)
            return (int)(sumPerc / (double)targetValues.length);
        }
        else
        {
            return 10;
        }
    }
    
    /**
     * Saves fitness per parameter for a hypothesis.
     * 
     * @param cGeneration Current generation
     * @param h The hypothesis
     */
    public void saveParamDiffsData(int cGeneration, Hypothesis h)
    {
        int hIndex = 0;
        int[] diffs = new int[targetValues.length];
        for (int i = 0; i < targetValues.length; i++)
        {
            //Convert each block of five bits to an integer value
            String binStr = "";
            for (int j = 0; j < 7; j++)
            {
                binStr += h.bitstring[hIndex] + "";
                hIndex++;
            }
            int cVal = Integer.parseInt(binStr, 2); //2 for binary

            //Calculate how much the hypothesis fitness value
            //differs from the target value.
            int diff = Math.abs(cVal - targetValues[i]);
            double perc = (double)100 - (double)diff / (double)128 * (double)100;

            //Add a small random diff to fitness for testing purpose
            if (rndFitness)
            {
                double rndDiff = Math.random() * 10 - 5;
                perc += rndDiff;
                if (perc < 0) perc = 0;
                if (perc >= 100) perc = 100;
            }

            diffs[i] = (int)perc;
        }
        
        saveToGnuplot(cGeneration, diffs);
    }
    
    private void initPlainFile() {
    	try {
    		FileWriter f = new FileWriter(PLAIN_FILENAME);
    		f.close();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    /**
     * Saves parameter fitness data to a Gnuplot data file.
     * 
     * @param cGeneration Current generation
     * @param diffs Fitness per parameter
     */
    private void saveToGnuplot(int cGeneration, int[] diffs)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PLAIN_FILENAME, true));
            String line = Integer.toString(cGeneration);
            for (int i = 0; i < diffs.length; i++)
            {
                line += " " + diffs[i];
            }
            writer.write(line);
            writer.newLine();
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
