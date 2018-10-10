/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import ag.core.GeneticAlgorithm;
import ag.core.GeneticAlgorithmEventListener;
import ag.core.GeneticAlgorithmStats;
import ag.utils.Coordinate2D;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import static ag.core.GeneticAlgorithmStats.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.font.effects.ColorEffect;

/**
 *
 * @author filip
 */
public class ApplicationDisplay {

    private static final double PLOT_LEFT_BOUND = 320D;
    private static final double PLOT_RIGHT_BOUND = 780D;
    private static final double PLOT_UP_BOUND = 20D;
    private static final double PLOT_DOWN_BOUND = 280D;
    private static final Coordinate2D HUD_BOUND_POSITION = new Coordinate2D(20D, 320D);
    private static final List<Coordinate2D> DIVISION_LINES = loadDivisionCoords();
    private static final List<Coordinate2D>[] HISTORIC_CHART_STRUCTURE_COORDINATES = loadChartLineDivisions();
    private static final double HISTORIC_CHART_MIN_X = 180;
    private static final double HISTORIC_CHART_MAX_X = 780;
    private static final double HISTORIC_CHART_MIN_Y = 360;
    private static final double HISTORIC_CHART_MAX_Y = 580;
    private static final int HISTORIC_CHART_DIVISIONS = 22;
    private static final char X_AXIS = 'x';
    private static final char Y_AXIS = 'y';
    private static final DecimalFormat decimal = new DecimalFormat("#.##");
    private float plotLineWidth = 1.0f;
    private UnicodeFont consoleFont12;
    private UnicodeFont consoleFont8;
    private String fontName;
    private GeneticAlgorithmStats stats;
    private GeneticAlgorithm heuristic;
    private List<Map<String, Double>> fitnessHistoric;
    private double historicalMin = Double.MAX_VALUE;
    private double historicalMax = Double.MIN_VALUE;
    public boolean isRunning;

    private static List<Coordinate2D> loadDivisionCoords() {
        List<Coordinate2D> divisionCoords = new ArrayList<>();
        divisionCoords.add(new Coordinate2D(300d, 300d));
        divisionCoords.add(new Coordinate2D(800d, 300d));
        divisionCoords.add(new Coordinate2D(300d, 0d));
        divisionCoords.add(new Coordinate2D(300d, 300d));
        divisionCoords.add(new Coordinate2D(0d, 300d));
        divisionCoords.add(new Coordinate2D(300d, 300d));
        return divisionCoords;
    }

    private static List<Coordinate2D>[] loadChartLineDivisions() {
        List<Coordinate2D>[] markings;
        markings = new List[2];
        List<Coordinate2D> divisionCoords = new ArrayList<>();
        for (int i = 1; i < HISTORIC_CHART_DIVISIONS; i++) {
            divisionCoords.add(new Coordinate2D(HISTORIC_CHART_MIN_X + i * (HISTORIC_CHART_MAX_X - HISTORIC_CHART_MIN_X) / HISTORIC_CHART_DIVISIONS, HISTORIC_CHART_MAX_Y));
            divisionCoords.add(new Coordinate2D(HISTORIC_CHART_MIN_X + i * (HISTORIC_CHART_MAX_X - HISTORIC_CHART_MIN_X) / HISTORIC_CHART_DIVISIONS, HISTORIC_CHART_MIN_Y));
        }
        List<Coordinate2D> limits = new ArrayList<>();
        limits.add(new Coordinate2D(HISTORIC_CHART_MIN_X, HISTORIC_CHART_MIN_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MAX_X, HISTORIC_CHART_MIN_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MIN_X, HISTORIC_CHART_MAX_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MAX_X, HISTORIC_CHART_MAX_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MIN_X, HISTORIC_CHART_MIN_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MIN_X, HISTORIC_CHART_MAX_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MAX_X, HISTORIC_CHART_MIN_Y));
        limits.add(new Coordinate2D(HISTORIC_CHART_MAX_X, HISTORIC_CHART_MAX_Y));
        markings[0] = divisionCoords;
        markings[1] = limits;
        return markings;
    }

    public ApplicationDisplay(String font, GeneticAlgorithm ag) {
        fontName = font;
        heuristic = ag;
        fitnessHistoric = new ArrayList<>();
        ag.addGeneticAlgorithmEventListener(new GeneticAlgorithmEventListener() {
            @Override
            public void generationEvolved(GeneticAlgorithmStats statistics) {
                updateStats(statistics);
            }
        });
        isRunning = true;
        heuristic.updateStats();
    }

