import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 *
 * @author William Liu
 *
 */
public final class wordCounter {

    /**
     * Definition of whitespace separators.
     */
    private static final String SEPARATORS = " \t\n\r,.!?[]';:/()<>";

    /**
     * Compares pair keys in lexicographic order, ignoring case.
     */
    private static class KeyLT implements Comparator<Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> p1,
                Entry<String, Integer> p2) {
            String s1 = p1.getKey();
            String s2 = p2.getKey();
            return s1.compareToIgnoreCase(s2);
        }
    }

    /**
     * Compares pair values from greatest to smallest based on integer values of
     * the keys.
     */
    private static class ValueGT implements Comparator<Entry<String, Integer>> {

        @Override
        public int compare(Entry<String, Integer> p1,
                Entry<String, Integer> p2) {
            int diff = p2.getValue() - p1.getValue();
            if (diff == 0) {
                diff = p1.getKey().compareToIgnoreCase(p2.getKey());
            }
            return diff;
        }
    }

    /**
     * Default constructor--private to prevent instantiation.
     */
    private wordCounter() {
        // no code needed here
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code SEPARATORS}) or "separator string" (maximal length string of
     * characters in {@code SEPARATORS}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection entries(SEPARATORS) = {}
     * then
     *   entries(nextWordOrSeparator) intersection entries(SEPARATORS) = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection entries(SEPARATORS) /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of entries(SEPARATORS)  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of entries(SEPARATORS))
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        /*
         * Check if it will be a word or separator by checking the character at
         * position. Then, keep incrementing the final position i until the end
         * of text is reached or the isSeparator property of the character at
         * index i does not match the initial character's isSeparator
         */
        boolean isSeparator = SEPARATORS.indexOf(text.charAt(position)) >= 0;
        int i = position;
        while (i < text.length()
                && (SEPARATORS.indexOf(text.charAt(i)) >= 0) == isSeparator) {
            i++;
        }
        return text.substring(position, i);
    }

    /**
     * Creates a map {@code wordCountMap} of the unique words from {@code input}
     * and their respective counts.
     *
     * @param wordCountMap
     *            the map in which the unique words and their respective integer
     *            counts are stored
     * @param input
     *            input file that contains all the words
     * @replaces wordCountMap
     * @updates input
     * @ensures [wordCountMap contains all unique words in input as keys, with
     *          the associated values representing the counts]
     */
    public static int getWordCounts(Map<String, Integer> wordCountMap,
            BufferedReader input) {
        int wordcount = 0;

        String line = "";
        try {
            line = input.readLine();
        } catch (IOException e) {
            System.err.println(
                    "Error reading lines from file: " + e.getMessage());
            return -1;
        }

        while (line != null) {
            int index = 0;
            while (index < line.length()) {
                String token = nextWordOrSeparator(line, index);
                // Increase the index by how long the word/separator string is
                index += token.length();

                // If it's an actual word (i.e. NOT a separator string)
                if (SEPARATORS.indexOf(token.charAt(0)) < 0) {
                    wordcount++;
                    // Check to see if it's in the map. If not, add to map.
                    // Else increase the count of that word by 1.
                    String tokenLC = token.toLowerCase();
                    if (!wordCountMap.containsKey(tokenLC)) {
                        wordCountMap.put(tokenLC, 1);
                    } else {
                        int oldValue = wordCountMap.get(tokenLC);
                        wordCountMap.put(tokenLC, oldValue + 1);
                    }
                    // Use lowercase to avoid different counts between capitals
                }
            }
            try {
                line = input.readLine();
            } catch (IOException e) {
                System.err.println(
                        "Error reading lines from file: " + e.getMessage());
                return 0;
            }
        }
        return wordcount;
    }

