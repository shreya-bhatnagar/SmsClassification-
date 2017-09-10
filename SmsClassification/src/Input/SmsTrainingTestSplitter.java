/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 *
 * @author shreya
 */
public class SmsTrainingTestSplitter {
  private static final String DIRECTORY_PATH = "/home/shreya/dataset/splitdata/";

  public static void main(String Str[]) throws FileNotFoundException, IOException  {
        // Random rng_for_picking = new Random();
        Random rng_for_training_test_split = new Random();
         int num_sms_test_data=0;
         int num_sms_training_data=0; 
         int num_sms_picked=0; 
         int num_sms_ignored=0;
         Files.createDirectories(Paths.get(DIRECTORY_PATH + "precision/trans"));

     PrintWriter training_data_pw = MakePrintWriter(DIRECTORY_PATH + "precision/trans/training_data.txt");
     PrintWriter test_data_pw = MakePrintWriter(DIRECTORY_PATH + "precision/trans/test_data.txt");

	 List<String> all_lines = Files.readAllLines(Paths.get(DIRECTORY_PATH + "trans.txt"));
	 System.out.println(all_lines.size() + " lines read from " + Paths.get(DIRECTORY_PATH + "trans.txt") );

     for (int id = 0; id < all_lines.size(); id++) {
              // int random_no_for_picking  = rng_for_picking.nextInt(100);  // random no between 0 to 99.
//                 if (random_no_for_picking <= 100) {
//                     num_sms_ignored++;
//                     continue;  // To next SMS. Ignore this one.
//                 }else{
//                 num_sms_picked++;
//                 }
            String incomingSMS = all_lines.get(id);
            if (incomingSMS.isEmpty()) continue;  // Skip empty line.
            int random_no_for_training_test_split = rng_for_training_test_split.nextInt(5);  // random no between 0 to 4.
            if (random_no_for_training_test_split == 0) {  // i.e., 1 in 5 chance, i.e., 20%
                num_sms_test_data++;
                System.out.println("SMS id " + id + " is test data"+ " random no."+random_no_for_training_test_split+ " test_data_counter:"+num_sms_test_data);
        		test_data_pw.println(incomingSMS);
             } else {
                num_sms_training_data++;
               System.out.println("SMS id " + id + " is training data"+" random no."+random_no_for_training_test_split+" training_data_counter:"+num_sms_training_data);
	      training_data_pw.println(incomingSMS);
            }
            
        }
     test_data_pw.flush();
     training_data_pw.flush();
             //System.out.println("num_sms_picked:"+num_sms_picked);
             //System.out.println("num_sms_ignored:"+num_sms_ignored);
             System.out.println("test_data_counter:"+num_sms_test_data);
             System.out.println("training_data_counter:"+num_sms_training_data);
            
    }
//Confirm that the file numbers are generated balanced in range 0 to 4
    
    public static PrintWriter MakePrintWriter(String file_full_path) throws IOException {
     File file = new File(file_full_path);
     // if File doesnt exists, then create it
     if (!file.exists()) {
       System.out.println("File not there : " + file.getAbsolutePath());
       file.createNewFile();
     }
     return new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile(), false /* dont append */)));
    }

    public static void splitMessage(String incomingSMS, String splitFile) throws IOException {
    String path = "/home/shreya/dataset/splitdata/precision/trans/";
     File file = new File(path+splitFile);
     FileWriter fw=new FileWriter(file.getAbsoluteFile(),true);
     BufferedWriter bw= new BufferedWriter(fw);
     PrintWriter out = new PrintWriter(bw);
        // if File doesnt exists, then create it
        if (!file.exists()) {
            System.out.println("File not there");
            file.createNewFile();
        }
        out.println(incomingSMS);
        bw.flush();
        bw.close();
    
    // System.out.println("SMS id " + incomingSMS + " is "+splitFile);
        
    }

}
