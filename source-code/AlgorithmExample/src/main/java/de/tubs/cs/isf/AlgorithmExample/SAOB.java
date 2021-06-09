package de.tubs.cs.isf.AlgorithmExample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.ovgu.featureide.fm.benchmark.util.Logger;

/**
 * Hello world!
 *
 */
public class SAOB 
{
    public static void main( String[] args )
    {
        System.out.println("This is the Sampling Algorithm of Bob!");
//        Logger.getInstance().logInfo("This is the Sampling Algorithm of Bob!", false);
        System.out.println("Start Sampling....");
        for(int i = 0; i<10; i++) {
        	System.out.println("...");
            try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        File file = new File("./saob_test.txt");
		
		String data = "Hello World!\nWelcome to www.tutorialkart.com";
		
		try(FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos)) {
			//convert string to byte array
			byte[] bytes = data.getBytes();
			//write byte array to file
			bos.write(bytes);
			bos.close();
			fos.close();
			System.out.print("Data written to file successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
       System.out.println("Sampling process complete");
       System.out.println("Shutting down application");
    }
}
