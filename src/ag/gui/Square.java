/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import ag.utils.Coordinate2D;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;

/**
 *
 * @author filipe
 */
public class Square {
    public Coordinate2D upperLeft;
    public Coordinate2D lowerRight;
    public List<Coordinate2D> borderLines;
    public Color borderColor;
    public Color bgColor;
    public float stroke;
    public boolean clickedInside;
    
    public Square(Coordinate2D upperLeft, Coordinate2D lowerRight, Color borderColor, Color bgColor) {
        this.upperLeft = new Coordinate2D(upperLeft.x, upperLeft.y);
        this.lowerRight = new Coordinate2D(lowerRight.x, lowerRight.y);
        borderLines = new ArrayList<>();
        borderLines.add(upperLeft);
        borderLines.add(new Coordinate2D(upperLeft.x, lowerRight.y));
        borderLines.add(lowerRight);
        borderLines.add(new Coordinate2D(lowerRight.x, upperLeft.y));
        borderLines.add(upperLeft);
        stroke = 1f;
        clickedInside = false;
        this.borderColor = borderColor;
        this.bgColor = bgColor;
    }
    
    public boolean colided(double x, double y) {
        return x >= upperLeft.x - stroke && x <= lowerRight.x + stroke
            && y >= upperLeft.y - stroke && y <= lowerRight.y + stroke;
    }
    
    public boolean hasClickedInside() {
        return clickedInside;
    }
}
