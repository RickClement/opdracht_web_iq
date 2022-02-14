/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clementr.opdracht_web_iq;

/*
 * Jsoup imports for web crawling/scraping utility.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.io.IOException;

/**
 * Main crawler class. Takes a web link from command line input and crawls the page, ranking the most important
 * terms on the page, determined by their TFIDF scores.
 * Optional arguments:
 * -r: rank: The number of terms printed. Default value: 5.
 * -d: depth: Limits the crawling to a certain depth from the starting page. Default value: 2.
 * -t: time. Limits the crawling to a certain time in seconds. Default value: 300.
 * @author clementr
 */
public class Crawler {
    // Class variables
    
    // Rank; denotes the number of returned terms, sorted by importance per TFIDF.
    static int rank = 5;
    // Depth; denotes the depth of web crawling.
    static int depth = 2;
    // Time; denotes the time spent crawling in seconds.
    static long time = 300;
    // Set up the set of visited links.
    static HashSet<String> visited;
    // Set up the corpus.
    static Corpus corpus;
    
    public static void main(String[] args){
        // Process the command line arguments and extract the URL to be crawled.
        String url = process_arguments(args);
        
        // Initialise a set with visited links.
        visited = new HashSet<String>();
        
        // Create the corpus
        corpus = new Corpus();
        
        // Take the current time to compare to the maximum.
        long start_time = System.currentTimeMillis();
        // Keep track of the current depth.
        int current_depth = 0;
        
        // Start crawling and extracting text recursively.
        System.out.println("Starting crawling at " + url + ".");
        crawl_extract(url, current_depth, start_time);
        System.out.println("Crawling complete, calculating TFIDF.");
        
        // After crawling and extracting is done, calculate TFIDF scores.
        TFIDF tfidf = new TFIDF(corpus);
        LinkedHashMap<String, Double> scores = tfidf.calculate_TFIDF(rank);
        System.out.println("Top " + String.valueOf(rank) + " words in " + url + " based on TFIDF scores:");
        int position = 1;
        for (Map.Entry top_term : scores.entrySet()) {
            System.out.println(String.valueOf(position) + ": " + top_term.getKey() + ". TFIDF value: " + String.valueOf(top_term.getValue()) + ".");
            position++;
        }
    }

    private static void crawl_extract(String url, int current_depth, long start_time) {
        if(!url.contains("#") && !visited.contains(url) && (current_depth <= depth && System.currentTimeMillis() - start_time < (time * 1000))) {
            try {
                visited.add(url);
                
                Document page = Jsoup.connect(url).get();
                // Extract all links on the page.
                Elements links = page.select("a[href]");
                
                // Extract and process the text from the body.
                String[] text = page.body().text().split("\\W+");
                // If this is not our initial page, add a term list to our corpus.
                if(current_depth > 0) {
                    corpus.add_page();
                }
                for(String term : text) {
                    term = term.toLowerCase();
                    // If this is our initial page, we need to use a different method in Corpus
                    if(current_depth == 0) {
                        corpus.add_word_freq(term);
                    } else {
                        // The index in the corpus should be the size of visited links - 2
                        // to account for our initial page.
                        corpus.add_word(visited.size() - 2, term);
                    }
                }
                
                // Increase depth and recursively search through the found links.
                // current_depth++;
                for(Element link : links) {
                    // Exclude bookmark links.
                    String new_url = link.attr("abs:href");
                    if(!new_url.contains(url)){
                        crawl_extract(new_url, current_depth + 1, start_time);
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Exception caught in " + url + ": " + ioe.getMessage());                
                // If crawling failed, remove from visited links.
                if(visited.contains(url)){
                    visited.remove(url);
                    // Remove term list in case exception was thrown halfway through crawling.
                    if(!((visited.size() - 1) == corpus.term_lists.size())) {
                        int final_index = corpus.term_lists.size() - 1;
                        corpus.term_lists.remove(final_index);
                    }
                }
            } catch (IllegalArgumentException iae) {
                System.err.println("Exception caught in " + url + ": "+ iae.getMessage());
                // If crawling failed, remove from visited links.
                if(visited.contains(url)){
                    visited.remove(url);
                    // Remove term list in case exception was thrown halfway through crawling.
                    if(!((visited.size() - 1) == corpus.term_lists.size())) {
                        int final_index = corpus.term_lists.size() - 1;
                        corpus.term_lists.remove(final_index);
                    }
                }
            }
        }
    }
    
    /**
     * Iterates over the arguments, set up instance variables and return URL String
     * @param args
     * @return URL String
     */
    private static String process_arguments (String[] args){
        // First argument is always the URL to be crawled.
        // TODO: Add some check to see whether the URL is valid, it's probably neater to do that here
        // instead of try-catching the Jsoup.connect()
        String url = args[0];
        
        // Start iterating over the remaining arguments to set up the variables
        for(int index = 1; index < args.length; index++) {
            String cmd = args[index];
            switch(cmd) {
                case "-r":
                    // Check if there was a proper argument provided after the command.
                    if (index == args.length) {
                        System.err.println("No valid argument detected for variable rank, using default value " + String.valueOf(rank) + ".");
                    } else {
                        // Increase index to grab the set value and check 
                        index++;
                        try {
                            int value = Integer.parseInt(args[index]);
                            rank = value;
                            break;
                        } catch (NumberFormatException e) {
                            // Make sure decrease index back!
                            index--;
                            System.err.println("No valid argument detected for variable rank, using default value " + String.valueOf(rank) + ".");
                            // Exception is handled by using the default value, so continue the loop rather than break.
                            continue;
                        }
                    }                    
                case "-d":
                    // Check if there was a proper argument provided after the command.
                    if (index == args.length) {
                        System.err.println("No valid argument detected for variable depth, using default value " + String.valueOf(depth) + ".");
                    } else {
                        // Increase index to grab the set value and check 
                        index++;
                        try {
                            int value = Integer.parseInt(args[index]);
                            depth = value;
                            break;
                        } catch (NumberFormatException e) {
                            // Make sure decrease index back!
                            index--;
                            System.err.println("No valid argument detected for variable depth, using default value " + String.valueOf(depth) + ".");
                            // Exception is handled by using the default value, so continue the loop rather than break.
                            continue;
                        }
                    } 
                case "-t":
                    // Check if there was a proper argument provided after the command.
                    if (index == args.length) {
                        System.err.println("No valid argument detected for variable time, using default value " + String.valueOf(time) + ".");
                    } else {
                        // Increase index to grab the set value and check 
                        index++;
                        try {
                            long value = Integer.parseInt(args[index]);
                            time = value;
                            break;
                        } catch (NumberFormatException e) {
                            // Make sure decrease index back!
                            index--;
                            System.err.println("No valid argument detected for variable time, using default value " + String.valueOf(time) + ".");
                            // Exception is handled by using the default value, so continue the loop rather than break.
                            continue;
                        }
                    }
                default: 
                    // TODO: Add proper formatting message.
                    System.err.println("Unknown command " + cmd + " found, ignoring.");
            }
        }
        return url;
    }
}
