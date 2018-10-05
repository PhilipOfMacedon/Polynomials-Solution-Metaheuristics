/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.main;

import ag.ex2.GeneticAlgorithm;

/**
 *
 * @author filipe
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GeneticAlgorithm ag = new GeneticAlgorithm(30, 8, 3, 21, 0, 4, 0.01f, 0.7f, "x^2-3x+4");
        float[] population = ag.evolve(7);
        for (int i = 0; i < population.length; i++) {
            System.out.println(population[i]);
        }
    }

}
