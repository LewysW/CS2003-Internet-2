import com.sun.security.sasl.Provider;

import org.apache.commons.lang3.time.StopWatch;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.file.Files;
import java.security.Security;
import java.util.ArrayList;

/**
 * Class to represent the server and allow clients to connect.
 */
public class SSLMultiThreadedServer {
    //StopWatch to track time elapsed.
    private static StopWatch stopWatch = new StopWatch();
    //Votes cast in election.
    private static ArrayList<Vote> votes = new ArrayList<>();

    //Default values if no arguments are provided during execution.
    private static final String DEFAULT_PORT = "1362";
    private static final String DEFAULT_KEYSTORE = System.getProperty("user.dir") + "/data/myKeyStore.jks";
    private static final String DEFAULT_KEYSTORE_PASSWORD = "cs2003student";


    /**
     * Main method to handle execution of server side processes.
     * @param args - command line arguments (if any) specifying the port number, key store file and key
     *             store password to use.
     */
    public static void main(String[] args) {
        String port;
        String keyStore;
        String keyStorePassword;

        //If the correct number of command line arguments are provided, use those values.
        if (args.length == 3) {
            port = args[0];
            keyStore = System.getProperty("user.dir") + "/data/" + args[1];
            keyStorePassword = args[2];

            //Otherwise use the default values.
        } else {
            port = DEFAULT_PORT;
            keyStore = DEFAULT_KEYSTORE;
            keyStorePassword = DEFAULT_KEYSTORE_PASSWORD;
        }

        //Instantiate a new Crypto object for hashing, and FileIO object for interacting with files.
        Crypto crypto = new Crypto();
        FileIO fileIO = new FileIO(System.getProperty("user.dir"));

        //Start stop watch.
        stopWatch.start();

        //Stores a list of hashes produced from the loaded in electoral_register file which lists registered student's
        //details.
        ArrayList<String> electoralRegister = crypto.hashList(
                fileIO.storeAsArrayList(
                        fileIO.readFile("electoral_register"), "\n"));


        /**
         * Line to call deleteFile in FileIO. The purpose of this is to delete the registered voter details as soon
         * as the program has produced a list of hashes from them. Commented out to avoid having to recreate the file
         * every time the program is tested.
         */
        //fileIO.deleteFile("electoral_register");


        /**
         * SSL was implemented to provide a secure transfer of data over sockets between the client and server.
         *
         * This video tutorial was used as a starting point for implementing SSL sockets:
         * https://www.youtube.com/watch?v=VSi3KFlVAbE&t=1s
         */
        Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);

        try {
            //Creates a new SSLSocketFactory from which an SSLServerSocket is created with a specified port no.
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Integer.parseInt(port));

            //Calls main server loop.
            serverLoop(sslServerSocket, electoralRegister, votes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to act as a server loop to wait for and accept incoming client requests. Creates a new thread for each
     * client.
     * @param sslServerSocket used to run the server.
     * @param electoralRegister used to store hashes of registered voter details.
     * @param votes cast in election.
     * @throws IOException if an error involving server/client IO occurs.
     */
    private static void serverLoop(SSLServerSocket sslServerSocket, ArrayList<String> electoralRegister, ArrayList<Vote> votes) throws IOException {
        //Loops while server is open.
        while (!sslServerSocket.isClosed()) {
            try {
                //When a client connects a new thread and ClientThread are made to serve the client.
                new Thread(new ClientThread((SSLSocket) sslServerSocket.accept(), electoralRegister, new Ballot(), votes, stopWatch)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Closes the SSLServerSocket
        sslServerSocket.close();
    }

}
