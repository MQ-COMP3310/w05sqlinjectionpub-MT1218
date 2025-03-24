package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } 
        catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO,"Wordle created and connected.");
        } 
        else {
            System.out.println("An error occured. Sorry!");
            logger.log(Level.WARNING,"Not able to connect to wordle.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Wordle structures in place.");
        } 
        else {
            System.out.println("An error occured. Sorry!");
            logger.log(Level.WARNING,"Not able to launch wordle.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        System.out.println("Loading game...");

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.INFO,"The following line has loaded from data.txt: " + line);
                if(line.length() == 4 && line.matches("[a-z]+")){
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.log(Level.INFO,"The following line has added to the database: " + line);
                }
                else{
                    logger.log(Level.SEVERE,"The following line has not been added to the database as it is invalid: " + line);
                }
                i++;
            }
        } 
        catch (IOException e) {
            System.out.println("An error has occured while trying to load data.txt. Sorry!");
            logger.log(Level.WARNING,"The following exception occured when trying to load words from data.txt: " + e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess + "'.");

                if(guess.length() == 4 && guess.matches("[a-z]+")){
                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                    }
                    else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }
                else{
                    System.out.println("Invalid input. Valid inputs are only four letter words consisting of alphabets.\n");
                    logger.log(Level.WARNING,"An invalid guess was inputted: " + guess);
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } 
        catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("An error has occured. Sorry!");
            logger.log(Level.WARNING,"The following exception occured when getting an input from the user: " + e.getMessage());
        }

    }
}