/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.utils;

import java.util.Random;

/**
 *
 * @author filipe
 */
public class BinaryGeneticUtils {

    public static int[] getChromosomeFromInt(int integer, int numberOfGenes) {
        if (numberOfGenes <= 0) {
            throw new IllegalArgumentException("The number of genes must be greater than zero.");
        } else if (Math.pow(2, numberOfGenes - 1) <= Math.abs(integer)) {
            throw new IllegalArgumentException("Insufficient number of genes to represent " + integer + ".");
        } else {
            int[] chromosome = new int[numberOfGenes];
            chromosome[0] = (integer >= 0) ? (0) : (1);
            integer = Math.abs(integer);
            for (int i = numberOfGenes - 1; i > 0; i--) {
                int powerOf2 = (int) Math.pow(2, (i - 1));
                boolean bitActivated = powerOf2 <= integer;
                chromosome[numberOfGenes - i] = (bitActivated) ? (1) : (0);
                integer -= (bitActivated) ? (powerOf2) : (0);
            }
            //printChromosome(chromosome);
            return chromosome;
        }
    }

    public static int getIntFromChromosome(int[] chromosome) {
        if (chromosome == null) {
            throw new NullPointerException("Can't get an int from a null chromosome, dummy!");
        } else if (chromosome.length == 1) {
            throw new IllegalArgumentException("Your chromosome is too short to represent a number.");
        } else {
            int integer = 0;
            for (int i = 1; i < chromosome.length; i++) {
                if (chromosome[i] > 1 || chromosome[i] < 0) {
                    throw new IllegalArgumentException("This isn't a chromosome!");
                }
                integer += chromosome[i] * Math.pow(2, (chromosome.length - i - 1));
            }
            integer *= (chromosome[0] == 1) ? (-1) : (1);
            //System.err.println(integer);
            return integer;
        }
    }

    public static int[] getChromosomeFromFloat(float decimal, int numberOfGenes, int binaryPrecision) {
        if (numberOfGenes <= 0) {
            throw new IllegalArgumentException("The number of genes must be greater than zero.");
        } else if (binaryPrecision < 0) {
            throw new IllegalArgumentException("The binary precision must be greater than zero.");
        } else if (Math.pow(2, (numberOfGenes - binaryPrecision) - 1) <= Math.abs(decimal)) {
            throw new IllegalArgumentException("Insufficient number of genes to represent " + decimal + ".");
        } else {
            int[] chromosome = new int[numberOfGenes];
            chromosome[0] = (decimal >= 0) ? (0) : (1);
            decimal = Math.abs(decimal);
            for (int i = numberOfGenes - 1; i > 0; i--) {
                float powerOf2 = (float) Math.pow(2, (i - binaryPrecision - 1));
                boolean bitActivated = powerOf2 <= decimal;
                chromosome[numberOfGenes - i] = (bitActivated) ? (1) : (0);
                decimal -= (bitActivated) ? (powerOf2) : (0);
            }
            //printChromosome(chromosome);
            return chromosome;
        }
    }

    public static float getFloatFromChromosome(int[] chromosome, int binaryPrecision) {
        if (chromosome == null) {
            throw new NullPointerException("Can't get an int from a null chromosome, dummy!");
        } else if (chromosome.length == 1) {
            throw new IllegalArgumentException("Your chromosome is too short to represent a number.");
        } else if (binaryPrecision < 0 || binaryPrecision > chromosome.length) {
            throw new IllegalArgumentException("Invalid binary precision. Expected something between 0 and " + chromosome.length + ".");
        } else {
            float decimal = 0;
            for (int i = 1; i < chromosome.length; i++) {
                if (chromosome[i] > 1 || chromosome[i] < 0) {
                    throw new IllegalArgumentException("This isn't a chromosome!");
                }
                decimal += (chromosome[i] + 0f) * Math.pow(2, (chromosome.length - binaryPrecision - i - 1));
            }
            decimal *= (chromosome[0] == 1) ? (-1) : (1);
            //System.err.println(integer);
            return decimal;
        }
    }

    public static void printChromosome(int[] chromosome) {
        for (Object gene : chromosome) {
            System.err.print(gene);
        }
        System.err.println();
    }

    public static int[] getRandomizedIntPopulation(int populationSize, int range, int centralElement) {
        if (populationSize <= 0) {
            throw new IllegalArgumentException("The population must be grater than zero");
        } else if (range <= 1) {
            throw new IllegalArgumentException("The chromosome size must be greater than one.");
        } else {
            int[] population = new int[populationSize];
            Random rnd = new Random();
            for (int i = 0; i < populationSize; i++) {
                int aNumber = rnd.nextInt(range);
                int sortedNumber = aNumber - range / 2 + centralElement;
                population[i] = sortedNumber;
            }
            return population;
        }
    }

    public static float[] getRandomizedFloatPopulation(int populationSize, int range, int centralElement) {
        if (populationSize <= 0) {
            throw new IllegalArgumentException("The population must be grater than zero");
        } else if (range <= 1) {
            throw new IllegalArgumentException("The chromosome size must be greater than one.");
        } else {
            float[] population = new float[populationSize];
            Random rnd = new Random();
            for (int i = 0; i < populationSize; i++) {
                float aNumber = rnd.nextFloat() * range;
                float sortedNumber = aNumber - range / 2 + centralElement;
                population[i] = sortedNumber;
            }
            return population;
        }
    }
}
