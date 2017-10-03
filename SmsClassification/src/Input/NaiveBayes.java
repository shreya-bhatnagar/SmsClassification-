package Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author shreya
 */
public class NaiveBayes {

    private double chisquareCriticalValue = 10.83; //equivalent to pvalue 0.001. It is used by feature selection algorithm

    private NaiveBayesKnowledgeBase knowledgeBase;

    /**
     * This constructor is used when we load an already train classifier
     *
     * @param knowledgeBase
     */
    public NaiveBayes(NaiveBayesKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    /**
     * This constructor is used when we plan to train a new classifier.
     */
    public NaiveBayes() {
        this(null);
        System.out.println("Train the new classifier-----------");
    }

    /**
     * Gets the knowledgebase parameter
     *
     * @return
     */
    public NaiveBayesKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    /**
     * Gets the chisquareCriticalValue paramter.
     *
     * @return
     */
    public double getChisquareCriticalValue() {
        return chisquareCriticalValue;
    }

    /**
     * Sets the chisquareCriticalValue parameter.
     *
     * @param chisquareCriticalValue
     */
    public void setChisquareCriticalValue(double chisquareCriticalValue) {
        System.out.println("setChisquare:"+chisquareCriticalValue);
        this.chisquareCriticalValue = chisquareCriticalValue;
        // System.out.
    }

    /**
     * Preprocesses the original dataset and converts it to a List of Documents.
     *
     * @param trainingDataset
     * @return
     */
    private List<Document> preprocessDataset(Map<String, String[]> trainingDataset) {
        List<Document> dataset = new ArrayList<Document>();

        String category;
        String[] examples;

        Document doc;

        Iterator<Map.Entry<String, String[]>> it = trainingDataset.entrySet().iterator();

        //loop through all the categories and training examples
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            category = entry.getKey();
            examples = entry.getValue();

            for (int i = 0; i < examples.length; ++i) {
                //for each example in the category tokenize its text and convert it into a Document object.
                doc = Tokenizer.tokenize(examples[i]);
                doc.category = category;
               //  System.out.println("category:"+category);

                dataset.add(doc);

                examples[i] = null; //try freeing some memory
            }

            it.remove(); //try freeing some memory
        }

        return dataset;
    }

    /**
     * Gathers the required counts for the features and performs feature
     * selection on the above counts. It returns a FeatureStats object that is
     * later used for calculating the probabilities of the model.
     *
     * @param dataset
     * @return
     */
    private FeatureStats selectFeatures(List<Document> dataset) {
        FeatureExtraction featureExtractor = new FeatureExtraction();

        //the FeatureStats object contains statistics about all the features found in the documents
        FeatureStats stats = featureExtractor.extractFeatureStats(dataset); //extract the stats of the dataset

        //we pass this information to the feature selection algorithm and we get a list with the selected features
        Map<String, Double> selectedFeatures = featureExtractor.chisquare(stats, chisquareCriticalValue);

        //clip from the stats all the features that are not selected
        Iterator<Map.Entry<String, Map<String, Integer>>> it = stats.featureCategoryJointCount.entrySet().iterator();
        while (it.hasNext()) {
            String feature = it.next().getKey();

            if (selectedFeatures.containsKey(feature) == false) {
                //if the feature is not in the selectedFeatures list remove it
                it.remove();
            }
        }

        return stats;
    }

