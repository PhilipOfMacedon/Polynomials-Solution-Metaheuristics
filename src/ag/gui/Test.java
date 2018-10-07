/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.gui;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author filip
 */
public class Test {

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.setTitle("A fresh display!");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        while (!Display.isCloseRequested()) {
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        System.exit(0);
    }
}
