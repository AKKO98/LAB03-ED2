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

    public static void main(String[] args) {
        //Trata de conectar al servidor (ClientServer)
        do {

            try (
                    Socket socketWorker = new Socket(ClientServer.SERVER_ADRESS, WORKER0_PORT); var workerSocketOut = new ObjectOutputStream(socketWorker.getOutputStream()); var workerSocketIn = new ObjectInputStream(socketWorker.getInputStream());) {
                System.out.println("\n\n\n\n\n\n\n\n\nConexion exitosa con Worker 0");
                Task task;
                WorkToDo work;
                //Envia solicitud de conexion hacia worker0
                try (Socket socketServer = new Socket(ClientServer.SERVER_ADRESS, ClientServer.SERVER_PORT); var socketOut = new ObjectOutputStream(socketServer.getOutputStream());) {
                    System.out.println("Conexion exitosa con ClientServer");

                    //logica
                    work = (WorkToDo) workerSocketIn.readObject();
                    System.out.println("Recibido worktodo from <Worker_0>");

                    while (!work.sorter.isFinished()) {

                        switch (work.task.getAlgorithm()) {
                            case 1 -> { // QuickSort
                                if (!work.sorter.isFinished()) {
                                    work.sorter.resumeQuickSort();
                                }
                            }
                            case 2 -> { // MergeSort

                                if (!work.sorter.isFinished()) {
                                    work.sorter.resumeMergeSort();
                                }

                            }
                            case 3 -> { // HeapSort

                                if (!work.sorter.isFinished()) {
                                    work.sorter.resumeHeapSort();
                                }

                            }
                        }
                        if (work.sorter.isFinished() && !work.meta) {
                            long endTime = System.currentTimeMillis();
                            var totalTime = (endTime - work.startTime);
                            socketOut.writeObject("end_conexion");
                            socketOut.writeObject(new Result(work.sorter.getArray(), totalTime));
                            System.out.println("Worker1 termino primero");
                            work.meta = true;
                            Worker0.readiToConnect = false;
                            workerSocketOut.writeObject(work);
                            
                        } else {
                            System.out.println("Tiempo de espera superado!");
                            System.out.println("Enviando a <Worker_0>");
                            workerSocketOut.writeObject(work);
                            workerSocketOut.flush();
                            work = (WorkToDo) workerSocketIn.readObject();
                            if (work.meta) {
                                socketOut.writeObject("end_conexion");
                                socketOut.writeObject(null);
                                socketWorker.close();
                            } else {
                                System.out.println("Recibido work to do");
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println("Class not found exception.");
                    Logger.getLogger(Worker0.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Worker1.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                System.out.println("Error al tratar de conectar con Worker0");
                try {
                    Thread.sleep(500); // esperar 500 milisegundos para volver a intentar conectar con Worker0
                } catch (InterruptedException ex1) {
                    System.out.println("Error al tratar de dormir el hilo");
                    Logger.getLogger(Worker1.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        } while (!Worker0.readiToConnect);
    }
}
