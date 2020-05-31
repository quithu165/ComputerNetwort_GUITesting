package com.mycompany.testmultithread;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class server {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException{
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            String fileName = "src/data/ip.xml";
            File myFile = new File(fileName);

            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElementNS("USER IP", "ip");
            doc.appendChild(root);
            
            TransformerFactory transformerFactory
                    = TransformerFactory.newInstance();
            Transformer transf = transformerFactory.newTransformer();

            transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transf.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);

            StreamResult file = new StreamResult(myFile);

            transf.transform(source, file);
            //InetAddress host = InetAddress.getLocalHost();
            // System.out.println(host.toString() + "\n");
            while (true) {
                Echoer echoer = new Echoer(serverSocket.accept());
                System.out.println("New client connected");
                echoer.start();

            }

        } catch (IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }
}
