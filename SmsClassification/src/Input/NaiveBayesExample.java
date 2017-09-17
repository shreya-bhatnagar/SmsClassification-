/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author shreya
 */
public class NaiveBayesExample {
    
     /**
     * Reads the all lines from a file and places it a String array. In each 
     * record in the String array we store a training example text.
     * 
     * @param url
     * @return
     * @throws IOException 
     */
    public static String[] readLines(URL url) throws IOException {

        Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        System.out.println("Array Size:"+lines.toArray(new String[lines.size()]).length);
        return lines.toArray(new String[lines.size()]);
        
    }
    
    
    /**
     * Main method
     * 
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
       //ist<String> arguments = runtimeMxBean.getInputArguments();
       
        //map of dataset files
        Map<String, URL> trainingFiles=null;
        NaiveBayes nb = new NaiveBayes();
        Map<String, String[]> trainingExamples = new HashMap<>();  
     for(int i=1;i<=3;i++){
            switch (i) {
                case 1:
            trainingFiles = new HashMap<>();
            trainingFiles.put("Trans", NaiveBayesExample.class.getResource("/datasets/training_data_trans.txt"));
                    break;
                case 2:
            trainingFiles = new HashMap<>();
            trainingFiles.put("Promo", NaiveBayesExample.class.getResource("/datasets/training_data_promo.txt")); 
                    break; 
                default:
            trainingFiles = new HashMap<>();
            trainingFiles.put("OTP", NaiveBayesExample.class.getResource("/datasets/training_data_otp.txt"));     
                    break;
            }
      
         //loading examples in memory
       
        for(Map.Entry<String, URL> entry : trainingFiles.entrySet()) {
            trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
        } 
        trainingFiles=null;
        //train classifier
        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
        System.gc();
        nb.train(trainingExamples);
     }
        //get trained classifier knowledgeBase
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
        
        nb = null; 
        trainingExamples = null;
        
        //Use classifier and prediction
        nb = new NaiveBayes(knowledgeBase);
          String DIRECTORY_PATH = "/home/shreya/NetBeansProjects/SmsClassification/src/datasets/";
         List<String> test_data_lines = Files.readAllLines(Paths.get(DIRECTORY_PATH + "test_data.txt"));
	 System.out.println(test_data_lines.size() + " lines read from " + Paths.get(DIRECTORY_PATH+ "trans.txt") );

        for (int index = 0; index < 10; index++) {
         
         String incomingSMS = test_data_lines.get(index);
         
         String predicted_class = nb.predict(incomingSMS);
         System.out.format("The sentense id="+index+" was classified as:", predicted_class);
//        
        }
        
        
//        String exampleEn = "Rs.2000.00 was withdrawn using your HDFC Bank Card ending 6476 on 2017-03-08:19:55:45 at +SITE NO 650 11TH MAIN. Avl bal: Rs.34654.23";
//        
//        String outputEn = nb.predict(exampleEn);
//        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleEn, outputEn);
//        
//        String exampleFr = "Do not miss the special IPL offer! Get extra 10% off @ Biryani Day, Imperio Restaurant, Jaffas Biryani, Delight Food & more. Use code FPWD goo.gl/mWgvjP *T&C.";
//        String outputFr = nb.predict(exampleFr);
//        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleFr, outputFr);
//        
//        String exampleDe = "OTP is 647595 for txn of INR 1893.49 at Myntra Designs Private Li on card ending 8877. Valid till 14:43:44. Do not share OTP for security";
//        String outputDe = nb.predict(exampleDe);
//        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleDe, outputDe);
//  
    }
}
