/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 *
 * @author filipe
 */
public final class PolynomialFunction extends ObjectiveFunction {

    private static final String REGEX_P1 = "([+-]?(?:(?:\\d+\\.\\d+x\\^\\d+)";
    private static final String REGEX_P2 = "|(?:\\d+\\.\\d+x)";
    private static final String REGEX_P3 = "|(?:\\d+\\.\\d+)";
    private static final String REGEX_P4 = "|(?:x\\^\\d+)";
    private static final String REGEX_P5 = "|(?:\\d+x\\^\\d+)";
    private static final String REGEX_P6 = "|(?:\\d+x)";
    private static final String REGEX_P7 = "|(?:\\d+)";
    private static final String REGEX_P8 = "|(?:x)))";
    private static final String REGEX = REGEX_P1 + REGEX_P2 + REGEX_P3 + REGEX_P4
            + REGEX_P5 + REGEX_P6 + REGEX_P7 + REGEX_P8;
    private List<PolynomialTerm> terms;

    public PolynomialFunction(String function) {
        super(REGEX);
        evaluateFunction(function);
    }

    @Override
    public double getFitness(double x) {
        if (terms == null || terms.isEmpty()) {
            throw new NullPointerException("There's no polynomial function stored in this object yet.");
        }
        double fitness = 0;
        for (PolynomialTerm term : terms) {
            fitness += term.coefficient * Math.pow(x, term.exponent);
        }
        return fitness;
    }

    private void evaluateFunction(String function) {
        List<String> parts = parse(function);
        List<PolynomialTerm> parsedTerms = new ArrayList<>();
        for (String part : parts) {
            parsedTerms.add(getPolynomialTerm(part));
        }
        optimizePolynomial(parsedTerms);
    }

    @Override
    public String getFormattedEquation() {
        String formattedEquation = "";
        if (!terms.isEmpty()) {
            formattedEquation = terms.get(0).toString();
            for (PolynomialTerm term : terms.subList(1, terms.size())) {
                formattedEquation += " " + term.toString();
            }
            if (formattedEquation.charAt(0) == '+') {
                formattedEquation = formattedEquation.substring(2);
            }
        }
        return formattedEquation + " = 0";
    }
    
    @Override
    public String getUnicodeEquation() {
        String formattedEquation = "";
        if (!terms.isEmpty()) {
            formattedEquation = terms.get(0).toUnicodeString();
            for (PolynomialTerm term : terms.subList(1, terms.size())) {
                formattedEquation += " " + term.toUnicodeString();
            }
            if (formattedEquation.charAt(0) == '+') {
                formattedEquation = formattedEquation.substring(2);
            }
        }
        return formattedEquation + " = 0";
    }

    private double getCoefficient(String term) {
        int position = term.indexOf("x");
        if (position > 0) {
            if (position == 1 && term.charAt(0) == '-') {
                return -1;
            } else if (position == 1 && term.charAt(0) == '+') {
                return 1;
            } else {
                return Double.parseDouble(term.substring(0, position));
            }
        } else if (term.contains("x")) {
            return 1;
        }
        return Double.parseDouble(term);
    }

    private int getExponent(String term) {
        int position = term.indexOf("^");
        if (position > 0) {
            return Integer.parseInt(term.substring(position + 1));
        } else if (term.contains("x")) {
            return 1;
        }
        return 0;
    }

    private PolynomialTerm getPolynomialTerm(String term) {
        return new PolynomialTerm(getCoefficient(term), getExponent(term));
    }

    private void optimizePolynomial(List<PolynomialTerm> parsedTerms) {
        terms = new ArrayList<>();
        while (!parsedTerms.isEmpty()) {
            PolynomialTerm term = parsedTerms.remove(0);
            int count = 0;
            while (count < parsedTerms.size()) {
                if (term.equals(parsedTerms.get(count))) {
                    term.addCoefficient(parsedTerms.remove(count).coefficient);
                } else {
                    count++;
                }
            }
            if (term.coefficient != 0) {
                terms.add(term);
            }
        }
        Collections.sort(terms);
        Collections.reverse(terms);
    }

    private List<String> parse(String toParse) {
        List<String> chunks = new ArrayList<>();
        Matcher matcher = VALID_PATTERN.matcher(toParse);
        while (matcher.find()) {
            chunks.add(matcher.group());
        }
        return chunks;
    }

}
