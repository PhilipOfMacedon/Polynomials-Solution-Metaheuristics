/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.main;

import ag.core.GeneticAlgorithm;
import ag.gui.ApplicationDisplay;
import ag.utils.ThreadUtils;

/**
 *
 * @author filipe
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //GeneticAlgorithm ag = new GeneticAlgorithm(30, 8, 3, 21f, 0f, 4, 0.01f, 0.7f, "x^2-3x+4");
        GeneticAlgorithm ag = new GeneticAlgorithm(30, 8, 3, 5f, -0.75f, 4, 0.01f, 0.7f, "x^3+3x^2-x-4", false);
        ApplicationDisplay appDisplay = new ApplicationDisplay("Courier New", ag);
        //ag.evolve(10);
        Thread agAutoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println(appDisplay.isRunning);
                while(appDisplay.isRunning) {
                    ThreadUtils.holdOn(3000);
                    ag.evolveOneStep();
                }
            }
        });
        agAutoThread.start();
        appDisplay.start();
    }

}
