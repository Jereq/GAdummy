/*
 * 
 */
package se.jereq.ga_dummy;

/**
 * This class holds a crossover mask and generates new offsprings
 * from two hypotheses.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class CrossoverFunction 
{
    private int[] mask; //Crossover mask
    private int length; //Length of crossover mask
    
    /**
     * Generates a random crossover function.
     * 
     * @param length The length of the crossover mask.
     */
    public CrossoverFunction(int length)
    {
        this.length = length;
        mask = new int[length];
        
        for (int i = 0; i < length; i++)
        {
            int rndNum = (int)(Math.random() * 2);
            mask[i] = rndNum;
        } 
    }
    
    /**
     * Generates a crossover function from a mask bitstring.
     * 
     * @param mask The crossover mask bitstring.
     */
    public CrossoverFunction(int[] mask)
    {
        this.mask = mask;
    }
    
    /**
     * Recombines two hypotheses into two new hypotheses using
     * the crossover mask.
     * 
     * @param h1 Hypothesis 1 to recombine
     * @param h2 Hypothesis 2 to recombine
     * @return Array of two new hypotheses
     */
    public Hypothesis[] recombine(Hypothesis h1, Hypothesis h2)
    {
        Hypothesis hnew1 = new Hypothesis(length);
        Hypothesis hnew2 = new Hypothesis(length);
        
        for (int i = 0; i < length; i++)
        {
            if (mask[i] == 1)
            {
                hnew1.bitstring[i] = h1.bitstring[i];
                hnew2.bitstring[i] = h2.bitstring[i];
            }
            else
            {
                hnew1.bitstring[i] = h2.bitstring[i];
                hnew2.bitstring[i] = h1.bitstring[i];
            }
        }
        
        Hypothesis[] res = new Hypothesis[2];
        res[0] = hnew1;
        res[1] = hnew2;
        
        return res;
    }
    
    @Override
    public String toString()
    {
        String res = "";
        for (int i = 0; i < length; i++)
        {
            res += mask[i] + "";
        }
        return res;
    }
}
