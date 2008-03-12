package net.bcharris.trie;

/**
 *
 * @author brian
 */
public interface Trie {

    /**
     * Find the best match to 'word' in this trie; uses edit distance and word frequency.
     * @param word The word to approximate.
     * @param max_time How long before returning the current best match (via early exit), in millis.
     * @return The best match to the word that exists in this trie.
     */
    String bestMatch(String word, long max_time);

    /**
     * Check if this trie contains the word.
     * @param word The word to check.
     * @return If this trie contains the word.
     */
    boolean contains(String word);

    /**
     * Get the number of occurances of 'word' in this trie.
     * @param word The word to check.
     * @return The number of occurances of 'word' in this trie;
     * 0 if non-existant.
     */
    int frequency(String word);

    /**
     * Inserts 'word' into this trie.
     * @param word The word to insert into the trie.
     * @return The number of occurances of 'word' in the trie
     * after the insertion.
     */
    int insert(String word);

    /**
     * Removes 'word' from this trie.
     * @param word The word to remove from the trie.
     * @return True if 'word' was removed from the trie, else
     * false (meaning 'word' wasn't in the trie).
     */
    boolean remove(String word);

    /**
     * @return The number of unique words in this trie.
     */
    int size();

}
