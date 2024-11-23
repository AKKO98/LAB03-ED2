/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ClientApp implements Runnable{


    private static final String PEER_HOST = "localhost";
    private Socket socket;
    private int id;
    private String clientName;
    private Task task;
    private ObjectInputStream socketIn; 
    private ObjectOutputStream socketOut;
    

    public ClientApp(Socket socketr, int idr, Task taskr) throws IOException {
        socket = socketr;
        id = idr;
        clientName = "<Worker_" + id + "> ";
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());
        task = taskr;
    }
    
    private void println(String str){
        System.out.println(clientName + str);
    }
    
    void Imprimirresultado(Result resultado){
        System.out.println("\n\n El vector fue ordenado en " + resultado.getTime() + " Milisegundos por " + clientName);
        System.out.println("Vector ordenado: " + Arrays.toString(resultado.getVector()));
    }

    @Override
    public void run() {
        System.out.println("Conexion exitosa con: " + clientName);
        try {
            String clientMessage = "";
            while (!clientMessage.equals("end_conexion")) {
                switch (id) {
                    case 0 -> {
                        socketOut.writeObject(task);
                        clientMessage = (String) socketIn.readObject();
                        Result resultado = (Result) socketIn.readObject();
                        if (resultado != null) {
                            Imprimirresultado(resultado);
                        }
                    }
                    
                    default -> {
                        clientMessage = (String) socketIn.readObject();
                        Result resultado = (Result) socketIn.readObject();
                        if (resultado != null) {
                            Imprimirresultado(resultado);
                        }
                    }
                }
            }
            
            socket.close();
            
            System.out.println("Conexion terminada exitosamente con " + clientName);
        } catch (IOException | ClassNotFoundException e) {
            println("Ocurrio un error en la conexion");
        }
    }
}
