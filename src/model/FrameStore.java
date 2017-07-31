package model;

import view.LeapUIApp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameStore {
    public static final int MAX_FRAMES = 10;
    static int numOfFrames = 0;
    static FrameWithScore[] arrayOfFrames = new FrameWithScore[MAX_FRAMES];

    public static void staticStart() {
        numOfFrames = 0;
    }

    //what does this piece of code do?
    //its given a frame. it seems to store up to around 10 frames in an array called arrayOfFrames
    //
    public static void pass(FrameWithScore frame) {
        if (numOfFrames < MAX_FRAMES) {
            arrayOfFrames[numOfFrames] = frame;
            numOfFrames++;
        } else {
            double min = 1; //set to the maximum possible score. so it can only go down from here.
            int index = 0;
            //this goes through entire array and returns the index of the frame with the smallest score
            //there is a class called FrameWithScore.
            for (int i = 0; i < MAX_FRAMES; i++) {
                if (arrayOfFrames[i].getScore() < min) {
                    min = arrayOfFrames[i].getScore();
                    index = i;
                }
            }
            //then replaces the smallest scoring frame with the new passed in frame.
            //so even if the passed in frame has a smaller score then the current min, it still replaces it
            //lets say current min in array is .4, argument passed in is .3. it will replace the .4 one.
            //interesting. note, its passed in as a "frameWithScore" object
            arrayOfFrames[index] = frame;
        }
    }

    public static void staticEnd() throws IOException {
        assert (numOfFrames >= MAX_FRAMES);
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        String filePath = sdf.format(cal.getTime());
        File file = new File(filePath);
        if (!file.exists())
            file.mkdirs();

        for (int i = 0; i < numOfFrames; i++) {
            String fileName = i + ".frame";
            byte[] serializedFrame = arrayOfFrames[i].getFrame().serialize();
            Files.write(Paths.get(fileName), serializedFrame);
            PrintWriter printer = new PrintWriter(LeapUIApp.TestsFile);
            printer.println(fileName);
            printer.close();
        }

    }

    public static FrameWithScore[] readFromFile(String s)
            throws FileNotFoundException, IOException {
        try (InputStream file = new FileInputStream(s);
             InputStream buffer = new BufferedInputStream(file);
             ObjectInput input = new ObjectInputStream(buffer);) {
            FrameWithScore[] array = (FrameWithScore[]) input.readObject();
            return array;
        } catch (ClassNotFoundException ex) {
            fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
            return null;
        }
    }

    private static final Logger fLogger = Logger.getLogger(FrameStore.class.getPackage().getName());
}
