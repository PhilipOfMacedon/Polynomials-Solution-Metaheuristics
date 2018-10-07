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
import static ag.core.GeneticAlgorithmStats.NUMBER_OF_POINTS;

/**
 *
 * @author filip
 */
public class ApplicationDisplay {

    private static final double PLOT_LEFT_BOUND = 320D;
    private static final double PLOT_RIGHT_BOUND = 780D;
    private static final double PLOT_UP_BOUND = 20D;
    private static final double PLOT_DOWN_BOUND = 280D;
    private TrueTypeFont consoleFont;
    private String fontName;
    private GeneticAlgorithmStats stats;
    private GeneticAlgorithm heuristic;

    public ApplicationDisplay(String font, GeneticAlgorithm ag) {
        fontName = font;
        heuristic = ag;
        ag.addGeneticAlgorithmEventListener(new GeneticAlgorithmEventListener() {
            @Override
            public void generationEvolved(GeneticAlgorithmStats statistics) {
                stats = statistics;
            }
        });
    }

    public void updateStats(GeneticAlgorithmStats newStats) {
        stats = newStats;
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
        Font awtFont = new Font(fontName, Font.BOLD, 12);
        consoleFont = new TrueTypeFont(awtFont, true);
    }

    private void render() {
        separateScreen();
        renderObjectiveFunction();
        renderGeneticAlgorithmDetails();
    }

    private void separateScreen() {
        glDisable(GL_TEXTURE_2D);
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
        glEnable(GL_TEXTURE_2D);
    }

    private void renderObjectiveFunction() {
        List<Coordinate2D> coordinates = getProportionalPlotCoords();
        glDisable(GL_TEXTURE_2D);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(GL_LINE_STRIP);
        {
            for (Coordinate2D coordinate : coordinates) {
                GL11.glVertex2d(coordinate.x, coordinate.y);
            }
        }
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        drawString("test", Color.white, new Coordinate2D(10, 10));
    }

    private void renderGeneticAlgorithmDetails() {

    }

    private void drawString(String sentence, Color color, Coordinate2D position) {
        Color.white.bind();
        consoleFont.drawString((float) position.x, (float) position.y, sentence, color);
    }

    private List<Coordinate2D> getProportionalPlotCoords() {
        double[] xValues = stats.getXValues();
        double[] yValues = stats.getYValues();
        double xMin = xValues[0];
        double xMax = xValues[NUMBER_OF_POINTS - 1];
        double yMin = stats.getSmallestOFValue();
        double yMax = stats.getBiggestOFValue();
        List<Coordinate2D> coordinates = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            double x = getRelativePosition(PLOT_LEFT_BOUND, PLOT_RIGHT_BOUND, getRatio(xMin, xMax, xValues[i]));
            double y = getRelativePosition(PLOT_UP_BOUND, PLOT_DOWN_BOUND, 1 - getRatio(yMin, yMax, yValues[i]));
            coordinates.add(new Coordinate2D(x, y));
        }
        return coordinates;
    }

    private double getRatio(double min, double max, double value) {
        return (value - min) / (max - min);
    }

    private double getRelativePosition(double min, double max, double ratio) {
        return (max - min) * ratio + min;
    }
}