    /**
     * Trains a Naive Bayes classifier by using the Multinomial Model by passing
     * the trainingDataset and the prior probabilities.
     *
     * @param trainingDataset
     * @param categoryPriors
     * @throws IllegalArgumentException
     */
    public void train(Map<String, String[]> trainingDataset, Map<String, Double> categoryPriors) throws IllegalArgumentException {
      
        //preprocess the given dataset
        List<Document> dataset = preprocessDataset(trainingDataset);

        //produce the feature stats and select the best features
        FeatureStats featureStats = selectFeatures(dataset);

        //intiliaze the knowledgeBase of the classifier
        knowledgeBase = new NaiveBayesKnowledgeBase();
        knowledgeBase.num_training_observations = featureStats.n; //number of observations
        knowledgeBase.num_features = featureStats.featureCategoryJointCount.size(); //number of features

        //check is prior probabilities are given
        if (categoryPriors == null) {
            //if not estimate the priors from the sample
            knowledgeBase.num_categories = featureStats.categoryCounts.size(); //number of cateogries
            //c=number of categories
            knowledgeBase.logPriors = new HashMap<String, Double>();

            String category;
            int count;
            for (Map.Entry<String, Integer> entry : featureStats.categoryCounts.entrySet()) {
                category = entry.getKey();
                count = entry.getValue();
                //n=number of training observations
                knowledgeBase.logPriors.put(category, Math.log((double) count / knowledgeBase.num_training_observations));
                System.out.println("catogries::" + category + ", priorProb:" + Math.log((double) count / knowledgeBase.num_training_observations) + ", count:" + count + ", knowledgeBase.n=" + knowledgeBase.num_training_observations+" knowledgeBase.c:"+knowledgeBase.num_categories+" knowledgeBase.d:"+knowledgeBase.num_features);
                
            }
            for(Map.Entry entry : knowledgeBase.logPriors.entrySet() ){
            System.out.println("knowBase.logPriors key:"+entry.getKey()+" knowBase.logPriors value:"+entry.getValue());
            }
        } else {
            //if they are provided then use the given priors
            knowledgeBase.num_categories = categoryPriors.size();
            System.out.println("knowledgeBase.c"+knowledgeBase.num_categories);
            //make sure that the given priors are valid
            if (knowledgeBase.num_categories != featureStats.categoryCounts.size()) {
                throw new IllegalArgumentException("Invalid priors Array: Make sure you pass a prior probability for every supported category.");
            }

            String category;
            Double priorProbability;
            for (Map.Entry<String, Double> entry : categoryPriors.entrySet()) {
                category = entry.getKey();   
                priorProbability = entry.getValue();
                if (priorProbability == null) {
                    throw new IllegalArgumentException("Invalid priors Array: Make sure you pass a prior probability for every supported category.");
                } else if (priorProbability < 0 || priorProbability > 1) {
                    throw new IllegalArgumentException("Invalid priors Array: Prior probabilities should be between 0 and 1.");
                }

                knowledgeBase.logPriors.put(category, Math.log(priorProbability));
            }
        }

        //We are performing laplace smoothing (also known as add-1). This requires to estimate the total feature occurrences in each category
        Map<String, Double> featureOccurrencesInCategory = new HashMap<String, Double>();

        Integer occurrences;
        Double featureOccSum;
        for (String category : knowledgeBase.logPriors.keySet()) {
            featureOccSum = 0.0;
            for (Map<String, Integer> categoryListOccurrences : featureStats.featureCategoryJointCount.values()) {
                occurrences = categoryListOccurrences.get(category);
                if (occurrences != null) {
                    featureOccSum += occurrences;
                }
            }
            featureOccurrencesInCategory.put(category, featureOccSum);
        }

        //estimate log likelihoods
        String feature;
        Integer count;
        Map<String, Integer> featureCategoryCounts;
        double logLikelihood;
        double prob;
        for (String category : knowledgeBase.logPriors.keySet()) {
            for (Map.Entry<String, Map<String, Integer>> entry : featureStats.featureCategoryJointCount.entrySet()) {
                feature = entry.getKey();

               // System.out.println("Train method feature:" + feature + ", featureCategoryCounts:" + entry.getValue());
                featureCategoryCounts = entry.getValue();

                count = featureCategoryCounts.get(category);
             //   System.out.println("count=featureCategoryCounts.get(category):" + featureCategoryCounts.get(category));
                if (count == null) {
                    count = 0;
                }
                //d=number of feature
                logLikelihood = Math.log((count + 1.0) / (featureOccurrencesInCategory.get(category) + knowledgeBase.num_features));
                prob=(count + 1.0) / (featureOccurrencesInCategory.get(category) + knowledgeBase.num_features);
            //    System.out.println("logLikelihood:"+logLikelihood+" probability:"+prob);
                if (knowledgeBase.logLikelihoods.containsKey(feature) == false) {
                    knowledgeBase.logLikelihoods.put(feature, new HashMap<String, Double>());
                }
                knowledgeBase.logLikelihoods.get(feature).put(category, logLikelihood);
            }
        }
        featureOccurrencesInCategory = null;
    }

    /**
     * Wrapper method of train() which enables the estimation of the prior
     * probabilities based on the sample.
     *
     * @param trainingDataset
     */
    public void train(Map<String, String[]> trainingDataset) {
        System.out.println("train Wrapper---");
        train(trainingDataset, null);
    }

    /**
     * Predicts the category of a text by using an already trained classifier
     * and returns its category.
     *
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public String predict(String text) throws IllegalArgumentException {
        if (knowledgeBase == null) {
            throw new IllegalArgumentException("Knowledge Bases missing: Make sure you train first a classifier before you use it.");
        }

        //Tokenizes the text and creates a new document
        Document doc = Tokenizer.tokenize(text);
       // System.out.println("Document catogry:" + doc.tokens);

        String category;
        String feature;
        Integer occurrences;
        Double logprob;

        String maxScoreCategory = null;
        Double maxScore = Double.NEGATIVE_INFINITY;

        Map<String, Double> predictionScores = new HashMap<String, Double>();
        for (Map.Entry<String, Double> category_logProb_entry : knowledgeBase.logPriors.entrySet()) {
            category = category_logProb_entry.getKey();
            logprob = category_logProb_entry.getValue(); //intialize the scores with the priors
            System.out.println("logprob======" + logprob + " category:" + category);
            //foreach feature of the document
            for (Map.Entry<String, Integer> doc_token_entry : doc.tokens.entrySet()) {
                feature = doc_token_entry.getKey();
                //System.out.println("Feature:"+feature);
                if (!knowledgeBase.logLikelihoods.containsKey(feature)) {
                    //logLikelihoods is hasmap of 
                     System.out.println("category:"+category+"  feature:"+feature+"-------------->Feature NOT FOUND");
                    continue; //if the feature does not exist in the knowledge base skip it
                }

                occurrences = doc_token_entry.getValue(); //get its occurrences in text
                System.out.println("category:"+category+" feature:"+feature+"  occurrences:"+occurrences);
              //  System.out.println("logLikelihoodxts.get(feature).get(category):" + knowledgeBase.logLikelihoods.get(feature).get(category));
                logprob += occurrences * knowledgeBase.logLikelihoods.get(feature).get(category); //multiply loglikelihood score with occurrences
            }
          predictionScores.put(category, logprob); 
            System.out.println("category:"+category+"  logprob (occurence*knowledgeBase):" + logprob+" current MaxScore:"+maxScore);
            if (logprob > maxScore) {
                System.out.println("max score:" + maxScore);
                maxScore = logprob;
                maxScoreCategory = category;
            }
        }

        return maxScoreCategory; //return the category with heighest score
    }
}
