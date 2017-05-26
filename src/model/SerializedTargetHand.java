package model;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Controller;



public class SerializedTargetHand{
	public static final int  MAX_FRAMES = 10; 
	private static final String hands_file = "TargetHands.txt";
	
	
	public static void Save(Frame f) throws IOException
	{
	   	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    	String fileName="targets/" + sdf.format(cal.getTime() ) + ".hand";
    	byte[] serializedFrame = f.serialize();
    	Files.write(Paths.get(fileName), serializedFrame);
    	PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(hands_file, true)));
    	printer.println(fileName);
    	printer.close();
	}
	public static Hand readFromFile(String s) throws FileNotFoundException, IOException {
		Controller controller = new Controller(); //An instance must exist
		byte[] frameBytes = Files.readAllBytes(Paths.get(s));

		Frame reconstructedFrame = new Frame();
		reconstructedFrame.deserialize(frameBytes);
		
		return reconstructedFrame.hands().leftmost();
	}
	
	public static ArrayList<Hand> getAllHands() throws Exception {
		
		File inFile = new  File(hands_file);
		if (!inFile.exists()) 
			throw new Exception("In file not found :"+hands_file); // No input file
		
		BufferedReader br = new BufferedReader(new FileReader( inFile));
		
		ArrayList<Hand> hands=new ArrayList<Hand>();

		try {
		     String line = br.readLine();
		     while (line != null) {
		    	 	hands.add(readFromFile(line));
		            line = br.readLine();
		      }
		    } finally {
		        br.close();
		 }
		return hands;
		
	}
	
}