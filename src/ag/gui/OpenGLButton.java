/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import ag.utils.Coordinate2D;
import org.newdawn.slick.Color;

/**
 *
 * @author filip
 */
public class OpenGLButton extends Square {
    
    public Coordinate2D textPosition;
    
    public OpenGLButton(Coordinate2D upperLeft, Coordinate2D lowerRight, Coordinate2D textPosition, Color borderColor, Color bgColor) {
        super(upperLeft, lowerRight, borderColor, bgColor);
        this.textPosition = textPosition;
    }
    
}
