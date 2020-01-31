/**
 * 
 */
package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Igor
 *
 */
public class Logger {

	public String line = "";
	public String path = "";
	
	
	
	public Logger() {
		super();
		this.line = "";
		this.path = "C:\\Users\\Igor\\Desktop";
	}
	
	public Logger(String path) {
		this.path = path;
	}

	public static void log(String data) {
        File file = new File("log.txt");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	
	public void preciseLog(String data) {
		File file = new File(this.path + File.separator +"log.txt");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void append(String data) {
		line +=data + "\n";
	}
	
	public void logAll() {
		preciseLog(line);
		log(line);
		line = "";
	}
	
}