//    public static NavigableSet<Entry<String, Integer>> sortByKey(
//            Set<Entry<String, Integer>> unsortedPairs,
//            Comparator<Entry<String, Integer>> order) {
//
//        NavigableSet<Entry<String, Integer>> sortedPairs = new TreeSet<>(order);
//        for (Entry<String, Integer> p : unsortedPairs) {
//            sortedPairs.add(p);
//        }
//        return sortedPairs;
//    }

    public static NavigableSet<Entry<String, Integer>> sortByValue(
            Map<String, Integer> wordCountMap,
            Comparator<Entry<String, Integer>> order) {

        NavigableSet<Entry<String, Integer>> sortedPairs = new TreeSet<>(order);
        for (Entry<String, Integer> p : wordCountMap.entrySet()) {
            sortedPairs.add(p);
        }
        return sortedPairs;
    }

    public static String lastSentence(BufferedReader input, String greatest) {
        String line = "";
        String temp = "";
        String tempLower = "";
        String maxSent = "";
        try {
            line = input.readLine();
        } catch (IOException e) {
            System.err.println(
                    "Error reading lines from file: " + e.getMessage());
            return "";
        }
        while (line != null) {
            int index = 0;
            boolean endSent = false;
            //check for end of sentence
            while (index < line.length() && !endSent) {
                String token = nextWordOrSeparator(line, index);
                // Increase the index by how long the word/separator string is
                index += token.length();

                //Adding next word to the sentence and checking for end. If it is the end of the sentence, then check if it contains the greatest word and reset the line.
                if (token.contains("!") || token.contains(".")
                        || token.contains("?")) {
                    temp = temp + token;
                    tempLower = temp.toLowerCase();
                    if (tempLower.matches(".*\\b" + greatest + "\\b.*")) {
                        maxSent = temp;
                        temp = "";
                    } else {
                        temp = "";
                    }

                } else {
                    temp = temp + token;
                }

            }
            try {
                line = input.readLine();
            } catch (IOException e) {
                System.err.println(
                        "Error reading lines from file: " + e.getMessage());
                return "";
            }
        }

        return maxSent;

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        Comparator<Entry<String, Integer>> keyOrder = new KeyLT();
        Comparator<Entry<String, Integer>> valueOrder = new ValueGT();
        Map<String, Integer> wordCountMap = new TreeMap<>();

        /*
         * Input file ----------------------------------------------------------
         */

        // Input path specification
        System.out.println("Enter name of input file: ");
        String inputFilePath = "";
        try {
            inputFilePath = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading user input: " + e.getMessage());
        }

        // Create input file stream
        BufferedReader inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(inputFilePath));
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return;
        }

        int n = 10;
        int wordcount = getWordCounts(wordCountMap, inputFile);
        NavigableSet<Entry<String, Integer>> s = sortByValue(wordCountMap,
                valueOrder);
        NavigableSet<Entry<String, Integer>> keyset = new TreeSet<>(keyOrder);

        /*
         * Printing top 10------------------------------------------------------
         */
        String greatest = "";
        System.out.println("10 Most Occuring Words by occurence: ");
        for (int i = 0; i < n; i++) {
            System.out.println("Count: " + s.first().getValue() + "\t"
                    + s.first().getKey());
            keyset.add(s.first());
            //holding the word with the greatest occurrence
            if (i == 0) {
                greatest = s.first().getKey();
            }
            s.remove(s.first());
        }

        /*
         * Alphabetical sort----------------------------------------------------
         */
//
//        System.out.println("\nSorted Alphabetically: ");
//        for (int i = 0; i < n; i++) {
//            System.out.println(" " + keyset.first().getKey());
//            keyset.remove(keyset.first());
//
//        }
        try {
            inputFile.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        System.out.println("\nTotal Words: " + wordcount);

        /*
         * Last Sentence--------------------------------------------------------
         */

        // reopen file stream to reset pointer
        inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(inputFilePath));
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return;
        }
        String lastSent = lastSentence(inputFile, greatest);
        System.out.println("\nThe Last Sentence containing \"" + greatest
                + "\": " + lastSent);

        //close file
        try {
            inputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
