package saves;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

public class SaveHandler {
	
	//the context
	Context myContext;
	
	//filename
	String filename;
	
	//stores the contents of the file
	public ArrayList<String> contents = new ArrayList<String>();
	
	//variables stored in the save file
	public boolean sound = true;
	public boolean other;
	//location
	//map visited or not
	//quest done
	//quest initiated
	//item found
	//items missed
	//conversations had
	//bosses killed
	//etc
	
	//constructor
	public SaveHandler(Context context, String name) {
		myContext = context;
		filename = name;
	}
	
	//writing to file
	public void write() {
		try {
	        FileOutputStream outputStream = myContext.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
	        String outputString;
	        if (sound == true)
	        	outputString = "1";
	        else
	        	outputString = "0";
			outputStream.write(outputString.getBytes());
	        outputStream.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//reading from file
	public void read() {
		FileInputStream inputStream = null;
		try {
	        inputStream = myContext.getApplicationContext().openFileInput(filename);
	        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
	        String line;
	        while ((line = r.readLine()) != null) {
	            contents.add(line);
	        }
	        r.close();
	        inputStream.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		for (int i = 0; i < contents.size(); i++)
			System.out.println(contents.get(i));
		interpret();
	}
	
	//breaking down the contents of the read file and assigning values to the variables each line represents
	//very long method
	private void interpret() {
		//first line is sound
		if (contents.size() > 0) { //first run - file doesn't exist and contents is empty
			if (contents.get(0).equals("1"))
				sound = true;
			else
				sound = false;
			if (contents.size() == 2)
				other = true;
		}
	}
}
