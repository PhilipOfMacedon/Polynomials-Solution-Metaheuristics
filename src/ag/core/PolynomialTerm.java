/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.core;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author filipe
 */
public class PolynomialTerm implements Comparable<PolynomialTerm> {

    public double coefficient;
    public int exponent;

    public PolynomialTerm(double coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public void addCoefficient(double anotherCoefficient) {
        coefficient += anotherCoefficient;
    }

    @Override
    public String toString() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.##########", otherSymbols);
        String returnStatement = "";
        if (exponent == 0 || coefficient != 1) {
            String formattedCoefficient = df.format(coefficient);
            if (formattedCoefficient.charAt(0) == '-') {
                returnStatement += "- " + formattedCoefficient.substring(1);
            } else {
                returnStatement += "+ " + formattedCoefficient;
            }
        } else if (coefficient == -1) {
            returnStatement += "- ";
        } else {
            returnStatement += "+ ";
        }
        if (exponent > 0) {
            returnStatement += "x";
        }
        if (exponent > 1) {
            returnStatement += "^" + exponent;
        }
        return returnStatement;
    }
    
    public String toUnicodeString() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.##########", otherSymbols);
        String returnStatement = "";
        if (exponent == 0 || coefficient != 1) {
            String formattedCoefficient = df.format(coefficient);
            if (formattedCoefficient.charAt(0) == '-') {
                returnStatement += "- " + formattedCoefficient.substring(1);
            } else {
                returnStatement += "+ " + formattedCoefficient;
            }
        } else if (coefficient == -1) {
            returnStatement += "- ";
        } else {
            returnStatement += "+ ";
        }
        if (exponent > 0) {
            returnStatement += "x";
        }
        if (exponent > 1) {
            String expString = exponent + "";
            String formattedExp = "";
            for (int i = 0; i < expString.length(); i++) {
                String hexString;
                switch (expString.charAt(i)) {
                    case '1':
                        hexString = "B9";
                        break;
                    case '2':
                    case '3':
                        hexString = "B" + expString.charAt(i);
                        break;
                    default:
                        hexString = "207" + expString.charAt(i);
                        break;
                }
                int charCode = Integer.parseInt(hexString, 16);
                char[] charArray = Character.toChars(charCode);
                formattedExp += new String(charArray);
            }
            returnStatement += formattedExp;
        }
        return returnStatement;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.coefficient) ^ (Double.doubleToLongBits(this.coefficient) >>> 32));
        hash = 89 * hash + this.exponent;
        return hash;
    }

    public boolean equals(Object other) {
        if (other instanceof PolynomialTerm) {
            PolynomialTerm otherPolynomialTerm = (PolynomialTerm) other;
            return (this.exponent == otherPolynomialTerm.exponent);
        }
        return false;
    }

    @Override
    public int compareTo(PolynomialTerm other) {
        if (this.exponent == other.exponent) {
            return (int) (this.coefficient - other.coefficient);
        }
        return this.exponent - other.exponent;
    }
}
