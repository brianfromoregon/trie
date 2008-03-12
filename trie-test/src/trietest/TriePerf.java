package trietest;

import java.util.Random;
import net.bcharris.trie.Trie;
import net.bcharris.trie.TrieImpl;

/**
 *
 * @author brian
 */
public class TriePerf {
    public static void main(String[] args)
    {
        Trie trie = new TrieImpl(true);
        populate(trie, 50000);
        bestMatch(trie, 10);
    }
    
    public static void populate(Trie trie, int numWords)
    {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numWords; i++)
        {
            sb.setLength(0);
            int u = r.nextInt(15)+1;
            for (int j=0; j<u; j++)
            {
                sb.append(randAlpha(r));
            }
            trie.insert(sb.toString());
        }
    }
    
    public static void bestMatch(Trie trie, int numWords)
    {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numWords; i++)
        {
            sb.setLength(0);
            int u = r.nextInt(15)+1;
            for (int j=0; j<u; j++)
            {
                sb.append(randAlpha(r));
            }
            trie.bestMatch(sb.toString(), Long.MAX_VALUE);
        }
    }
    
    private static char randAlpha(Random r)
    {
        return (char)(r.nextInt('z'-'a') + 'a');
    }
}
