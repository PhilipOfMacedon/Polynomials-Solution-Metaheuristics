/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.core;

/**
 *
 * @author filip
 */
public class GeneticAlgorithmStats {

    public static final int PLOT_RESOLUTION = 50;
    public static final int AXIS_DIVISION_AMMOUNT = 13;
    private final ObjectiveFunction function;
    private final float range;
    private final float center;
    private final double smallestOFValue;
    private final double biggestOFValue;
    private final double smallestFitness;
    private final double biggestFitness;
    private final double averageFitness;
    private final double[] xValues;
    private final double[] yValues;
    private final float[] population;
    private double[] fitnesses;
    private double[] xAxis;
    private double[] yAxis;

    public GeneticAlgorithmStats(ObjectiveFunction function, float range, float center, double smallestOFValue,
            double biggestOFValue, double smallestFitness, double biggestFitness,
            double averageFitness, float[] population, double[] xValues, double[] yValues) {
        this.function = function;
        this.range = range;
        this.center = center;
        this.smallestOFValue = smallestOFValue;
        this.biggestOFValue = biggestOFValue;
        this.smallestFitness = smallestFitness;
        this.biggestFitness = biggestFitness;
        this.averageFitness = averageFitness;
        this.xValues = xValues;
        this.yValues = yValues;
        this.population = population;
        setupAxes();
        loadFitnesses();
    }

    private void setupAxes() {
        xAxis = new double[AXIS_DIVISION_AMMOUNT];
        yAxis = new double[AXIS_DIVISION_AMMOUNT];
        for (int i = 0; i < AXIS_DIVISION_AMMOUNT; i++) {
            xAxis[i] = xValues[0] + (xValues[PLOT_RESOLUTION] * i / AXIS_DIVISION_AMMOUNT);
            yAxis[i] = smallestOFValue + (biggestOFValue * i / AXIS_DIVISION_AMMOUNT);
        }
    }
    
    private void loadFitnesses() {
        fitnesses = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            fitnesses[i] = function.getFitness(population[i]);
        }
    }
    
    public float getRange() {
        return range;
    }

    public float getCenter() {
        return center;
    }

    public double getSmallestOFValue() {
        return smallestOFValue;
    }

    public double getBiggestOFValue() {
        return biggestOFValue;
    }

    public double getSmallestFitness() {
        return smallestFitness;
    }

    public double getBiggestFitness() {
        return biggestFitness;
    }

    public double getAverageFitness() {
        return averageFitness;
    }

    public double[] getXValues() {
        return xValues;
    }

    public double[] getYValues() {
        return yValues;
    }

    public float[] getPopulation() {
        return population;
    }

    public ObjectiveFunction getFunction() {
        return function;
    }

    public double[] getFitnesses() {
        return fitnesses;
    }

    public double[] getxAxis() {
        return xAxis;
    }

    public double[] getyAxis() {
        return yAxis;
    }

    public boolean xRangeCrossesYAxis() {
        if (xValues[0] * xValues[PLOT_RESOLUTION] < 0) {
            return true;
        }
        return false;
    }

    public boolean yRangeCrossesXAxis() {
        if (smallestOFValue * biggestOFValue < 0) {
            return true;
        }
        return false;
    }
}
