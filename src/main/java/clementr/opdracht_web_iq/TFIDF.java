/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clementr.opdracht_web_iq;

// Imports
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class that calculates the Term Frequency - Inverse Document Frequency scores
 * of a given corpus.
 * @author clementr
 */
public class TFIDF {
    
    // Class variables
    public static Corpus corpus;

    public TFIDF(Corpus input) {
        corpus = input;
    }
    
    public static LinkedHashMap<String, Double> calculate_TFIDF(int rank) {
        HashMap<String, Double> scores = new HashMap<String, Double>();
        
        // For each term in target_freq_map, calculate the TFIDF scores
        for (HashMap.Entry<String, Integer> entry : corpus.target_freq_map.entrySet()) {
            double tfidf = calculate_TF(entry) * calculate_IDF(entry);
            scores.put(entry.getKey(), tfidf);
        }
        
        // Sort the map by scores and return the highest ones based on the desired rank.
        return sort_scores(scores, rank);
    }
    
    /**
     * Returns the term frequency.
     * Term frequency is calculated as the raw frequency of the term in the document
     * divided by the total length of the document.
     * @param entry
     * @return term frequency
     */
    private static double calculate_TF(Map.Entry<String, Integer> entry) {
        Double value = Double.valueOf(entry.getValue());
        Double count = Double.valueOf(corpus.target_term_count);
        return value / count;
    }

    /**
     * Returns the inverse document frequency.
     * Inverse document frequency is calculated as the base-10 logarithm of the
     * total number of documents in the corpus, divided by the number of documents
     * which contain the term.
     * @param entry
     * @return inverse document frequency
     */
    private static double calculate_IDF(Map.Entry<String, Integer> entry) {
        // Initialise the term frequency. Remember that term_lists does not include
        // our initial website, but it must by definition include the term!
        int frequency = 1;
        int size = corpus.term_lists.size();
        String term = entry.getKey();
        for(int index = 0;  index < size; index++) {
            if(corpus.term_lists.get(index).contains(term)){
                frequency++;
            }
        }
        return Math.log10((size + 1) / frequency);
    }

    /**
     * Method that sorts the terms by their TFIDF value and returns the first [rank] entries.
     * @param scores
     * @param rank
     * @return 
     */
    private static LinkedHashMap<String, Double> sort_scores(HashMap<String, Double> scores, int rank) {
        LinkedList<Map.Entry<String, Double>> sorted_list = new LinkedList<Map.Entry<String, Double>>(scores.entrySet());
        Comparator<Map.Entry<String, Double>> tfidf_comp = new Comparator<Map.Entry<String, Double>>(){
            @Override
            public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2){
                return(e2.getValue().compareTo(e1.getValue()));
            }
        };
        Collections.sort(sorted_list, tfidf_comp);
        
        LinkedHashMap<String, Double> sorted_scores = new LinkedHashMap<String, Double>();
        for(int index = 0; index < rank; index++){
            Map.Entry<String, Double> entry = sorted_list.get(index);
            sorted_scores.put(entry.getKey(), entry.getValue());
        }
        return sorted_scores;
    }
}
