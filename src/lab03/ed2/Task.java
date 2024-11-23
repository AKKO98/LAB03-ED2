/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] vector;
    private int algorithm;
    private double time;

    public Task(int[] vector, int algorithm, double time) {
        this.vector = vector;
        this.algorithm = algorithm;
        this.time = time;
    }

    public int[] getVector() {
        return vector;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public double getTime() {
        return time;
    }
}
