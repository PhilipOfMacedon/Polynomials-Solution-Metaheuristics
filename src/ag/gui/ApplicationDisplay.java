/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import ag.core.GeneticAlgorithm;
import ag.core.ObjectiveFunction;
import ag.utils.Coordinate2D;
import java.awt.Font;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author filip
 */
public class ApplicationDisplay {

    private TrueTypeFont consoleFont;
    private String fontName;
    private GeneticAlgorithm heuristic;

    public ApplicationDisplay(String font, GeneticAlgorithm ag) {
        fontName = font;
        heuristic = ag;
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
        ObjectiveFunction function = heuristic.getObjectiveFunction();
        drawString("test", Color.white, new Coordinate2D(10, 10));
    }

    private void renderGeneticAlgorithmDetails() {

    }

    private void drawString(String sentence, Color color, Coordinate2D position) {
        Color.white.bind();
        consoleFont.drawString(position.x, position.y, sentence, color);
    }
}
