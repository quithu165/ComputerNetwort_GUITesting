package com.mycompany.testmultithread;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;



public class  server{

    public static void main(String[] args) {
	try(ServerSocket serverSocket = new ServerSocket(5000)) {
             String fileName = "src/data/ip.xml";
             File myFile = new File(fileName);
            //InetAddress host = InetAddress.getLocalHost();
           // System.out.println(host.toString() + "\n");
            while(true) {
                Echoer echoer = new Echoer(serverSocket.accept());
                System.out.println("New client connected");
                echoer.start();
               
            }
            
        } catch(IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }
}
