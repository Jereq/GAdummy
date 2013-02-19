/*
 * 
 */
package se.jereq.ga_dummy;

/**
 * Class for holding a hypothesis (bitstring) in a GA population.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class Hypothesis 
{
    public int[] bitstring; //Have the bitstring public for easier access.
    private int length; //Length of bitstring
    
    /**
     * Generates a new random hypothesis.
     * 
     * @param length Length of the bitstring.
     */
    public Hypothesis(int length)
    {
        this.length = length;
        bitstring = new int[length];
        
        for (int i = 0; i < length; i++)
        {
            bitstring[i] = (int)(Math.random() * 2);
        }
    }
    
    /**
     * Generates a new hypothesis from a bitstring.
     * 
     * @param bitstring The bitstring.
     */
    public Hypothesis(int[] bitstring)
    {
        this.length = bitstring.length;
        this.bitstring = bitstring;
    }
    
    /**
     * Mutates this hypothesis by inverting a random bit.
     */
    public void mutate()
    {
        //Generate a random bit to mutate
        int rndIndex = (int)(Math.random() * length);
        //Mutate it: 1 -> 0, 0 -> 1
        bitstring[rndIndex] = 1 - bitstring[rndIndex];
    }
    
    @Override
    public String toString()
    {
        String str = "";
        for (int i = 0; i < length; i++)
        {
            str += bitstring[i] + "";
        }
        return str;
    }
}
