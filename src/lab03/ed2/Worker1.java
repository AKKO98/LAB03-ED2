/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bazas
 */
public class Worker1 extends Worker {

    private static int dialoge = 0;

    private static void println(String t) {
        System.out.println(dialoge + ": " + t);
        dialoge++;
    }

    public static void main(String[] args) {
        //Trata de conectar al servidor (ClientServer)
        boolean end = false;
        do {

            try (
                    Socket socketWorker = new Socket(ClientServer.SERVER_ADRESS, WORKER0_PORT); var workerSocketOut = new ObjectOutputStream(socketWorker.getOutputStream()); var workerSocketIn = new ObjectInputStream(socketWorker.getInputStream());) {
                println("Conexion exitosa con <Worker_0>.");
                Task task;
                WorkToDo work;
                //Envia solicitud de conexion hacia worker0
                try (Socket socketServer = new Socket(ClientServer.SERVER_ADRESS, ClientServer.SERVER_PORT); var socketOut = new ObjectOutputStream(socketServer.getOutputStream());) {
                    println("Conexion exitosa con <ClientServer>.");

                    //logica
                    println("Esperando a recibir tarea a realizar...");
                    work = (WorkToDo) workerSocketIn.readObject();
                    println("Recibida tarea a realizar de <Worker_0>.");

                    if (work.meta) {
                        socketOut.writeObject("end_conexion");
                        socketOut.writeObject(null);
                        socketWorker.close();
                        end = true;
                        println("<Worker_0> termino primero.");
                        println("Conexion cerrada con <ClientServer> y <Worker_1>.");
                    }

                    while (!work.sorter.isFinished()) {

                        switch (work.task.getAlgorithm()) {
                            case 1 -> { // QuickSort
                                if (!work.sorter.isFinished()) {
                                    println("Empezando a ordenar el vector...");
                                    work.sorter.resumeQuickSort();
                                }
                            }
                            case 2 -> { // MergeSort

                                if (!work.sorter.isFinished()) {
                                    println("Empezando a ordenar el vector...");
                                    work.sorter.resumeMergeSort();
                                }

                            }
                            case 3 -> { // HeapSort

                                if (!work.sorter.isFinished()) {
                                    println("Empezando a ordenar el vector...");
                                    work.sorter.resumeHeapSort();
                                }

                            }
                        }
                        if (work.sorter.isFinished() && !work.meta) {
                            long endTime = System.currentTimeMillis();
                            var totalTime = (endTime - work.startTime);
                            socketOut.writeObject("end_conexion");
                            socketOut.writeObject(new Result(work.sorter.getArray(), totalTime));
                            println("<Worker_1> Termino de ordenar el vector...");
                            work.meta = true;
                            end = true;
                            workerSocketOut.writeObject(work);
                            println("Conexion cerrada con <ClientServer> y <Worker_0>.");
                        } else {
                            println("Tiempo de espera superado!");
                            println("Enviando a <Worker_0>");
                            workerSocketOut.writeObject(work);
                            workerSocketOut.flush();
                            println("Esperando el vector a ordenar...");
                            work = (WorkToDo) workerSocketIn.readObject();
                            if (work.meta) {
                                socketOut.writeObject("end_conexion");
                                socketOut.writeObject(null);
                                socketWorker.close();
                                println("<Worker_0> termino primero.");
                                println("Conexion cerrada con <ClientServer> y <Worker_0>.");
                                end = true;
                            } else {
                                println("Recibido vector a ordenar!");
                            }
                        }
                    }

                } catch (ClassNotFoundException ex) {
                    System.out.println("\nClass not found exception.");
                    Logger.getLogger(Worker0.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Worker1.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                System.out.println("\nError al tratar de conectar con Worker0");
                try {
                    Thread.sleep(1000); // esperar 1 segundo para volver a intentar conectar con Worker0
                } catch (InterruptedException ex1) {
                    System.out.println("\nError al tratar de dormir el hilo");
                    Logger.getLogger(Worker1.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        } while (!Worker0.readiToConnect && !end);
    }
}
