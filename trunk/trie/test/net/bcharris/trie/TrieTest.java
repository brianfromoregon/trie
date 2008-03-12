package net.bcharris.trie;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Brian Harris (brian_bcharris_net)
 */
public class TrieTest {

	private static String[] words = new String[] {
		"bob","a","bat","ass","za","z","z","Bob"
	};
	private static Map<String, Integer> uniqueWords;
	
	private Trie trie;
	
	@Before
	public void setUp() throws Exception {
		trie = new TrieImpl(true);
		uniqueWords = new HashMap<String, Integer>();
		for (int i=0; i<words.length; i++)
			uniqueWords.put(words[i], 0);
	}

	@After
	public void tearDown() throws Exception {
		trie = null;
		uniqueWords = null;
	}

	@Test
	public void all() {
		int iters=100000;
		insert();
		remove();
		insertAndRemove(iters);
	}
	
	void insert() {
		for (int i=0; i<words.length; i++) {
			trie.insert(words[i]);
			uniqueWords.put(words[i], uniqueWords.get(words[i])+1);
		}
		check();
	}
	
	void remove() {
		Iterator<String> it = uniqueWords.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			trie.remove(s);
			uniqueWords.put(s, 0);
		}
		check();
	}
	
	void insertAndRemove(int iters) {
		Random r = new Random();
		for (int i=0; i<iters; i++) {
			int rInt = r.nextInt(words.length);
			trie.insert(words[rInt]);
			uniqueWords.put(words[rInt], uniqueWords.get(words[rInt])+1);
		}
		check();
		for (int i=0; i<iters; i++) {
			int rInt = r.nextInt(words.length);
			boolean b = r.nextBoolean();
			if (b) {
				trie.insert(words[rInt]);
				uniqueWords.put(words[rInt], uniqueWords.get(words[rInt])+1);
			} else { 
				trie.remove(words[rInt]);
				uniqueWords.put(words[rInt], 0);
			}
		}
		check();
	}
	
	void check() {
		String s = null;
		int count = 0;
		for (Iterator<String> it = uniqueWords.keySet().iterator(); it.hasNext();) {
			s = it.next();
			int val = uniqueWords.get(s);
			assertEquals(val, trie.frequency(s));
			if (val > 0)
				count++;
		}
		assertEquals(count, trie.size());
		System.out.println("----\n"+trie+"----");
	}
}
