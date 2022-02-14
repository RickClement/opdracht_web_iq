/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clementr.opdracht_web_iq;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class used to compile the full corpus for the crawled webpage.
 * Contains a HashMap containing the full term frequencies for the target site
 * and an ArrayList containing ArrayLists with term lists for each other crawled site.
 * @author clementr
 */
public class Corpus {
    // Class variables
    
    // Total count; denotes the total term count in the target site.
    public static int target_term_count = 0;
    // Target map; contains the per-term frequency of the target site.
    public static HashMap<String, Integer> target_freq_map;
    // Term lists; contains ArrayLists of each other crawled site and the terms found in them.
    public static ArrayList<ArrayList<String>> term_lists;
    
    public Corpus() {
        target_freq_map = new HashMap<String, Integer>();
        term_lists = new ArrayList<ArrayList<String>>();
    }
    
    /**
     * Method to add a word to the frequency list of the original webpage.
     * Will either add the word to the list if it is not yet present or increase
     * its count by 1 if the word is already present.
     * @param word
     */
    public static void add_word_freq(String term) {
        target_freq_map.merge(term, 1, Integer::sum);
        // Increment the target term count by 1 for term frequency calculation.
        target_term_count++;
    }
    
    /**
     * Method to add a word to the term list of a crawled webpage.
     * @param index
     * @param word 
     */
    public static void add_word(int index, String term) {
        ArrayList<String> target = term_lists.get(index);
        if (!target.contains(term)) {
            target.add(term);
        }
    }
    
    /**
     * Help method to add a new term list for a fresh page.
     */
    public static void add_page() {
        term_lists.add(new ArrayList<String>());
    }
}
