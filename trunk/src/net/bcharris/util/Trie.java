package net.bcharris.util;

import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * An case-sensitive implementation of a trie that 
 * automatically grows and shrinks with word insertions 
 * and removals and supports approximate matching of words
 * using edit distance and word frequency.
 * 
 * @author Brian Harris (brian_bcharris_net)
 */
public class Trie {

	// dummy node for root of trie
	private TrieNode root;
	
	// current number of unique words in trie
	private int size;
	
	public Trie() {
		root = new TrieNode((char)0);
		size = 0;
	}
	
	/**
	 * Inserts 'word' into this trie.
	 * @param word The word to insert into the trie.
	 * @return The number of occurances of 'word' in the trie
	 * after the insertion.
	 */
	public int insert(String word) {
		if (word == null)
			return 0;
		
		int i = root.insert(word, 0);
		
		if (i == 1)
			size++;
		
		return i;
	}
	
	/**
	 * Removes 'word' from this trie.
	 * @param word The word to remove from the trie.
	 * @return True if 'word' was removed from the trie, else
	 * false (meaning 'word' wasn't in the trie). 
	 */
	public boolean remove(String word) {
		if (word == null)
			return false;
		
		if (root.remove(word, 0)) {
			size--;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the number of occurances of 'word' in this trie.
	 * @param word The word to check.
	 * @return The number of occurances of 'word' in this trie; 
	 * 0 if non-existant.
	 */
	public int frequency(String word) {
		if (word == null)
			return 0;
		
		TrieNode n = root.lookup(word, 0);
		return n == null ? 0 : n.occurances;
	}
	
	/**
	 * Check if this trie contains the word.
	 * @param word The word to check.
	 * @return If this trie contains the word.
	 */
	public boolean contains(String word) {
		if (word == null)
			return false;
		
		return root.lookup(word, 0) != null;
	}
	
	/**
	 * @return The number of unique words in this trie.
	 */
	public int size() {
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * Find the best match to 'word' in this trie; uses edit distance and word frequency.
	 * @param word The word to approximate.
	 * @param max_time How long before returning the current best match (via early exit), in millis.
	 * @return The best match to the word that exists in this trie.
	 */
	public String bestMatch(String word, long max_time) {
		
		// we store candidate nodes in a pqueue in an attempt to find the optimal
		// match ASAP which can be useful for a necessary early exit
		PriorityQueue<DYMNode> pq = new PriorityQueue<DYMNode>();
	
		DYMNode best = new DYMNode(root, Distance.LD("", word), "", false);		
		DYMNode cur = best;
		TrieNode tmp;
		int count=0;
		long start_time = System.currentTimeMillis();
		
		while (true) {
			if (count++ % 1000 == 0 && (System.currentTimeMillis() - start_time) > max_time)
				break;
				
			if (cur.node.children != null) {
				for (char c : cur.node.children.keySet()) {
					tmp = cur.node.children.get(c);
					String tWord = cur.word + c;
					int distance = Distance.LD(tWord, word);
					
					// only add possibly better matches to the pqueue
					if (distance <= cur.distance) {
						if (tmp.occurances == 0)
							pq.add(new DYMNode(tmp, distance, tWord, false));
						else
							pq.add(new DYMNode(tmp, distance, tWord, true));
					}
				}
			}
			
			DYMNode n = pq.poll();
			
			if (n == null)
				break;
			
			cur = n;
			if (n.wordExists)
				// if n is more optimal, set it as best
				if (n.distance < best.distance || (n.distance == best.distance && n.node.occurances > best.node.occurances))
					best = n;
		}
		return best.word;
	}

	/**
	 * An internal class representing a node in this trie.
	 * @author Brian Harris (brian_bcharris_net)
	 */
	private class TrieNode implements Comparable<TrieNode> {
		char c;
		private int occurances;
		private TreeMap<Character, TrieNode> children;
		
		TrieNode(char c) {
			this.c = c;
			occurances = 0;
			children = null;
		}
		
		int insert(String s, int pos) {
			if (s == null || pos >= s.length())
				return 0;
			
			// allocate on demand
			if (children == null)
				children = new TreeMap<Character, TrieNode>();

			char c = s.charAt(pos);
			TrieNode n = children.get(c);

			// make sure we have a child with char c
			if (n == null) {
				n = new TrieNode(c);
				children.put(c, n);
			}
			
			// if we are the last node in the sequence of chars
			// that make up the string
			if (pos == s.length()-1) {
				n.occurances++;
				return n.occurances;
			} else
				return n.insert(s, pos+1);
		}
		
		boolean remove(String s, int pos) {
			if (children == null || s == null)
				return false;
			
			char c = s.charAt(pos);
			TrieNode n = children.get(c);

			if (n == null)
				return false;

			boolean ret;
			if (pos == s.length()-1) {
				int before = n.occurances;
				n.occurances = 0;
				ret = before > 0;
			} else {
				ret = n.remove(s, pos+1);
			}
			
			// if we just removed a leaf, prune upwards
			if (n.children == null && n.occurances == 0) {
				children.remove(n.c);
				if (children.size() == 0)
					children = null;
			}
			
			return ret;
		}
		
		TrieNode lookup(String s, int pos) {
			if (s == null)
				return null;
			
			if (pos >= s.length() || children == null)
				return null;
			else if (pos == s.length()-1)
				return children.get(s.charAt(pos));
			else {
				TrieNode n = children.get(s.charAt(pos)); 
				return n == null ? null : n.lookup(s, pos+1);
			}
		}
		
		public int compareTo(TrieNode t) {
			if (t == null)
				return c;
			
			return c - t.c;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			
			if (o == null)
				return false;
			
			if (!(o instanceof TrieNode))
				return false;
			
			return c == ((TrieNode)o).c;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			dump(sb, "");
			return sb.toString();
		}
		
		private void dump(StringBuilder sb, String prefix) {
			sb.append(prefix+c+"("+occurances+")"+"\n");
			
			if (children == null)
				return;
			
			for (TrieNode n : children.values())
				n.dump(sb, prefix+(c==0?"":c));
		}
	}
	
	/**
	 * Utility class for finding a best match to a word.
	 * @author Brian Harris (brian_bcharris_net)
	 */
	private class DYMNode implements Comparable<DYMNode> {
		TrieNode node;
		String word;
		int distance;
		boolean wordExists;
		
		DYMNode(TrieNode node, int distance, String word, boolean isRealWord) {
			this.node = node;
			this.distance = distance;
			this.word = word;
			this.wordExists = isRealWord;
		}
		
		// break ties of distance with frequency
		public int compareTo(DYMNode n) {
			if (distance == n.distance)
				return n.node.occurances - node.occurances;
			return distance - n.distance;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			
			if (o == null)
				return false;
			
			if (!(o instanceof DYMNode))
				return false;
			
			DYMNode n = (DYMNode)o;
			return distance == n.distance && n.node.occurances == node.occurances;
		}
		
		public String toString() {
			return word+":"+distance;
		}
	}
}
