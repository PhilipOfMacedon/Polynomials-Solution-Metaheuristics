/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import ag.core.GeneticAlgorithm;
import ag.core.GeneticAlgorithmStats;
import ag.utils.Coordinate2D;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import static ag.core.GeneticAlgorithmStats.*;
import ag.utils.ThreadUtils;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
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
    private static final DecimalFormat DECIMAL = new DecimalFormat("#.##");
    private final OpenGLButton optionsButton = new OpenGLButton(new Coordinate2D(20, 40), 
            new Coordinate2D(280, 90), new Coordinate2D(116, 53), Color.green, Color.black);
    private final OpenGLButton aboutButton = new OpenGLButton(new Coordinate2D(20, 210), 
            new Coordinate2D(280, 260), new Coordinate2D(125, 223), Color.green, Color.black);
    private final OpenGLButton decSpeedButton = new OpenGLButton(new Coordinate2D(20, 560), 
            new Coordinate2D(40, 580), new Coordinate2D(25, 558), Color.green, Color.black);
    private final OpenGLButton incSpeedButton = new OpenGLButton(new Coordinate2D(50, 560), 
            new Coordinate2D(70, 580), new Coordinate2D(55, 558), Color.green, Color.black);
    private final OpenGLButton randomizeButton = new OpenGLButton(new Coordinate2D(140, 560), 
            new Coordinate2D(160, 580), new Coordinate2D(145, 558), Color.green, Color.black);
    private final Coordinate2D logoCoordinate1 = new Coordinate2D(80, 120);
    private final Coordinate2D logoCoordinate2 = new Coordinate2D(82, 145);
    private final Coordinate2D logoCoordinate3 = new Coordinate2D(155, 145);
    private long waitingTime = 1500L;
    private float plotLineWidth = 1.0f;
    private UnicodeFont consoleFont12;
    private UnicodeFont consoleFont16;
    private UnicodeFont consoleFont24;
    private final String fontName;
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
            divisionCoords.add(new Coordinate2D(HISTORIC_CHART_MIN_X + i
                    * (HISTORIC_CHART_MAX_X - HISTORIC_CHART_MIN_X) / HISTORIC_CHART_DIVISIONS, HISTORIC_CHART_MAX_Y));
            divisionCoords.add(new Coordinate2D(HISTORIC_CHART_MIN_X + i
                    * (HISTORIC_CHART_MAX_X - HISTORIC_CHART_MIN_X) / HISTORIC_CHART_DIVISIONS, HISTORIC_CHART_MIN_Y));
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
        resetHistoricStats();
        isRunning = true;
        heuristic.updateStats();
        startThread();
    }

    private void subscribeToAG() {
        heuristic.addGeneticAlgorithmEventListener((GeneticAlgorithmStats statistics) -> {
            updateStats(statistics);
        });
    }

    private void resetHistoricStats() {
        fitnessHistoric = new ArrayList<>();
        historicalMin = Float.MAX_VALUE;
        historicalMax = Float.MIN_VALUE;
        subscribeToAG();
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
            Display.setTitle("Polynomial Genetic Machine V1.1");
        } catch (LWJGLException e) {
            JOptionPane.showMessageDialog(null, e.getCause(), "Aw shit", JOptionPane.ERROR_MESSAGE);
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
            consoleFont12.loadGlyphs();
            consoleFont16 = new UnicodeFont(new Font(fontName, Font.BOLD, 16));
            consoleFont16.getEffects().add(new ColorEffect(java.awt.Color.white));
            consoleFont16.addAsciiGlyphs();
            consoleFont16.loadGlyphs();
            consoleFont24 = new UnicodeFont(new Font(fontName, Font.BOLD, 24));
            consoleFont24.getEffects().add(new ColorEffect(java.awt.Color.white));
            consoleFont24.addAsciiGlyphs();
            consoleFont24.loadGlyphs();
        } catch (SlickException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Aw shit", JOptionPane.ERROR_MESSAGE);
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
        int x = Mouse.getX();
        int y = 600 - Mouse.getY();
        evaluateMouseHover(x, y);
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                evaluateMouseButtonPressed(x, y);
            } else {
                evaluateMouseButtonReleased(x, y);
            }
        }
    }

    private void evaluateMouseHover(int x, int y) {
        if (x > PLOT_LEFT_BOUND - 20 && x < PLOT_RIGHT_BOUND + 20
                && y > PLOT_UP_BOUND - 20 && y < PLOT_DOWN_BOUND + 20) {
            plotLineWidth = 3.0f;
        } else {
            plotLineWidth = 1.0f;
        }
        if (optionsButton.colided(x, y)) {
            optionsButton.stroke = 3f;
            optionsButton.bgColor = new Color(0.0f, 0.2f, 0.0f);
        } else {
            optionsButton.stroke = 1f;
            optionsButton.bgColor = Color.black;
        }
        if (aboutButton.colided(x, y)) {
            aboutButton.stroke = 3f;
            aboutButton.bgColor = new Color(0.0f, 0.2f, 0.0f);
        } else {
            aboutButton.stroke = 1f;
            aboutButton.bgColor = Color.black;
        }
        if (decSpeedButton.colided(x, y)) {
            decSpeedButton.stroke = 2f;
            decSpeedButton.bgColor = new Color(0.2f, 0.1f, 0.0f);
        } else {
            decSpeedButton.stroke = 1f;
            decSpeedButton.bgColor = Color.black;
        }
        if (incSpeedButton.colided(x, y)) {
            incSpeedButton.stroke = 2f;
            incSpeedButton.bgColor = new Color(0.0f, 0.2f, 0.2f);
        } else {
            incSpeedButton.stroke = 1f;
            incSpeedButton.bgColor = Color.black;
        }
        if (randomizeButton.colided(x, y)) {
            randomizeButton.stroke = 2f;
            randomizeButton.bgColor = new Color(0.2f, 0.0f, 0.0f);
        } else {
            randomizeButton.stroke = 1f;
            randomizeButton.bgColor = Color.black;
        }
    }

    private void evaluateMouseButtonPressed(int x, int y) {
        switch (Mouse.getEventButton()) {
            //Left button pressed
            case 0:
                optionsButton.clickedInside = false;
                aboutButton.clickedInside = false;
                randomizeButton.clickedInside = false;
                if (optionsButton.colided(x, y)) {
                    optionsButton.clickedInside = true;
                } else if (aboutButton.colided(x, y)) {
                    aboutButton.clickedInside = true;
                } else if (decSpeedButton.colided(x, y)) {
                    decUpdateSpeed();
                } else if (incSpeedButton.colided(x, y)) {
                    incUpdateSpeed();
                } else if (randomizeButton.colided(x, y)) {
                    randomizeButton.clickedInside = true;
                }
                break;
            //Right button pressed
            case 1:
                break;
            case 2:
                //Middle button pressed
                showPositionAtConsole();
                break;
            default:
                break;
        }
    }

    private void evaluateMouseButtonReleased(int x, int y) {
        switch (Mouse.getEventButton()) {
            //Left button released
            case 0:
                if (optionsButton.colided(x, y) && optionsButton.hasClickedInside()) {
                    GeneticAlgorithm newAG = ConfigGeneticAlgorithmOptions.getConfig(heuristic);
                    if (newAG != null) {
                        heuristic.terminate();
                        heuristic = newAG;
                        resetHistoricStats();
                    }
                } else if (aboutButton.colided(x, y) && aboutButton.hasClickedInside()) {
                    HelpDialog.showMessage();
                } else if (randomizeButton.colided(x, y) && randomizeButton.hasClickedInside()) {
                    heuristic.randomizePopulation();
                }
                optionsButton.clickedInside = false;
                randomizeButton.clickedInside = false;
                break;
            //Right button released
            case 1:
                break;
            case 2:
                //Middle button released
                showPositionAtConsole();
                break;
            default:
                break;
        }
    }
    
    private void incUpdateSpeed() {
        if (waitingTime >= 500) {
            waitingTime -= 250;
        }
    }
    
    private void decUpdateSpeed() {
        if (waitingTime <= 1250) {
            waitingTime += 250;
        }
    }

    private void showPositionAtConsole() {
        int x = Mouse.getX();
        int y = 600 - Mouse.getY();
        System.err.println("Mouse @ X:" + x + " Y:" + y);
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
        renderLogo();
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
        drawString("MIN x: " + DECIMAL.format(stats.getXValues()[0]), Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("MAX x: " + DECIMAL.format(stats.getXValues()[PLOT_RESOLUTION]), Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("POPULATION: " + stats.getPopulation().length, Color.white, positioning, consoleFont12);
        positioning.y += 12;
        drawString("GENERATION: " + stats.getGeneration(), Color.yellow, positioning, consoleFont12);
        if (!fitnessHistoric.isEmpty()) {
            positioning.y += 12;
            drawString("MIN FITNESS: " + DECIMAL.format(historicalMin), Color.orange, positioning, consoleFont12);
            positioning.y += 12;
            drawString("MAX FITNESS: " + DECIMAL.format(historicalMax), new Color(0.2f, 1.0f, 0.3f), positioning, consoleFont12);
            positioning.y += 12;
            drawString("LOW FITNESS: " + DECIMAL.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("min")), Color.red, positioning, consoleFont12);
            positioning.y += 12;
            drawString("HIGH FITNESS: " + DECIMAL.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("max")), Color.green, positioning, consoleFont12);
            positioning.y += 12;
            drawString("AVG FITNESS: " + DECIMAL.format(fitnessHistoric.get(fitnessHistoric.size() - 1).get("average")), Color.cyan, positioning, consoleFont12);
        }
        positioning.y += 24;
        drawString("AVG X VALUE: " + DECIMAL.format(stats.getAverageIndividual()), Color.magenta, positioning, consoleFont12);
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

    private void renderLogo() {
        drawString("POLYNOMIAL", Color.green, logoCoordinate1, consoleFont24);
        drawString("GENETIC", Color.orange, logoCoordinate2, consoleFont16);
        drawString("MACHINE", Color.orange, logoCoordinate3, consoleFont16);
    }
    
    private void renderInterface() {
        renderButtons();
    }

    private void renderButtons() {
        drawButton(optionsButton, "OPTIONS");
        drawButton(aboutButton, "ABOUT");
        drawButton(decSpeedButton, "-");
        drawButton(incSpeedButton, "+");
        drawButton(randomizeButton, "R");
    }

    private void drawButton(OpenGLButton button, String text) {
        drawQuad(button.upperLeft, button.lowerRight, button.bgColor);
        drawLines(button.borderLines, button.stroke, Color.green, false, true);
        drawString(text, Color.green, button.textPosition, consoleFont16);
    }
    
    private void startThread() {
        Thread agAutoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    ThreadUtils.holdOn(waitingTime);
                    heuristic.evolveOneStep();
                }
            }
        });
        agAutoThread.start();
    }
}
