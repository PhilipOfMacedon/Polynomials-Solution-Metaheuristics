/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.core;

import java.util.regex.Pattern;

/**
 *
 * @author filipe
 */
public abstract class ObjectiveFunction {

    protected static Pattern VALID_PATTERN;

    protected ObjectiveFunction(String regex) {
        VALID_PATTERN = Pattern.compile(regex);
    }

    public abstract double getFitness(double x);

    public abstract String getFormattedEquation();
    
    public abstract String getUnicodeEquation();

}
