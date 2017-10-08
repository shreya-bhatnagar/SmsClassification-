/*
 * Naive Baise Tokenizer
 * TextTokenizer class used to tokenize the texts and store them as Document objects.
 */
package Input;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
//import javax.swing.text.Document;
//import org.w3c.dom.Document;
//import Input.Document;
/**
 *
 * @author shreya
 */
public class Tokenizer   {  
    
    /**
     * Preprocess the text by removing punctuation, duplicate spaces and 
     * lowercasing it.
     */
   public static String preprocess(String text) {
    return text.replaceAll("[0-9]", "").replaceAll(" XXXXXXXX.*", "").replaceAll("[\\p{P}&&[^\u0025]]", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());        
   }
    
    /**
     * A simple method to extract the keywords from the text. 
     */
    public static String[] extractKeywords(String text) {
        if(text.contains("%")){
           text= text.replace("%", " %");
        }
        return text.split(" ");
    }
    
    /**
     * Counts the number of occurrences of the keywords inside the text.
     * 
     */
    public static  Map<String, Integer> getKeywordCounts(String[] keywordArray) {
        Map<String, Integer> counts = new HashMap<String,Integer>();
        
        Integer counter;
        for(int i=0;i<keywordArray.length;++i) {
            counter = counts.get(keywordArray[i]);
            if(counter==null) {
                counter=0;
            }
            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
        }
        
        return counts;
    }
    
    /**
     * Tokenizes the document and returns a Document Object.
     * 
     */
    public static Document tokenize(String text) {
        String preprocessedText = preprocess(text);
        String[] keywordArray = extractKeywords(preprocessedText);
        
        Document doc = new Document() ;     //see the main method and caller of tokenize() method
        doc.tokens = getKeywordCounts(keywordArray);
        
        return doc;
    }

     public static void main(String args[])
   { 
    String sms1= "Thanks for your interest. To for start s messages sms, START WLCCOLLEGE to 575758 @Rs3/";
    String sms2= "Dear Customer, Your Ac XXXXXXXX2183 is debited with dear INR24,000.00  on 12 Jan. Info.CASH PAID: SELF. Your Total Avbl. Bal is INR56,167.00";
    Tokenizer tok = new Tokenizer();
    Map<String, Integer> counts = new HashMap<String,Integer>();
    String preProcessed = tok.preprocess(sms2);
    // System.out.print(preProcessed);
   String[] keyWords = tok.extractKeywords(preProcessed);int i=0;
   while(i < keyWords.length)
   {
   System.out.println(keyWords[i]);
   i++; 
   }
    counts = tok.getKeywordCounts(keyWords);
    System.out.println("Count:"+counts.get("dear"));
  }
}
