package net.bcharris.trie;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;



/**
 * An implementation of a trie that 
 * dynamically grows and shrinks with word insertions 
 * and removals and supports approximate matching of words
 * using edit distance and word frequency.
 * 
 * This trie implementation is not thread-safe because I didn't need it to be,
 * but it would be easy to change.
 * 
 * @author Brian Harris (brian_bcharris_net)
 */
public class TrieImpl implements Trie {

	// dummy node for root of trie
	final TrieNode root;
	
	// current number of unique words in trie
	private int size;
	
	// if this is a case sensitive trie
	protected boolean caseSensitive;
	
	/**
	 * @param caseSensitive If this Trie should be case-insensitive to the words it encounters. 
	 * Case-insensitivity is accomplished by converting String arguments to all functions to 
	 * lower-case before proceeding.
	 */
	public TrieImpl(boolean caseSensitive) {
		root = new TrieNode((char)0);
		size = 0;
		this.caseSensitive = caseSensitive;
	}
	
	public int insert(String word) {
		if (word == null)
			return 0;
		
		int i = root.insert(caseSensitive ? word : word.toLowerCase(), 0);
		
		if (i == 1)
			size++;
		
		return i;
	}
	
	public boolean remove(String word) {
		if (word == null)
			return false;
		
		if (root.remove(caseSensitive ? word : word.toLowerCase(), 0)) {
			size--;
			return true;
		}
		
		return false;
	}
	
	public int frequency(String word) {
		if (word == null)
			return 0;
		
		TrieNode n = root.lookup(caseSensitive ? word : word.toLowerCase(), 0);
		return n == null ? 0 : n.occurances;
	}
	
	public boolean contains(String word) {
		if (word == null)
			return false;
		
		return root.lookup(caseSensitive ? word : word.toLowerCase(), 0) != null;
	}
	
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}

	public String bestMatch(String word, long max_time) {
		
		if (!caseSensitive)
			word = word.toLowerCase();
		
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
}
/**
 * A node in a trie.
 * @author Brian Harris (brian_bcharris_net)
 */
class TrieNode {
	char c;
	int occurances;
	Map<Character, TrieNode> children;
	
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
			children = new HashMap<Character, TrieNode>();

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

	// debugging facility
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
class DYMNode implements Comparable<DYMNode> {
	TrieNode node;
	String word;
	int distance;
	boolean wordExists;
	
	DYMNode(TrieNode node, int distance, String word, boolean wordExists) {
		this.node = node;
		this.distance = distance;
		this.word = word;
		this.wordExists = wordExists;
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

	@Override
	public int hashCode()
	{
		int hash = 31;
		hash += distance * 31;
		hash += node.occurances * 31;
		return hash;
	}

	@Override
	public String toString() {
		return word+":"+distance;
	}
}