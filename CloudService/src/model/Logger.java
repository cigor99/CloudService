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

	private String line;
	private String path;
	private String fileName;
	
	
	
	public Logger() {
		super();
		this.line = "";
		this.path = "C:\\Users\\Igor\\Desktop\\Logs\\";
		this.fileName = "log.txt";
	}
	
	public Logger(String fileName) {
		this.fileName = fileName;
		this.line = "";
		this.path = "C:\\Users\\Igor\\Desktop\\Logs\\";
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
		File file = new File(this.path+this.fileName);
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

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	
	
	
}
