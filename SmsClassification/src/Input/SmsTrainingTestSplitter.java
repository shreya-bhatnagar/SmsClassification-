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
import java.util.Random;

/**
 *
 * @author shreya
 */
public class SmsDistribution {
    static File file=null;
    static PrintWriter out;
    static BufferedWriter bw;
    public static void main(String Str[]) throws FileNotFoundException, IOException  {
        // Random rng_for_picking = new Random();
        Random rng_for_training_test_split = new Random();
         int num_sms_test_data=0;
         int num_sms_training_data=0; 
         int num_sms_picked=0; 
         int num_sms_ignored=0;
             for (int id = 0; id < 1251475; id++) {
              // int random_no_for_picking  = rng_for_picking.nextInt(100);  // random no between 0 to 99.
//                 if (random_no_for_picking <= 100) {
//                     num_sms_ignored++;
//                     continue;  // To next SMS. Ignore this one.
//                 }else{
//                 num_sms_picked++;
//                 }
            String incomingSMS = Files.readAllLines(Paths.get("/home/shreya/dataset/splitdata/trans.txt")).get(id);
            int random_no_for_training_test_split = rng_for_training_test_split.nextInt(5);  // random no between 0 to 4.
            if (random_no_for_training_test_split == 0) {  // i.e., 1 in 5 chance, i.e., 20%
                num_sms_test_data++;
                splitMessage(incomingSMS, "test_data");
                //System.out.println("SMS id " + id + " is test data"+ " random no."+random_no_for_training_test_split+ " test_data_counter:"+num_sms_test_data);
             } else {
                num_sms_training_data++;
               //System.out.println("SMS id " + id + " is training data"+" random no."+random_no_for_training_test_split+" training_data_counter:"+num_sms_training_data);
              splitMessage(incomingSMS, "training_data");
            }
            
        }
             //System.out.println("num_sms_picked:"+num_sms_picked);
             //System.out.println("num_sms_ignored:"+num_sms_ignored);
             System.out.println("test_data_counter:"+num_sms_test_data);
             System.out.println("training_data_counter:"+num_sms_training_data);
            
    }
//Confirm that the file numbers are generated balanced in range 0 to 4
    
    public static void splitMessage(String incomingSMS, String splitFile) throws IOException {
    String path = "/home/shreya/dataset/splitdata/precision/trans/";
     file = new File(path+splitFile);
     FileWriter fw=new FileWriter(file.getAbsoluteFile(),true);
     bw= new BufferedWriter(fw);
     out = new PrintWriter(bw);
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
