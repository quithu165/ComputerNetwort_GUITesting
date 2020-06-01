package com.mycompany.testmultithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
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

                    String username = echoString.substring(1);
                    findPerson(username,output);
                    break;
                case '4':
                   
                    addFriend(echoString, output);
                    break;
                case '5':
                    System.out.println("Test");
                  
                    addFriendtoFile("src/data/quithu165.xml","src/data/thuyduong123.xml");
    
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
//REGISTER HERE%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    private static void register(String echoString, PrintWriter output)
            throws ParserConfigurationException, TransformerException, SAXException, IOException {
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
                    newElement(doc, "friends", "quithu165")
            );
            user.appendChild(
                    newElement(doc, "status", "1")
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
    
//END OF REGISTER%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//LOGIN HERE%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    private static void authenticate(String echoString, PrintWriter output)
            throws SAXException, ParserConfigurationException, IOException, TransformerException {
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

        addNewUserIP(ip, name);

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
            String message = node2.getTextContent();
            // login successfully, send back its listen port
            if (passFile.equals(pass)) {

                System.out.println("Login!!!");
                updateStatus(name, "1");
                message = message + "%" + numberOfFriend + "%";
                Node friend;
                String friendName;
                String friendIP;
                String friendPort;                
                String status;
                for (int i = 0; i < numberOfFriend; i++) {
                    friend = elem.getElementsByTagName("friends").item(i);
                    friendName = friend.getTextContent();
                    status = getFriendStatus(friendName);
                    friendIP = getFriendIP(friendName);
                    friendPort = getFriendPort(friendName);
                    message = message+ status + friendName + "-" + friendIP + "-" + friendPort + "%";
                }
                output.println(message);

            } else {

                System.out.println("Rejected!!!");

                output.println("0");
            }

        }

    }
    
    
    public static void addNewUserIP(String ip, String name) throws SAXException, ParserConfigurationException, IOException, TransformerException {
        String fileName = "src/data/ip.xml";
        File userFile = new File(fileName);
        boolean flag = true;
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(userFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("ip");
        //Get first element
        Node nNode = nList.item(0);
        Element elem = (Element) nNode;
        NodeList nE = elem.getElementsByTagName("user");
        int length = nE.getLength();
        for (int i = 0; i < length; i++){
            Node n1 = nE.item(i);
            Element x = (Element) n1;
            Node n2 = x.getElementsByTagName("username").item(0);
            String nameField = n2.getTextContent();
            System.out.println(nameField);
            if (nameField.equals(name)){
                System.out.println("Occur");
                flag = false;
            }
        }
        
        if (!flag) {
        } else {
            Element user = doc.createElement("user");

            user.setAttribute("name", name);
            user.appendChild(
                    newElement(doc, "ip", ip)
            );
            user.appendChild(
                    newElement(doc, "username", name)
            );
//        System.out.println("Step 3");

            elem.appendChild(user);

            TransformerFactory transformerFactory
                    = TransformerFactory.newInstance();
            Transformer transf = transformerFactory.newTransformer();

            transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transf.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);

            StreamResult file = new StreamResult(userFile);

            transf.transform(source, file);
        }
    }
    
    public static String getFriendStatus(String username) throws ParserConfigurationException, SAXException, IOException{
        String result = null;
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
        Node n1 = elem.getElementsByTagName("port").item(0);
        result = n1.getTextContent();
        return result;
    }    
        
    public static String getFriendIP(String username) throws SAXException, ParserConfigurationException, IOException{
        String result = null;
        String fileName = "src/data/ip.xml";
        File userFile = new File(fileName);
        boolean flag = true;
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(userFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("ip");
        //Get first element
        Node nNode = nList.item(0);
        Element elem = (Element) nNode;
        NodeList nE = elem.getElementsByTagName("user");
        int length = nE.getLength();
        for (int i = 0; i < length; i++){
            Node n1 = nE.item(i);
            Element x = (Element) n1;
            Node n2 = x.getElementsByTagName("username").item(0);
            String nameField = n2.getTextContent();
            System.out.println(nameField);
            if (nameField.equals(username)){
                Node n3 = x.getElementsByTagName("ip").item(0);
                result = n3.getTextContent();
                break;
            }
        }
        return result;
        
    }
    
    public static String getFriendPort(String username) throws SAXException, IOException, ParserConfigurationException{
        String result = null;
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
        Node n1 = elem.getElementsByTagName("port").item(0);
        result = n1.getTextContent();
        return result;
        
    }
    //END OF LOGIN%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //BEGIN OF FINDPERSON%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public static void findPerson(String username, PrintWriter output) throws SAXException, IOException, ParserConfigurationException{
        String fileName = "src/data/" + username + ".xml";
        File personFile = new File(fileName);
        if (!personFile.isFile() ){
            output.print("0");
        }
        else {
            String message = null;
            String status;
            String port;
            String ip;
            status = getFriendStatus(username);
            port = getFriendPort(username);
            ip = getFriendIP(username);
            
            if (status.equals("0")) {
                output.println("0");
            }
            else 
                message = ip + "-" + port;
                output.println(message);
            
        }
    }
    //END OF FINDPERSON%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    //BEGIN OF ADDFRIEND%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public static void addFriend(String message, PrintWriter output){
        String host;
        String friend;

        int curPos = 1;

        while (message.charAt(curPos) != '-') {
            curPos++;
        }
        if ((curPos - 1) == 1) {
            host = String.valueOf(message.charAt(curPos - 1));
        } else {
            host = message.substring(1, curPos);
        }
        curPos++;
        friend = message;
        
        
    }
    public static void addFriendtoFile(String filename, String friendname) throws SAXException, IOException, ParserConfigurationException{
        File hostFile = new File(filename);
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(hostFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("user");
        Node nNode = nList.item(0);
        Element elem = (Element) nNode;
        System.out.println("test 1");
        elem.appendChild(
                    newElement(doc, "friend", friendname)
            );
        doc.appendChild(elem);
        System.out.println("test 2");
        
            
    }
    //END OF ADDFRIEND%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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

