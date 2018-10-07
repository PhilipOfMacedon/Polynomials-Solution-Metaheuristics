/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.utils;

/**
 *
 * @author filipe
 */
public class Coordinate2D {
    public double x;
    public double y;
    
    public Coordinate2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public static double getRatio(double min, double max, double value) {
        return (value - min) / (max - min);
    }

    public static double getRelativePosition(double min, double max, double ratio) {
        return (max - min) * ratio + min;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Coordinate2D) {
            Coordinate2D otherCoordinate = (Coordinate2D) other;
            if (x == otherCoordinate.x && y == otherCoordinate.y) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
}