import org.apache.commons.lang3.time.StopWatch;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents a thread running the processes for a particular user.
 */
public class ClientThread extends Thread {
    //Duration of election in milliseconds.
    private static final long DURATION = 10 * 60 * 1000;
    //Stopwatch to keep track of how long the election has been going.
    private StopWatch stopWatch;
    //SSLSocket which provides a secure connection between client and server.
    private SSLSocket sslSocket;
    //Array list storing user details converted to a SHA512 hash.
    private ArrayList<String> electoralRegister;
    //Ballot with which to vote.
    private Ballot ballot;
    //Current votes for candidates.
    private ArrayList<Vote> votes;

    /**
     * Constructor for ClientThread
     * @param sslSocket for secure connection to server.
     * @param electoralRegister containing hashed credentials to validate voters.
     * @param ballot used to vote.
     * @param votes for candidates so far.
     * @param stopWatch to record current time elapsed in election.
     */
    public ClientThread(SSLSocket sslSocket, ArrayList<String> electoralRegister, Ballot ballot, ArrayList<Vote> votes, StopWatch stopWatch) {
        super();
        this.sslSocket = sslSocket;
        this.electoralRegister = electoralRegister;
        this.ballot = ballot;
        this.votes = votes;
        this.stopWatch = stopWatch;
    }

    /**
     * Run method which is automatically called when the thread is made.
     * Handles the main sequence of execution of the voting system.
     */
    public void run() {
        try {
            //Creates input and output streams over sslSocket to speak to client
            DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());

            //Outputs whether the election has ended.
            //Election is over if the electoral register is empty or if time elapsed is greater than election DURATION
            outputStream.writeUTF(Boolean.toString(!electoralRegister.isEmpty() && (stopWatch.getTime() < DURATION)));

            //If election is not over.
            if (!electoralRegister.isEmpty() && (stopWatch.getTime() < DURATION)) {
                //Create new ballot for client.
                ballot = new Ballot();

                //Receive hash code created by hashing the user's name, matriculation number and date of birth on the client
                //side. May also receive "guest" indicating a guest log in.
                String recieved = inputStream.readUTF();

                //If user is guest
                if (recieved.equals("guest")) {
                    //Call method to serve guest appropriate data.
                    serveGuest(outputStream, votes, stopWatch);
                } else {
                    //Otherwise call method to serve student the appropriate response.
                    votes = serveStudent(votes, electoralRegister, ballot, recieved, outputStream, inputStream);
                }

                //Close I/O streams as well as socket.
                inputStream.close();
                outputStream.close();
                sslSocket.close();

            } else {
                //Otherwise if election is over, serve the results to the user.
                serveResults(sslSocket, new ElectoralSystem(ballot, votes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends the number of votes cast so far as well as the time left in the election to a guest user.
     * @param outputStream used to write data to guest.
     * @param votes cast so far in the election
     * @param stopWatch recording the time elapsed in the election.
     * @throws IOException if an error is encountered while writing data to the client.
     *
     * Found out how to use TimeUnit for displaying formatted times from StackOverflow:
     * https://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java
     */
    private static void serveGuest(DataOutputStream outputStream, ArrayList<Vote> votes, StopWatch stopWatch) throws IOException {
        //Stores time left in election using stopWatch and election duration.
        long timeLeft = (stopWatch.getStartTime() + DURATION) - (stopWatch.getStartTime() + stopWatch.getTime());

        //Writes no. votes cast so far to user as well as time left to vote in hours, minutes and seconds formatted using
        //time unit.
        outputStream.writeUTF(votes.size() + " vote(s) cast so far.\n"
                + "Time left to vote: " + String.format("%d hrs, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(timeLeft),
                TimeUnit.MILLISECONDS.toMinutes(timeLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft)),
                TimeUnit.MILLISECONDS.toSeconds(timeLeft) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft)))
        );
    }

    /**
     * Once the election is over, writes the final results to the user.
     * @param sslSocket used to securely send results to client.
     * @param electoralSystem object to get the results of the vote.
     * @throws IOException if an error occurs while writing an object to the user.
     */
    private static void serveResults(SSLSocket sslSocket, ElectoralSystem electoralSystem) throws IOException {
        //Hash map to store the results of the election (with name as key and number of votes as value).
        HashMap<String, Integer> results = electoralSystem.getVoteResults();

        //ObjectOutput stream to write the hash map to the user over the SSLSocket.
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(sslSocket.getOutputStream());
        objectOutputStream.writeObject(results);
        objectOutputStream.close();
    }

    /**
     * Writes data to the client if they are a student who is registered, provides them with a ballot to vote and
     * takes the vote.
     * @param votes array list to store the matriculation number of the candidate voted for.
     * @param electoralRegister containing a list of hashed credentials of registered students to compare with a hash
     *                          sent by the client.
     * @param ballot to send the user the list of candidates in a random order.
     * @param recieved message from user.
     * @param outputStream to write to client.
     * @param inputStream to reader from client.
     * @return updated array list of votes.
     * @throws IOException if read/write to user fails.
     */
    private static ArrayList<Vote> serveStudent(ArrayList<Vote> votes, ArrayList<String> electoralRegister, Ballot ballot,
                                                String recieved, DataOutputStream outputStream, DataInputStream inputStream)
            throws IOException {

        //If registered,
        if (isRegistered(electoralRegister, recieved)) {
            //Send registered message and ballot paper.
            outputStream.writeUTF("You are registered. \n\n" + ballot.generateBallotPaper());

            //Sends number of candidates for use in input validation.
            outputStream.writeUTF(Integer.toString(ballot.getNumCandidates()));
            outputStream.flush();

            //Gets vote as a number from 1 to the number of candidates.
            recieved = inputStream.readUTF();

            //Store current number representing a particular vote.
            int voteNum = Integer.parseInt(recieved);

            //Adds the matriculation number of the candidate associated with that index in the current ballot
            //to the list of votes.
            votes.add(new Vote(ballot.getCandidates().get(voteNum - 1).getMatricNum()));


        } else {
            //Otherwise if not registered. Sends a message to the client to tell them that they're not registered.
            //i.e. their hash does not match any stored hashes.
            outputStream.writeUTF("Not registered!");
            outputStream.flush();
        }

        return votes;
    }

    /**
     * Method to check whether the client is a registered student eligible to vote.
     * @param electoralRegister containing a list of hashed user credentials (name, matric number, date of birth).
     * @param received message from user containing the hash of the details they just entered.
     * @return true if registered and false if not.
     */
    private static boolean isRegistered(ArrayList<String> electoralRegister, String received) {
        //If the list of hashes contains the client input hashed
        if (electoralRegister.contains(received)) {
            //"Cross them off" from the register. Indicating that another student has voted.
            electoralRegister.remove(received);
            return true;
        } else {
            return false;
        }

    }
}
