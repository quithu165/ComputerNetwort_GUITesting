/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source_code;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class server {
    //private ArrayList<dataPeer> dataPeer = null;	
    //private static ServerSocket server;						
    //private static Socket connection;	
    //private int port = 12345;
    public static void  main(String[] args) throws IOException{
        try{
            int port = 12345;
            ServerSocket server = new ServerSocket(port);
            Socket connection = server.accept();
            System.out.println("Client connected at: " + connection);
        }
        catch (UnknownHostException e){
            System.out.println("Host not found: " + e.getMessage());
        }
        catch (IOException ioe) {
            System.out.println("I/O Error " + ioe.getMessage());
        }
    }
    
}
