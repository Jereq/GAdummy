/*
 * 
 */
package se.jereq.ga_dummy;

/**
 * Starter class of the GAdummy tool. GAdummy is a fully featured Genetic Algorithm
 * implementation, but uses a dummy FitnessFunction for testing and display purpose.
 * It can easily be replaced with a fitness evaluation of a real problem.
 * 
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class GAdummy
{

    /**
     * Starts the program.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        GAmain gm = new GAmain();
        gm.runGA();
    }
}
