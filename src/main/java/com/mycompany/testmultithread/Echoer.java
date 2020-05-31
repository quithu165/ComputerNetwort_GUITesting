package com.mycompany.testmultithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Echoer extends Thread {

    private final Socket socket;
    public static int listenPort = 5000;
    public static int prt = 1;

    public Echoer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output
                    = new PrintWriter(socket.getOutputStream(), true);

            String echoString = input.readLine();
            System.out.println("Received client input: " + echoString);

            char c = echoString.charAt(0);

//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            //convert ObjectInputStream object to String
//            String echoString = (String) ois.readObject();
//            System.out.println("Message Received: " + echoString);
            switch (c) {
                case '1': // request to register

                    register(echoString, output);

                    break;
                case '2': // request login

                    authenticate(echoString, output);

                    break;
                case '3': // request to send message

                    String receiver = echoString.substring(1);
                    String fileName = "src/data/" + receiver + ".xml";
                    File userFile = new File(fileName);

                    // user exists
                    if (userFile.isFile()) {

                        DocumentBuilderFactory factory
                                = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = factory.newDocumentBuilder();
                        Document doc = dBuilder.parse(userFile);

                        doc.getDocumentElement().normalize();

                        NodeList nList = doc.getElementsByTagName("user");
                        Node nNode = nList.item(0);

                        Element elem = (Element) nNode;
                        Node node = elem.getElementsByTagName("port").item(0);
                        String port = node.getTextContent();

                        System.out.printf("Port: %s%n", port);

                        output.println(port);

                        // user not exist
                    } else {
                        System.out.printf("User not found");

                        output.println("-1");
                    }

                    break;
                case '4':
                    String name;
                    String friend;
                    int curPos = 0;

                    while (echoString.charAt(curPos) != '-') {
                        curPos++;
                    }
                    name = echoString.substring(1, curPos);

                    friend = echoString.substring(curPos + 1);

                    if (name.equals(friend)) {
                        output.println("-1");
                    } else {

                        String fName = "src/data/" + friend + ".xml";
                        File fFile = new File(fName);

                        // friend exists
                        if (fFile.isFile()) {

                            DocumentBuilderFactory factory
                                    = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = factory.newDocumentBuilder();
                            Document doc = dBuilder.parse(fFile);

                            doc.getDocumentElement().normalize();

                            NodeList nList = doc.getElementsByTagName("user");
                            Node nNode = nList.item(0);

                            Element elem = (Element) nNode;
                            Node node = elem.getElementsByTagName("friends").item(0);

                            // System.out.printf("Port: %s%n", port);
                            output.println("1");

                            // friend not exist
                        } else {
                            System.out.printf("Friend not found");

                            output.println("-1");
                        }
                    }

                    break;
                case '5':
                    System.out.println("Log out");
                    String username = echoString.substring(1, echoString.length() - 1);
                    updateStatus(username, "0");
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            System.out.println("Oops: " + e.getMessage());

        } catch (TransformerException | ParserConfigurationException
                | SAXException ex) {
            Logger.getLogger(Echoer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Server close socket: " + e.getMessage());
            }
        }

    }

    private static void register(String echoString, PrintWriter output)
            throws ParserConfigurationException, TransformerException {
        String name;
        String pass;

        int curPos = 1;

        while (echoString.charAt(curPos) != '-') {
            curPos++;
        }
        if ((curPos - 1) == 1) {
            name = String.valueOf(echoString.charAt(curPos - 1));
        } else {
            name = echoString.substring(1, curPos);
        }
        curPos++;
        pass = echoString.substring(curPos);
        String fileName = "src/data/" + name + ".xml";
        File myFile = new File(fileName);
        String port = "1";
        if (myFile.isFile()) {
            port = "0";
        }
        //Find user here        
        if (port.equals("0")) {
            output.println("0");
        } else {
            output.println("1"); // return ID number
            port = (int) (prt + listenPort) + "";
            prt++;
            // write to xml file 
            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElementNS("Information", "info");
            doc.appendChild(root);

            //int port = (Integer.parseInt(ID) % 1000) + 3000;
            Element user = doc.createElement("user");
            user.setAttribute("id", port);
            user.appendChild(
                    newElement(doc, "username", name)
            );
            user.appendChild(
                    newElement(doc, "pass", pass)
            );
            user.appendChild(
                    newElement(doc, "port", port)
            );
            user.appendChild(
                    newElement(doc, "friends", null)
            );

            root.appendChild(user);

            TransformerFactory transformerFactory
                    = TransformerFactory.newInstance();
            Transformer transf = transformerFactory.newTransformer();

            transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transf.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);

            StreamResult file = new StreamResult(myFile);

            transf.transform(source, file);

        }
    }

    private static void authenticate(String echoString, PrintWriter output)
            throws SAXException, ParserConfigurationException, IOException {
        String name;
        String pass;
        String ip;
        int curPos = 1;

        while (echoString.charAt(curPos) != '-') {
            curPos++;
        }
        if ((curPos - 1) == 1) {
            name = String.valueOf(echoString.charAt(curPos - 1));
        } else {
            name = echoString.substring(1, curPos);
        }
        curPos++;
        int tracePos = curPos;
        while (echoString.charAt(curPos) != '-') {
            curPos++;
        }
        
        if ((curPos - 1) == 1) {
            pass = String.valueOf(echoString.charAt(curPos - 1));
        } else {
            pass = echoString.substring(tracePos, curPos);
        }
        curPos++;
        ip = echoString.substring(curPos);
//        System.out.println(name);
//        System.out.println(pass);
//        System.out.println(ip);
        String fileName = "src/data/" + name + ".xml";
        File userFile = new File(fileName);

        if (!userFile.isFile()) {
            System.out.println("Rejected!!!");

            output.println("0");
        } else {

            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            Document doc = dBuilder.parse(userFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("user");
            Node nNode = nList.item(0);
            Element elem = (Element) nNode;
            // get password
            Node node1 = elem.getElementsByTagName("pass").item(0);
            String passFile = node1.getTextContent();
            // get listen port of user
            Node node2 = elem.getElementsByTagName("port").item(0);

            int numberOfFriend = elem.getElementsByTagName("friends").getLength();
            String port = node2.getTextContent();
            // login successfully, send back its listen port
            if (passFile.equals(pass)) {

                System.out.println("Login!!!");
                updateStatus(name, "1");
                port = port + "%" + numberOfFriend + "%";
                Node friend;
                String friendName;
                char status;
                for (int i = 0; i < numberOfFriend; i++) {
                    friend = elem.getElementsByTagName("friends").item(i);
                    friendName = friend.getTextContent();
                    status = friendName.charAt(0);
                    friendName = friendName.substring(1, friendName.length());
                    port = port + status+"%"+friendName + "-";
                }
                output.println(port);

            } else {

                System.out.println("Rejected!!!");

                output.println("0");
            }

        }

    }

    private static int getPort(String name) {

        int port = 0;
        String fileName = "src/data/" + name + ".xml";
        File newFile = new File(fileName);

        if (!newFile.isFile()) {
            File directory = new File("src/data/");
            int userCount = directory.list().length;

            port = userCount + 3000;
        }

        return port;
    }

    private static Node newElement(Document doc, String name, String value) {

        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));

        return node;
    }

    private static void updateStatus(String username, String status)
            throws SAXException, IOException, ParserConfigurationException {
        String fileName = "src/data/" + username + ".xml";
        File userFile = new File(fileName);
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(userFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("user");
        Node nNode = nList.item(0);
        Element elem = (Element) nNode;
        // get length
        int length = elem.getElementsByTagName("friend").getLength();
        Node friend;
        String friendName;
        for (int i = 0; i < length; i++) {
            friend = elem.getElementsByTagName("friend").item(i);
            friendName = friend.getTextContent();
            friendName = friendName.substring(1, friendName.length() - 1);
            updateFriendFile(friendName, status, username);
        }

    }

    private static void updateFriendFile(String username, String status, String userupdate)
            throws SAXException, IOException, ParserConfigurationException {
        String fileName = "src/data/" + username + ".xml";
        File userFile = new File(fileName);
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(userFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("user");
        Node nNode = nList.item(0);
        Element elem = (Element) nNode;
        // get length
        int length = elem.getElementsByTagName("friend").getLength();
        Node friend;
        String friendName;
        for (int i = 0; i < length; i++) {
            friend = elem.getElementsByTagName("friend").item(i);
            friendName = friend.getTextContent();
            friendName = friendName.substring(1, friendName.length() - 1);
            if (friendName.equals(userupdate)) {
                elem.getElementsByTagName("friend").item(i).setNodeValue(status + friendName);
            }
        }

    }

}
