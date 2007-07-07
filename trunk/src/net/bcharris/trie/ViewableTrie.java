package net.bcharris.trie;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class ViewableTrie extends Trie
{
	private final TrieTreeModel treeModel;
	
	public ViewableTrie(boolean caseSensitive)
	{
		super(caseSensitive);
		treeModel = new TrieTreeModel(this);
	}
	
	@Override
	public int insert(String word)
	{
		int ret = super.insert(word);
		if (ret == 1)
		{
			treeModel.nodeStructureChanged((TreeNode)treeModel.getRoot());
		}
		return ret;
	}

	@Override
	public boolean remove(String word)
	{
		if (super.remove(word))
		{
			treeModel.nodeStructureChanged((TreeNode)treeModel.getRoot());
			return true;
		}
		
		return false;
	}
	
	public TreeModel getTreeModel()
	{
		return treeModel;
	}
}
