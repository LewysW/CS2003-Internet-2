import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Cryptography class which generates a SHA-512 hash given a String input.
 */
public class Crypto {
    /**
     * Method to hash a String into a hex SHA-512 String
     * @param input string to be hashed
     * @return hashed of string
     * Developed using tutorial code:
     * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     */
    public String hash(String input) {
        String hash = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //Add password bytes to digest
            md.update(input.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            hash = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }

    /**
     * Method to hash a given array list using the hash method.
     * @param inputs array list to be hashed.
     * @return hash array list of strings.
     */
    public ArrayList<String> hashList(ArrayList<String> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, hash(inputs.get(i)));
        }

        return inputs;
    }



}
