import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class to handle loading in files and storing data as an array list.
 */
public class FileIO {
    private String fileDirectory;

    /**
     * Constructor to initialise the file directory to work in.
     * @param fileDirectory to access files.
     */
    public FileIO(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    /**
     * Reads the contents of a file into a String.
     * @param fileName of file to be read into String.
     * @return contents of file as a String.
     */
    public String readFile(String fileName) {
        String content = "";

        try {
            //Reads a particular data file in the /data/ folder until the end of its content.
            content = new Scanner(new File(fileDirectory + "/data/" + fileName)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * Stores a given String loaded in from a file into an array list divided using a given separator.
     * @param content to be split and stored in array list.
     * @param separator to be used to split content.
     * @return new array list with content split between elements.
     */
    public ArrayList<String> storeAsArrayList(String content, String separator) {
        return new ArrayList<>(Arrays.asList(content.split(separator)));
    }

    /**
     * Method to delete a given file.
     * @param fileName of file to be deleted.
     */
    public void deleteFile(String fileName) {
        File file = new File(fileDirectory + "/data/" + fileName);
        file.delete();
    }



}

