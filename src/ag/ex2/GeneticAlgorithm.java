/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.ex2;

import ag.utils.BinaryGeneticUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author filipe
 */
public class GeneticAlgorithm {

    private float[] population;
    private int numberOfGenes;
    private int binaryPrecision;
    private int numberOfCrossoverPoints;
    private float mutationRatio;
    private float crossoverRatio;
    private ObjectiveFunction objectiveFunction;

    public GeneticAlgorithm(int populationSize, int numberOfGenes, int binaryPrecision,
            int range, int centralElement, int crossoverPoints,
            float mutationRatio, float crossoverRatio, String function) {
        if (populationSize % 2 == 1) {
            throw new IllegalArgumentException("The population size must be even.");
        } else if (mutationRatio < 0 || mutationRatio > 1) {
            throw new IllegalArgumentException("The mutation ratio must be a number between zero and one.");
        } else if (crossoverRatio < 0 || crossoverRatio > 1) {
            throw new IllegalArgumentException("The crossover ratio must be a number between zero and one.");
        }
        this.numberOfGenes = numberOfGenes;
        this.binaryPrecision = binaryPrecision;
        this.numberOfCrossoverPoints = crossoverPoints;
        this.mutationRatio = mutationRatio;
        this.crossoverRatio = crossoverRatio;
        population = BinaryGeneticUtils.getRandomizedFloatPopulation(populationSize, range, centralElement);
        objectiveFunction = new PolynomialFunction(function);
    }

    public float[] evolve(int numberOfGenerations) {
        for (int i = 0; i < numberOfGenerations; i++) {
            breed();
        }
        return population;
    }

    private void breed() {
        float[] nextGeneration = new float[population.length];
        for (int i = 0; i < population.length / 2; i++) {
            float parent1 = tournament();
            float parent2 = tournament();
            float[] children = getChildren(parent1, parent2);
            nextGeneration[2 * i] = children[0];
            nextGeneration[2 * i + 1] = children[1];
        }
        population = nextGeneration;
    }

    private float tournament() {
        int rival1 = generateRandomInt(population.length);
        int rival2 = generateRandomInt(population.length);
        float rival1Value = population[rival1];
        float rival2Value = population[rival2];
        if (objectiveFunction.getFitness(rival1Value) > objectiveFunction.getFitness(rival2Value)) {
            return population[rival1];
        } else {
            return population[rival2];
        }
    }

    private float[] getChildren(float parent1, float parent2) {
        float[] children = new float[2];
        if (generateRandomFloat() <= crossoverRatio) {
            int[] parent1Chromosome = BinaryGeneticUtils.getChromosomeFromFloat(parent1, numberOfGenes, binaryPrecision);
            int[] parent2Chromosome = BinaryGeneticUtils.getChromosomeFromFloat(parent2, numberOfGenes, binaryPrecision);
            int[] crossoverPoints = generateRandomIntArray(numberOfGenes);
            children[0] = BinaryGeneticUtils.getFloatFromChromosome(crossover(parent1Chromosome, parent2Chromosome, crossoverPoints), binaryPrecision);
            children[1] = BinaryGeneticUtils.getFloatFromChromosome(crossover(parent2Chromosome, parent1Chromosome, crossoverPoints), binaryPrecision);
        } else {
            children[0] = parent1;
            children[1] = parent2;
        }
        children[0] = subjectToMutation(children[0]);
        children[1] = subjectToMutation(children[1]);
        return children;
    }

    private float subjectToMutation(float subject) {
        int[] chromosome = BinaryGeneticUtils.getChromosomeFromFloat(subject, numberOfGenes, binaryPrecision);
        for (int i = 0; i < chromosome.length; i++) {
            if (generateRandomFloat() < mutationRatio) {
                chromosome[i] = (chromosome[i] == 0) ? (1) : (0);
            }
        }
        return BinaryGeneticUtils.getFloatFromChromosome(chromosome, binaryPrecision);
    }

    private int[] crossover(int[] chromosome1, int[] chromosome2, int[] crosoverPoints) {
        int[] child = new int[chromosome1.length];
        for (int i = 0; i < child.length; i++) {
            child[i] = (getGeneSource(i, crosoverPoints) % 2 == 0) ? (chromosome1[i]) : (chromosome2[i]);
        }
        return child;
    }
    
    private int getGeneSource(int index, int[] crossoverPointsArray) {
        for (int i = 0; i < numberOfCrossoverPoints; i++) {
            if (index >= crossoverPointsArray[i]) continue;
            else return i;
        }
        return numberOfCrossoverPoints;
    }

    private int generateRandomInt(int range) {
        Random rnd = new Random();
        return rnd.nextInt(range);
    }

    private int[] generateRandomIntArray(int range) {
        Random rnd = new Random();
        List<Integer> randomIntArray = new ArrayList<>();
        for (int i = 0; i < numberOfCrossoverPoints; i++) {
            randomIntArray.add(rnd.nextInt(range));
        }
        Collections.sort(randomIntArray);
        return randomIntArray.stream().mapToInt(i -> i).toArray();
    }

    private float generateRandomFloat() {
        Random rnd = new Random();
        return rnd.nextFloat();
    }

}
