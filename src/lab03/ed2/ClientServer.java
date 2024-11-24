/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab03.ed2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientServer {

    public static final int SERVER_PORT = 8081;
    public static final String SERVER_ADRESS = "localhost";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = 5000000;
        int t = 1;
        int choice = 1;
        int count = 0;
        String rutaArchivo = "datos.txt";

        // Elegir algoritmo
        System.out.println("Seleccione el algoritmo: ");
        System.out.println("1. QuickSort\n2. MergeSort\n3. HeapSort");
        choice = scanner.nextInt();

        System.out.print("Ingrese el tiempo límite para cada worker (en segundos): \n");
        t = scanner.nextInt();

        // Generar vector aleatorio
        int[] vector;
        vector = leerVectorDesdeArchivo(rutaArchivo);
        Task task = new Task(vector, choice, t);
        System.out.println("Esperando para nuevas conexiones...");
        //Procesamiento de datos con los Worker_0 y Worker_1
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket socketWorker = serverSocket.accept();

                var worker = new ClientApp(socketWorker, count, task);

                //inicializa la conexion con el worker en otro hilo
                new Thread(worker).start();

                count++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static int[] leerVectorDesdeArchivo(String rutaArchivo) {
        BufferedReader reader = null;
        int[] vector = null;
        try {
            
            
            reader = new BufferedReader(new FileReader(rutaArchivo));
            String linea;
            StringBuilder contenido = new StringBuilder();
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append(",");
            }   reader.close();
            String[] numerosStr = contenido.toString().split(",");
            vector = new int[numerosStr.length];
            for (int i = 0; i < numerosStr.length; i++) {
                vector[i] = Integer.parseInt(numerosStr[i].trim());
            }   return vector;
            
            
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo");
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return vector;
    }
}