    public void updateStats(GeneticAlgorithmStats newStats) {
        stats = newStats;
        if (stats.getSmallestFitness() < historicalMin) {
            historicalMin = stats.getSmallestFitness();
        }
        if (stats.getBiggestFitness() > historicalMax) {
            historicalMax = stats.getBiggestFitness();
        }
        Map<String, Double> historicPoint = new HashMap<>();
        historicPoint.put("min", stats.getSmallestFitness());
        historicPoint.put("max", stats.getBiggestFitness());
        historicPoint.put("average", stats.getAverageFitness());
        if (fitnessHistoric.size() == HISTORIC_CHART_DIVISIONS + 1) {
            fitnessHistoric.remove(0);
        }
        fitnessHistoric.add(historicPoint);
    }

    public void start() {
        initGL(800, 600);
        initFont();

        while (true) {
            glClear(GL_COLOR_BUFFER_BIT);
            render();
            pollInput();
            Display.update();
            Display.sync(100);

            if (Display.isCloseRequested()) {
                Display.destroy();
                isRunning = false;
                System.exit(0);
            }
        }
    }

    private void initGL(int width, int height) {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
            Display.setVSyncEnabled(true);
            Display.setTitle("Polynomial Genetic Machine V1.0");
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glViewport(0, 0, width, height);
        glMatrixMode(GL_MODELVIEW);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    private void initFont() {
        try {
            // load a default java font
            consoleFont12 = new UnicodeFont(new Font(fontName, Font.BOLD, 12));
            consoleFont12.getEffects().add(new ColorEffect(java.awt.Color.white));
            consoleFont12.addAsciiGlyphs();
            consoleFont12.loadGlyphs(); // load glyphs from font file
            consoleFont8 = new UnicodeFont(new Font(fontName, Font.BOLD, 8));
            consoleFont8.getEffects().add(new ColorEffect(java.awt.Color.white));
            consoleFont8.addAsciiGlyphs();
            consoleFont8.loadGlyphs(); // load glyphs from font file
        } catch (SlickException ex) {
            ex.printStackTrace();
        }
    }

    private void render() {
        separateScreen();
        renderHUD();
        renderInterface();
    }

    private void pollInput() {
        pollMouse();
    }
    private void pollMouse() {
        if (Mouse.getX() > PLOT_LEFT_BOUND - 20 && Mouse.getY() > PLOT_DOWN_BOUND + 20) {
            plotLineWidth = 3.0f;
        } else {
            plotLineWidth = 1.0f;
        }
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                switch (Mouse.getEventButton()) {
                //Left button pressed
                    case 0:
                        break;
                //Right button pressed
                    case 1:
                        break;
                    case 2:
                        showPositionAtConsole();
                        break;
                    default:
                        break;
                }
            } else if (Mouse.getEventButton() == 0) {
                //Left button released
            }
        }
    }

    private void showPositionAtConsole() {
        int x = Mouse.getX();
        int y = Mouse.getY();
        System.err.println("Mouse @ X:" + x + " Y:" + (800 - y));
    }

    private void separateScreen() {
        drawQuad(new Coordinate2D(300, 0), new Coordinate2D(800, 300), new Color(0f, 0f, 0.25f));
        //drawQuad(new Coordinate2D(180d, 360d), new Coordinate2D(780d, 580d), new Color(0f, 0f, 0.25f));
        drawLines(DIVISION_LINES, 3f, Color.white, false, false);
    }

    private void drawQuad(Coordinate2D upperLeft, Coordinate2D lowerRight, Color background) {
        glColor3f(background.r, background.g, background.b);
        glBegin(GL_QUADS);
        {
            glVertex2d(upperLeft.x, upperLeft.y);
            glVertex2d(upperLeft.x, lowerRight.y);
            glVertex2d(lowerRight.x, lowerRight.y);
            glVertex2d(lowerRight.x, upperLeft.y);
        }
        glEnd();
    }

    private void drawLines(List<Coordinate2D> lineCoords, float lineWidth, Color color, boolean dotted, boolean stripped) {
        if (dotted) {
            glPushAttrib(GL_ENABLE_BIT);
            glLineStipple(1, (short) 0x0001);
            glEnable(GL_LINE_STIPPLE);
        }
        glColor3f(color.r, color.g, color.b);
        glLineWidth(lineWidth);
        if (stripped) {
            glBegin(GL_LINE_STRIP);
        } else {
            glBegin(GL_LINES);
        }
        {
            for (Coordinate2D coord : lineCoords) {
                glVertex2d(coord.x, coord.y);
            }
        }
        glEnd();
        if (dotted) {
            glPopAttrib();
        }
    }

    private void renderObjectiveFunction() {
        glDisable(GL_TEXTURE_2D);
        renderPlotAxis();
        plotGraphic();
        renderPopulationPoints();
    }

    private void renderPlotAxis() {
        glLineWidth(1.0f);
        if (stats.yRangeCrossesXAxis()) {
            double yAxis = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                    1 - Coordinate2D.getRatio(stats.getSmallestOFValue(), stats.getBiggestOFValue(), 0));
            List<Coordinate2D> graduation = getAxisConfiguration(stats.getxAxis(), X_AXIS);
            glColor3f(1.0f, 1.0f, 0.0f);
            glBegin(GL_LINES);
            {
                glVertex2d(PLOT_LEFT_BOUND - 10, yAxis);
                glVertex2d(PLOT_RIGHT_BOUND + 10, yAxis);
                for (Coordinate2D coordinate : graduation) {
                    glVertex2d(coordinate.x, coordinate.y - 2);
                    glVertex2d(coordinate.x, coordinate.y + 2);
                }
            }
            glEnd();
        }
        if (stats.xRangeCrossesYAxis()) {
            double xAxis = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                    Coordinate2D.getRatio(stats.getXValues()[0], stats.getXValues()[PLOT_RESOLUTION], 0));
            List<Coordinate2D> graduation = getAxisConfiguration(stats.getyAxis(), Y_AXIS);
            glColor3f(1.0f, 1.0f, 0.0f);
            glBegin(GL_LINES);
            {
                glVertex2d(xAxis, PLOT_UP_BOUND - 10);
                glVertex2d(xAxis, PLOT_DOWN_BOUND + 10);
                for (Coordinate2D coordinate : graduation) {
                    glVertex2d(coordinate.x - 2, coordinate.y);
                    glVertex2d(coordinate.x + 2, coordinate.y);
                }
            }
            glEnd();
        }
    }

    private void plotGraphic() {
        List<Coordinate2D> coordinates = getProportionalPlotCoords();
        glLineWidth(plotLineWidth);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINE_STRIP);
        {
            for (Coordinate2D coordinate : coordinates) {
                glVertex2d(coordinate.x, coordinate.y);
            }
        }
        glEnd();
    }

    private void renderPopulationPoints() {
        List<Coordinate2D> coordinates = getProportionalPopulationPoints();
        glPointSize(5.0f);
        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_POINTS);
        {
            for (Coordinate2D coordinate : coordinates) {
                glVertex2d(coordinate.x, coordinate.y);
            }
        }
        glEnd();
    }

    private void renderHUD() {
        renderObjectiveFunction();
        renderGeneticAlgorithmDetails();
    }

    private void drawString(String sentence, Color color, Coordinate2D position, UnicodeFont font) {
        glEnable(GL_TEXTURE_2D);
        Color.white.bind();
        font.drawString((float) position.x, (float) position.y, sentence, color);
        glDisable(GL_TEXTURE_2D);
    }

    private List<Coordinate2D> getProportionalPlotCoords() {
        double[] xValues = stats.getXValues();
        double[] yValues = stats.getYValues();
        double xMin = xValues[0];
        double xMax = xValues[PLOT_RESOLUTION];
        double yMin = stats.getSmallestOFValue();
        double yMax = stats.getBiggestOFValue();
        List<Coordinate2D> coordinates = new ArrayList<>();
        for (int i = 0; i <= PLOT_RESOLUTION; i++) {
            double x = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                    Coordinate2D.getRatio(xMin, xMax, xValues[i]));
            double y = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                    1 - Coordinate2D.getRatio(yMin, yMax, yValues[i]));
            coordinates.add(new Coordinate2D(x, y));
        }
        return coordinates;
    }

    private List<Coordinate2D> getProportionalPopulationPoints() {
        float[] population = stats.getPopulation();
        double[] fitnesses = stats.getFitnesses();
        double xMin = stats.getXValues()[0];
        double xMax = stats.getXValues()[PLOT_RESOLUTION];
        double yMin = stats.getSmallestOFValue();
        double yMax = stats.getBiggestOFValue();
        List<Coordinate2D> coordinates = new ArrayList<>();
        for (int i = 0; i < population.length; i++) {
            if (population[i] >= stats.getXValues()[0]
                    && population[i] <= stats.getXValues()[PLOT_RESOLUTION]) {
                double x = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                        Coordinate2D.getRatio(xMin, xMax, population[i]));
                double y = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                        1 - Coordinate2D.getRatio(yMin, yMax, fitnesses[i]));
                coordinates.add(new Coordinate2D(x, y));
            }
        }
        return coordinates;
    }

    private List<Coordinate2D> getAxisConfiguration(double[] axisConfig, char axis) {
        double min = axisConfig[0];
        double max = axisConfig[AXIS_DIVISION_AMMOUNT - 1];
        List<Coordinate2D> coordinates = new ArrayList<>();
        for (int i = 0; i < AXIS_DIVISION_AMMOUNT; i++) {
            double x, y;
            if (axis == X_AXIS) {
                x = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                        Coordinate2D.getRatio(min, max, axisConfig[i]));
                y = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                        1 - Coordinate2D.getRatio(stats.getSmallestOFValue(), stats.getBiggestOFValue(), 0));
            } else {
                x = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                        Coordinate2D.getRatio(stats.getXValues()[0], stats.getXValues()[PLOT_RESOLUTION], 0));
                y = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                        1 - Coordinate2D.getRatio(min, max, axisConfig[i]));
            }
            coordinates.add(new Coordinate2D(x, y));
        }
        return coordinates;
    }

    private List<Coordinate2D> getHistoricDataChartCoords(double[] absolutValues) {
        List<Coordinate2D> relativeValues = new ArrayList<>();
        double x;
        double y;
        double step = (HISTORIC_CHART_MAX_X - HISTORIC_CHART_MIN_X) / HISTORIC_CHART_DIVISIONS;
        int count = absolutValues.length - 1;
        for (double absoluteValue : absolutValues) {
            x = HISTORIC_CHART_MAX_X - (count--) * step;
            y = Coordinate2D.getRelativePosition(HISTORIC_CHART_MIN_Y + 20,
                    HISTORIC_CHART_MAX_Y - 20, 1 - Coordinate2D.getRatio(historicalMin, historicalMax, absoluteValue));
            relativeValues.add(new Coordinate2D(x, y));
        }
        return relativeValues;
    }

    private void renderGeneticAlgorithmDetails() {
        renderStats();
        renderHistoricalGraphic();
    }

    private void renderStats() {
        Coordinate2D positioning = new Coordinate2D(HUD_BOUND_POSITION.x, HUD_BOUND_POSITION.y);
        drawString("OBJECTIVE FUNCTION: " + heuristic.getObjectiveFunction().getFormattedEquation(),
                Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("MIN x: " + decimal.format(stats.getXValues()[0]), Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("MAX x: " + decimal.format(stats.getXValues()[PLOT_RESOLUTION]), Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("POPULATION: " + stats.getPopulation().length, Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("GENERATION: " + stats.getGeneration(), Color.yellow, positioning, consoleFont12);
        positioning.y += 12;
        drawString("MIN FITNESS: " + decimal.format(historicalMin), Color.red, positioning, consoleFont12);
        positioning.y += 12;
        drawString("MAX FITNESS: " + decimal.format(historicalMax), Color.green, positioning, consoleFont12);
        positioning.y += 12;
        drawString("LOW FITNESS: " + decimal.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("min")), Color.red, positioning, consoleFont12);
        positioning.y += 12;
        drawString("HIGH FITNESS: " + decimal.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("max")), Color.green, positioning, consoleFont12);
        positioning.y += 12;
        drawString("AVG FITNESS: " + decimal.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("average")), Color.cyan, positioning, consoleFont12);
    }

    private void renderHistoricalGraphic() {
        drawLines(HISTORIC_CHART_STRUCTURE_COORDINATES[0], 1, new Color(0.1f, 0.1f, 0.1f), false, false);
        drawLines(HISTORIC_CHART_STRUCTURE_COORDINATES[1], 1, Color.white, false, false);
        double[] absoluteLowestFitnesses = new double[fitnessHistoric.size()];
        double[] absoluteHighestFitnesses = new double[fitnessHistoric.size()];
        double[] absoluteAverageFitnesses = new double[fitnessHistoric.size()];
        int count = 0;
        for (Map<String, Double> registry : fitnessHistoric) {
            absoluteLowestFitnesses[count] = registry.get("min");
            absoluteHighestFitnesses[count] = registry.get("max");
            absoluteAverageFitnesses[count++] = registry.get("average");
        }
        drawLines(getHistoricDataChartCoords(absoluteLowestFitnesses), 1f, Color.red, false, true);
        drawLines(getHistoricDataChartCoords(absoluteHighestFitnesses), 1f, Color.green, false, true);
        drawLines(getHistoricDataChartCoords(absoluteAverageFitnesses), 1f, Color.cyan, false, true);
    }

    private void renderInterface() {

    }
}
