package net.bcharris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.bcharris.util.Trie;


public class DidYouMean {

	private Trie trie;
	
	public DidYouMean() {
		trie = new Trie();
	}
	
	public void go() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s;
		while ((s = in.readLine()) != null) {
			if (trie.contains(s))
				System.out.println("Yep!");
			else
				System.out.println("Nope, but did you mean: "+trie.bestMatch(s, Long.MAX_VALUE));
		}
	}
	
	/**
	 * Adds the words in file f to the dictionary, where word is
	 * a series of characters surrounded by whitespace.
	 * @param f A file containing words.
	 */
	public void addToDictionary(File f) 
	throws IOException, FileNotFoundException {
		long t = System.currentTimeMillis();
		final int bufSize = 1000;
		int read;
		int numWords=0;
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			char[] buf = new char[bufSize];
			while ((read = fr.read(buf)) != -1) {
				// TODO modify this split regex to actually be useful
				String[] words = new String(buf, 0, read).split("\\W");
				for (String s : words) {
					trie.insert(s);
					numWords++;
				}
			}
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {}
			}
		}
		System.out.println("Read from file and inserted "+numWords+" words into trie in "+
				(System.currentTimeMillis()-t)/1000.0+" seconds.");
	}
	
	public static void main(String[] args) throws Exception {
		DidYouMean dym = new DidYouMean();
		dym.addToDictionary(new File("bible.txt"));
		dym.go();
	}
}
