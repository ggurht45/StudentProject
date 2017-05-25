package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.leapmotion.leap.Hand;


import java.util.logging.Level;
import java.util.logging.Logger;



public class TargetHandStore {
	public static final int  MAX_FRAMES = 10; 
	private static final String hands_file = "TargetHands.txt";	
	
	
	public static void Save(Hand h)
	{
	   	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	String fileName=sdf.format(cal.getTime() )+".txt";
    	

		try (
	      OutputStream file = new FileOutputStream(fileName);
	      OutputStream buffer = new BufferedOutputStream(file);
	      ObjectOutput output = new ObjectOutputStream(buffer);
	     ){
	      output.writeObject(h);
		      try {
			        File outFile = new  File(hands_file);
			        if (!outFile.exists())										
			        	outFile.createNewFile();
			        Writer output2;
			        output2 = new BufferedWriter(new FileWriter(hands_file, true));
			        output2.append(fileName+"\r\n");
			        output2.close();
		      }catch (IOException e) {
			        e.printStackTrace();
		      }
	    }  
	    catch(IOException ex){
	      fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
	    }
	}
	public static Hand readFromFile(String s) throws FileNotFoundException, IOException {
		try(
	      InputStream file = new FileInputStream(s);
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);
	    ){
	 	  return  (Hand)input.readObject();
	    }
	    catch(ClassNotFoundException ex){
	      fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
	      return null;
	    }
	}
	
	public static ArrayList<Hand> getAllHands() throws Exception{
		
		File inFile = new  File(hands_file);
		if (!inFile.exists()) 
			throw new Exception("In file not found :"+hands_file);		// No input file
		
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
	
	private static final Logger fLogger = Logger.getLogger(TargetHandStore.class.getPackage().getName());
}
