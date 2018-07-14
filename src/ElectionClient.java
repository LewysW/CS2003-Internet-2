import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.security.Security;
import java.util.HashMap;

import com.sun.security.sasl.Provider;
import org.jfree.ui.RefineryUtilities;

/**
 * Class which is run on the client side to allow them to connect to the voting server.
 */
public class ElectionClient {

    //Default values provided if no arguments are provided during execution.
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String DEFAULT_PORT = "1362";
    private static final String DEFAULT_TRUSTSTORE = System.getProperty("user.dir") + "/data/myTrustStore.jts";
    private static final String DEFAULT_TRUSTORE_PASSWORD = "cs2003student";

    /**
     * Main method to begin execution of voting program on the client side.
     * @param args - command line arguments (if any) specifying the IP address, port number, trust store file and trust
     *             store password to use.
     */
    public static void main(String[] args) {
        String ip;
        String port;
        String trustStore;
        String trustStorePassword;

        //If the correct number of command line arguments are provided, use those values.
        if (args.length == 4) {
            ip = args[0];
            port = args[1];
            trustStore = System.getProperty("user.dir") + "/data/" + args[2];
            trustStorePassword = args[3];

        //Otherwise use the default values.
        } else {
            ip = DEFAULT_IP;
            port = DEFAULT_PORT;
            trustStore = DEFAULT_TRUSTSTORE;
            trustStorePassword = DEFAULT_TRUSTORE_PASSWORD;
        }

        try {
            /*
             * SSL was implemented to provide a secure transfer of data over sockets between the client and server.
             *
             * This video tutorial was used as a starting point for implementing SSL sockets:
             * https://www.youtube.com/watch?v=VSi3KFlVAbE&t=1s
             */

            //Adds a new security provider
            Security.addProvider(new Provider());
            //Sets the value of the trust store file
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            //Sets the value of the trust store password
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

            //Creates a new SSLSocketFactory from which an SSLSocket is created with a specified ip address and port no.
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(ip, Integer.parseInt(port));

            //boolean to store whether voting is still open.
            boolean open = checkIfVotingOpen(sslSocket);

            //If the voting is still open.
            if (open) {

                //Create a new voter object.
                Voter voter = new Voter();

                //Check if the voter is a guest.
                if (voter.isGuest()) {
                    //And run as guest.
                    runAsGuest(sslSocket, open);
                } else {
                    //Otherwise run as a student
                    runAsStudent(sslSocket, voter);
                }

                //Otherwise if voting has closed, just run as a guest.
            } else {
                runAsGuest(sslSocket, open);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Carries out interaction with the server if the client is a student.
     * @param sslSocket to securely connect to the server.
     * @param voter object to allow the user to cast their vote.
     * @throws IOException if an error involving server IO occurs.
     */
    private static void runAsStudent(SSLSocket sslSocket, Voter voter) throws IOException {
        //Sets up IO streams
        DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());


        //Send hashed credentials to server
        outputStream.writeUTF(voter.getHash());
        outputStream.flush();

        //Receive message from server specifying if client is registered.
        String message = inputStream.readUTF();
        System.out.println("Server said: " + message);

        //If client is registered.
        if (!message.contains("Not registered!")) {
            //Receive ballot paper String with candidate names
            message = inputStream.readUTF();

            //Cast vote and write to server.
            outputStream.writeUTF(Integer.toString(voter.castVote(Integer.parseInt(message))));
            outputStream.flush();

            //Otherwise if not registered.
        } else if (message.contains("Not registered!")) {
            //Print message to client and terminate program.
            System.out.println("Not registered. Closing program.");
            System.exit(0);
        }

        outputStream.flush();

    }

    /**
     * Carries out interaction with the server if the client is a guest.
     * @param sslSocket to securely connect to the server.
     * @param open - boolean storing whether the voting is still open.
     * @throws IOException if an error occurs during IO with server.
     * @throws ClassNotFoundException if an error occurs in the retrieve results method.
     */
    private static void runAsGuest(SSLSocket sslSocket, boolean open) throws IOException, ClassNotFoundException {
        //Create IO streams
        DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());

        //Writes to server to indicate that the user is a guest.
        outputStream.writeUTF("guest");

        //If voting is closed, retrieve the results.
        if (!open) {
            retrieveResults(sslSocket);

        //Otherwise if voting is open
        } else {
            //Retrieve and print number of votes cast and time left to vote.
            String received = inputStream.readUTF();
            System.out.println(received);
            System.exit(0);
        }
    }

    /**
     * Checks if the election is still ongoing.
     * @param sslSocket to create secure connection with the server.
     * @return a boolean storing whether or not the election is ongoing.
     * @throws IOException if an error occurs during IO with server.
     */
    private static boolean checkIfVotingOpen(SSLSocket sslSocket) throws IOException {
        //Sets up input stream.
        DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());

        //Returns a boolean parsed from the server response.
        return Boolean.parseBoolean(inputStream.readUTF());
    }

    /**
     * Retrieves the final results of the election from the server as a hash map of names and votes.
     * @param sslSocket to create secure connection with the server.
     * @throws IOException if an error occurs during IO with server.
     * @throws ClassNotFoundException if object returned by objectInputStream is not of the correct type.
     */
    private static void retrieveResults(SSLSocket sslSocket) throws IOException, ClassNotFoundException {
        //Creates new object input stream to retrieve objects from server.
        ObjectInputStream objectInputStream = new ObjectInputStream(sslSocket.getInputStream());
        //Stores object sent by server, cast to type HashMap.
        HashMap<String, Integer> results = (HashMap<String, Integer>) objectInputStream.readObject();
        objectInputStream.close();

        //Calls method to display results.
        displayResults(results);
    }

    /**
     * Displays the results of the election as a pie chart using JFreeChart.
     * @param results hash map containing names and votes, used to supply data to pie chart.
     */
    private static void displayResults(HashMap<String, Integer> results) {
        //Creates a new pie chart demo with the header "Election Results"
        PieChart_AWT demo = new PieChart_AWT( "Election Results" , results);
        //Sets the width and height of the demo.
        demo.setSize( 560 , 367 );
        //Centres the demo window.
        RefineryUtilities.centerFrameOnScreen( demo );
        //Sets the demo to visible.
        demo.setVisible( true );
        //Sets default close operation to do nothing.
        demo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
}
