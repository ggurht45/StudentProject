package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameStore {
	public static final int MAX_FRAMES = 10;
	private static final String tests_file = "AllTest.txt";
	static int numOfFrames = 0;
	static FrameWithScore[] arrayOfFrames = new FrameWithScore[MAX_FRAMES];

	public static void staticStart() {
		numOfFrames = 0;
	}

	public static void pass(FrameWithScore frame) {
		if (numOfFrames < MAX_FRAMES) {
			arrayOfFrames[numOfFrames] = frame;
			numOfFrames++;
		} else {
			double min = 1;
			int index = 0;
			for (int i = 0; i < MAX_FRAMES; i++)
				if (arrayOfFrames[i].getScore() < min) {
					min = arrayOfFrames[i].getScore();
					index = i;
				}
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

		for (int i=0;i<numOfFrames;i++)
		{
			String fileName=i+".frame";
	    	byte[] serializedFrame = arrayOfFrames[i].getFrame().serialize();
	    	Files.write(Paths.get(fileName), serializedFrame);
	    	PrintWriter printer = new PrintWriter(tests_file);
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
			fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.",	ex);
			return null;
		}
	}

	private static final Logger fLogger = Logger.getLogger(FrameStore.class.getPackage().getName());
}
