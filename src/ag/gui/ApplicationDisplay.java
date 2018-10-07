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
import org.newdawn.slick.TrueTypeFont;
import static ag.core.GeneticAlgorithmStats.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author filip
 */
public class ApplicationDisplay {

    private static final double PLOT_LEFT_BOUND = 320D;
    private static final double PLOT_RIGHT_BOUND = 780D;
    private static final double PLOT_UP_BOUND = 20D;
    private static final double PLOT_DOWN_BOUND = 280D;
    private static final char X_AXIS = 'x';
    private static final char Y_AXIS = 'y';
    private static final DecimalFormat decimal = new DecimalFormat(".##");
    private TrueTypeFont consoleFont12;
    private TrueTypeFont consoleFont8;
    private String fontName;
    private GeneticAlgorithmStats stats;
    private GeneticAlgorithm heuristic;
    private List<Map<String, Double>> fitnessHistoric;
    public boolean isRunning;

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
        Map<String, Double> historicPoint = new HashMap<>();
        historicPoint.put("min", stats.getSmallestFitness());
        historicPoint.put("max", stats.getBiggestFitness());
        historicPoint.put("average", stats.getAverageFitness());
        fitnessHistoric.add(historicPoint);
    }

    public void start() {
        initGL(800, 600);
        initFont();

        while (true) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            render();

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
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void initFont() {
        // load a default java font
        consoleFont12 = new TrueTypeFont(new Font(fontName, Font.BOLD, 12), true);
        consoleFont8 = new TrueTypeFont(new Font(fontName, Font.BOLD, 8), true);
    }

    private void render() {
        separateScreen();
        renderObjectiveFunction();
        renderGeneticAlgorithmDetails();
    }

    private void separateScreen() {

        GL11.glColor3f(0.0f, 0.0f, 0.25f);
        GL11.glBegin(GL_QUADS);
        {
            GL11.glVertex2f(300f, 0f);
            GL11.glVertex2f(300f, 300f);
            GL11.glVertex2f(800f, 300f);
            GL11.glVertex2f(800f, 0f);
        }
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(3f);
        GL11.glBegin(GL_LINES);
        {
            //
            GL11.glVertex2f(300f, 300f);
            GL11.glVertex2f(800f, 300f);
            //
            GL11.glVertex2f(300f, 0f);
            GL11.glVertex2f(300f, 300f);
            //
            GL11.glVertex2f(0f, 300f);
            GL11.glVertex2f(300f, 300f);
        }
        GL11.glEnd();

    }

    private void renderObjectiveFunction() {
        glDisable(GL_TEXTURE_2D);
        renderPlotAxis();
        plotGraphic();
        renderPopulationPoints();
    }

    private void renderPlotAxis() {
        GL11.glLineWidth(1.0f);
        if (stats.yRangeCrossesXAxis()) {
            double yAxis = Coordinate2D.getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND,
                    1 - Coordinate2D.getRatio(stats.getSmallestOFValue(), stats.getBiggestOFValue(), 0));
            List<Coordinate2D> graduation = getAxisConfiguration(stats.getxAxis(), X_AXIS);
            GL11.glColor3f(1.0f, 1.0f, 0.0f);
            GL11.glBegin(GL_LINES);
            {
                GL11.glVertex2d(PLOT_LEFT_BOUND - 10, yAxis);
                GL11.glVertex2d(PLOT_RIGHT_BOUND + 10, yAxis);
                for (Coordinate2D coordinate : graduation) {
                    GL11.glVertex2d(coordinate.x, coordinate.y - 2);
                    GL11.glVertex2d(coordinate.x, coordinate.y + 2);
                }
            }
            GL11.glEnd();
        }
        if (stats.xRangeCrossesYAxis()) {
            double xAxis = Coordinate2D.getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND,
                    Coordinate2D.getRatio(stats.getXValues()[0], stats.getXValues()[PLOT_RESOLUTION], 0));
            List<Coordinate2D> graduation = getAxisConfiguration(stats.getyAxis(), Y_AXIS);
            GL11.glColor3f(1.0f, 1.0f, 0.0f);
            GL11.glBegin(GL_LINES);
            {
                GL11.glVertex2d(xAxis, PLOT_UP_BOUND - 10);
                GL11.glVertex2d(xAxis, PLOT_DOWN_BOUND + 10);
                for (Coordinate2D coordinate : graduation) {
                    GL11.glVertex2d(coordinate.x - 2, coordinate.y);
                    GL11.glVertex2d(coordinate.x + 2, coordinate.y);
                }
            }
            GL11.glEnd();
        }
    }

    private void plotGraphic() {
        List<Coordinate2D> coordinates = getProportionalPlotCoords();
        GL11.glLineWidth(1.0f);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL_LINE_STRIP);
        {
            for (Coordinate2D coordinate : coordinates) {
                GL11.glVertex2d(coordinate.x, coordinate.y);
            }
        }
        GL11.glEnd();
    }

    private void renderPopulationPoints() {
        List<Coordinate2D> coordinates = getProportionalPopulationPoints();
        GL11.glPointSize(5.0f);
        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glBegin(GL_POINTS);
        {
            for (Coordinate2D coordinate : coordinates) {
                GL11.glVertex2d(coordinate.x, coordinate.y);
            }
        }
        GL11.glEnd();
    }

    private void renderGeneticAlgorithmDetails() {

    }

    private void drawString(String sentence, Color color, Coordinate2D position, TrueTypeFont font) {
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
}
