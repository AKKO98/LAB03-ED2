/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.Serializable;

/**
 *
 * @author bazas
 */
public class WorkToDo implements Serializable{
    private static final long serialVersionUID = 1L;
    Task task;
    Sorter sorter;
    long startTime;
    volatile boolean meta = false;


    public WorkToDo(Task task) {
        this.task = task;
        sorter = null;
        startTime = System.currentTimeMillis();
    }
    
    public WorkToDo() {
        this.task = null;
        sorter = null;
        startTime = System.currentTimeMillis();
    }
    
}
