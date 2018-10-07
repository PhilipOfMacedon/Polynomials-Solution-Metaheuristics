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
    public int x;
    public int y;
    
    public Coordinate2D(int x, int y) {
        this.x = x;
        this.y = y;
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
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }
}