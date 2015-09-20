package net.bcharris.trie;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import net.bcharris.trie.ViewableTrie;
import net.bcharris.trie.ViewableTrie;

public class TrieViewer extends JFrame {

    private ViewableTrie trie;

    public TrieViewer() {
        trie = new ViewableTrie(false);
    }

    public void go() throws Exception {
        JTree jTree = new JTree(trie.getTreeModel());
        getContentPane().add(new JScrollPane(jTree));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Trie Visualizer");
        setBounds(0, 0, 500, 600); //necessary to display with OpenJDK 7 on 
                //Linux
        pack();
        setVisible(true);
    }
    
    public void addToDictionary(File f) throws IOException, FileNotFoundException {
        addToDictionary(new FileInputStream(f));
    }

    /**
     * Adds the words in file f to the dictionary, where word is
     * a series of characters surrounded by whitespace.
     * @param f A file containing words.
     */
    public void addToDictionary(InputStream f)
            throws IOException, FileNotFoundException {
        long t = System.currentTimeMillis();
        final int bufSize = 1000;
        int read;
        int numWords = 0;
        InputStreamReader fr = null;
        try {
            fr = new InputStreamReader(f);
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
                } catch (IOException e) {
                }
            }
        }
        System.out.println("Read from file and inserted " + numWords + " words into trie in " +
                (System.currentTimeMillis() - t) / 1000.0 + " seconds.");
    }

    public static void main(String[] args) throws Exception {
        TrieViewer tv = new TrieViewer();
        tv.addToDictionary(Thread.currentThread().getContextClassLoader().getResourceAsStream("bible.txt"));
        tv.go();
    }
}
