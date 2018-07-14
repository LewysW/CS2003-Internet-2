import java.util.Scanner;

/**
 * Class to represent a voter in the election.
 */
public class Voter {

    //Stores hash of user details.
    private String hash;
    //Stores whether voter is a guest.
    private boolean isGuest;

    /**
     * Constructor for Voter.
     * Prompts the client to enter whether they area student or guest.
     *
     * If the client is not a guest (so is a student) then they are prompted to enter their name, matriculation number,
     * and date of birth. All three values concatenated are then hashed.
     */
    public Voter() {
        String name;
        String matricNum;
        String date;
        Scanner reader = new Scanner(System.in);
        Crypto crypto = new Crypto();

        //Prompt user to enter if student or guest and stores this.
        System.out.println("Are you a student or guest? (s/g).");
        isGuest = checkIfGuest(reader);

        //If not a guest, prompts user to enter details.
        if (!isGuest) {
            System.out.println("Please enter your name (as it appears on your matriculation card).");
            name = reader.nextLine();
            System.out.println("Please enter your matriculation number.");
            matricNum = reader.nextLine();
            System.out.println("Please enter your date of birth (dd/mm/yyyy).");
            date = reader.nextLine();

            //Hashes concatenated details.
            this.hash = crypto.hash(name + "," + matricNum + "," + date);
        }
    }

    /**
     * Method to allow user to cast a vote.
     * @param numCandidates - number of candidates in election. Used to ensure that the user does not select the no.
     *                      of a candidate that does not exist.
     * @return the number entered by the user corresponding to a candidate on their ballot.
     */
    public int castVote(int numCandidates) {
        int vote;

        try {
            //Prompts user to vote.
            System.out.println();
            System.out.println("Vote for only one candidate by entering the number of the candidate of your choice:");
            System.out.println();

            //Creates Scanner and gets user input.
            Scanner reader = new Scanner(System.in);
            String input = reader.nextLine();
            vote = Integer.parseInt(input);

            //If vote out of bounds
            if (vote <= 0 || vote > numCandidates) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            //If exception thrown if vote invalid then error message is printed and method is returned.
            System.out.println("Invalid vote.");
            System.out.println("Please try again:");
            return castVote(numCandidates);
        }

        return vote;
    }

    /**
     * Method to validate the user's input when specifying if they are a guest or student and return if they are a guest.
     * @param reader - used to take in user input.
     * @return whether the user is a guest or not.
     */
    public boolean checkIfGuest(Scanner reader) {
        String input;

        do {
            //Take in user input.
            input = reader.nextLine();

            //If input is not "s" for student or "g" for guest
            if (! (input.equals("s") || input.equals("g"))) {
                //print error message to user.
                System.out.println("Entry invalid. Please enter s if you are a registered student" +
                        " or g if you are a guest.");
            }
            //Iterate while input is not "s" or "g".
        } while (!(input.equals("s") || input.equals("g")));

        //Return true for g or false for s.
        return input.equals("g");
    }

    /**
     * Getter for hashed details.
     * @return hash of user details.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Getter for isGuest.
     * @return true for guest or false for not guest.
     */
    public boolean isGuest() {
        return isGuest;
    }
}
