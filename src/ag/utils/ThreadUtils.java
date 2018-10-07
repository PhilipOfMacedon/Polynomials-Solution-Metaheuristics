/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag.utils;

/**
 *
 * @author filip
 */
public class ThreadUtils {
    public static void holdOn(long millis) {
        long ti = System.currentTimeMillis();
        long tf;
        do {
            tf = System.currentTimeMillis();
            System.out.print("");
        } while (tf - ti < millis);
    }
}
