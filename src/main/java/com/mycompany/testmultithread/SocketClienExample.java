/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testmultithread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 *
 * @author Admin
 */
public class SocketClienExample {
      public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host, 5000);
//        ObjectOutputStream oos = null;
//        ObjectInputStream ois = null;
//        for(int i=0; i<5;i++){
//            //establish socket connection to server
//            socket = new Socket(host.getHostName(), 5000);
//            //write to socket using ObjectOutputStream
//            oos = new ObjectOutputStream(socket.getOutputStream());
//            System.out.println("Sending request to Socket Server");
//            if(i==4)oos.writeObject("exit");
//            else oos.writeObject("quithu165-ngoquithu");
//            //read the server response message
//            ois = new ObjectInputStream(socket.getInputStream());
//            String message = (String) ois.readObject();
//            System.out.println("Message: " + message);
//            //close resources
//            ois.close();
//            oos.close();
//            Thread.sleep(100);
//        }
             BufferedReader input = new BufferedReader(
                new  InputStreamReader (socket.getInputStream()));
            PrintWriter output = 
                    new PrintWriter(socket.getOutputStream(), true);
            output.println("1thuyduong-quithu-192.168.5");
            String echoString = input.readLine();
            System.out.println("Received server input: " + echoString);
            
    }
}
