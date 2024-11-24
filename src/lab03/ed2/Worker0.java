/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bazas
 */
public class Worker0 extends Worker {
    public static volatile boolean readiToConnect = false;
    private static int dialoge = 0;
    
    private static void println(String t) {
        System.out.println(dialoge + ": " + t);
        dialoge++;
    }

    public static void main(String[] args) {
        //Trata de conectar al servidor (ClientServer)
        try (Socket socketServer = new Socket(ClientServer.SERVER_ADRESS, ClientServer.SERVER_PORT); var socketOut = new ObjectOutputStream(socketServer.getOutputStream()); var socketIn = new ObjectInputStream(socketServer.getInputStream());) {
            println("Conexion exitosa con <ClientServer>.");
            
            println("Esperando a recibir la tarea a realizar...");
            Task task = (Task) socketIn.readObject();
            println("Tarea a realizar recibida correctamente.");
            
            var work = new WorkToDo(task);
            
            //Conexion con Worker1
            println("Esperando conexion con <Worker_1>.");
            readiToConnect = true;
            try (ServerSocket serverWorkerSocket = new ServerSocket(WORKER0_PORT); Socket workerSocket = serverWorkerSocket.accept(); //Pausa el hilo hasta que ocurra una conexion con Worker1
                     var workerSocketOut = new ObjectOutputStream(workerSocket.getOutputStream()); var workerSocketIn = new ObjectInputStream(workerSocket.getInputStream());) {
                println("Conexion exitosa con <Worker_1>.");

                work.startTime = System.currentTimeMillis();
                work.sorter = new Sorter(task.getVector());
                while (!work.sorter.isFinished()) {
                    //logica
                    
                    switch (work.task.getAlgorithm()) {
                        case 1 -> { // QuickSort
                            if (!work.sorter.isFinished()) {
                                println("Empezando a ordenar el vector...");
                                work.sorter.startQuickSort((int) work.task.getTime());
                            }
                            
                        }
                        case 2 -> { // MergeSort
                            if (!work.sorter.isFinished()) {
                                println("Empezando a ordenar el vector...");
                                work.sorter.startMergeSort((int) work.task.getTime());
                            }
                        }
                        case 3 -> { // HeapSort
                            if (!work.sorter.isFinished()) {
                                println("Empezando a ordenar el vector...");
                                work.sorter.startHeapSort((int) work.task.getTime());
                            }

                        }
                    }

                    if (work.sorter.isFinished() && !work.meta) {
                        var totalTime = (System.currentTimeMillis() - work.startTime);
                        socketOut.writeObject("end_conexion");
                        socketOut.writeObject(new Result(work.sorter.getArray(), totalTime));
                        println("<Worker_0> Termino de ordenar el vector.");
                        work.meta = true;
                        workerSocketOut.writeObject(work);
                        workerSocketOut.flush();
                    } else {
                        println("Tiempo de espera superado!");
                        println("Enviando a <Worker_1>.");
                        workerSocketOut.writeObject(work);
                        workerSocketOut.flush();
                        println("Esperando el vector a ordenar...");
                        work = (WorkToDo) workerSocketIn.readObject();
                        if (work.meta) {
                                socketOut.writeObject("end_conexion");
                                socketOut.writeObject(null);
                                workerSocket.close();
                                println("<Worker_1> termino primero.");
                                println("Conexion cerrada con <ClientServer> y <Worker_1>.");
                            } else {
                                println("Recibido vector a ordenar!");
                            }
                    }
                }

            } catch (IOException e) {
                System.out.println("\nError con la conexion con Worker1.");
            }

        } catch (IOException ex) {
            System.out.println("\nError con la conexion con el server.");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nClass not found exception.");
            Logger.getLogger(Worker0.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
