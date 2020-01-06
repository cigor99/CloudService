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
	
}
