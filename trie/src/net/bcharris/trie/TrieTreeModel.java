package net.bcharris.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


// A TreeModel for a Trie
class TrieTreeModel extends DefaultTreeModel
{
	TrieTreeModel(TrieImpl trie)
	{
		super(TrieTreeNode.newInstance(null, trie.root));
	}
}

// A TreeNode for a TrieNode
class TrieTreeNode implements TreeNode, Comparable<TrieTreeNode>
{
	final TrieTreeNode parent;
	final TrieNode trieNode;

	// flyweight design pattern
	private static final Map<TrieNode, TrieTreeNode> tNodeMapping = new HashMap<TrieNode, TrieTreeNode>();
	
	private TrieTreeNode(TrieTreeNode parent, TrieNode trieNode)
	{
		this.parent = parent;
		this.trieNode = trieNode;
	}

	public static TrieTreeNode newInstance(TrieTreeNode parent, TrieNode trieNode)
	{
		if (tNodeMapping.containsKey(trieNode))
			return tNodeMapping.get(trieNode);
		else
		{
			TrieTreeNode trieTreeNode = new TrieTreeNode(parent, trieNode);
			tNodeMapping.put(trieNode, trieTreeNode);
			return trieTreeNode;
		}
	}
	
	public int compareTo(TrieTreeNode o)
	{
		return trieNode.c - o.trieNode.c;
	}
	
	public Enumeration children()
	{
		if (trieNode.children == null)
			return Collections.enumeration(Collections.EMPTY_SET);
		
		Set<Entry<Character, TrieNode>> entries = trieNode.children.entrySet();
		List<TrieTreeNode> trieTreeNodes = new ArrayList<TrieTreeNode>(entries.size());
		
		for (Entry<Character, TrieNode> entry : entries)
		{
			trieTreeNodes.add(newInstance(this, entry.getValue()));
		}

		Collections.sort(trieTreeNodes);
		
		return Collections.enumeration(trieTreeNodes);
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int childIndex)
	{
		if (trieNode.children == null)
			return null;
		
		Enumeration e = children();
		int i=0;
		while (e.hasMoreElements())
		{
			if (i++ == childIndex)
				return (TreeNode)e.nextElement();
			else
				e.nextElement();
		}
		
		return null;
	}

	public int getChildCount()
	{
		if (trieNode.children == null)
			return 0;
		
		return trieNode.children.entrySet().size();
	}

	public int getIndex(TreeNode node)
	{
		Enumeration e = children();
		int i=0;
		while (e.hasMoreElements())
		{
			TreeNode treeNode = (TreeNode)e.nextElement();
			if (treeNode.equals(node))
				return i;
			
			i++;
		}
		return -1;
	}

	public TreeNode getParent()
	{
		return parent;
	}

	public boolean isLeaf()
	{
		return getChildCount() == 0;
	}

	private String getWord()
	{
		if (parent == null)
			return "";
		
		return parent.getWord() + trieNode.c;
	}
	
	@Override
	public String toString()
	{
		return getWord();
	}
}