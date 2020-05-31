/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testmultithread;

/**
 *
 * @author Admin
 */
public class MyClass extends Thread {
     public void run(){
     System.out.println("MyClass running");
   }
     public static void main(String args[]) {
         
        MyClass T1 = new MyClass ();
        T1.start();
     }
}


