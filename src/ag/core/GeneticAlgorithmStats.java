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

    public static final int NUMBER_OF_POINTS = 30;
    private final float range;
    private final float center;
    private final double smallestOFValue;
    private final double biggestOFValue;
    private final double smallestFitness;
    private final double biggestFitness;
    private final double averageFitness;
    private final double[] xValues;
    private final double[] yValues;

    public GeneticAlgorithmStats(float range, float center, double smallestOFValue, 
            double biggestOFValue, double smallestFitness, double biggestFitness, 
            double averageFitness, double[] xValues, double[] yValues) {
        this.range = range;
        this.center = center;
        this.smallestOFValue = smallestOFValue;
        this.biggestOFValue = biggestOFValue;
        this.smallestFitness = smallestFitness;
        this.biggestFitness = biggestFitness;
        this.averageFitness = averageFitness;
        this.xValues = xValues;
        this.yValues = yValues;
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
}
