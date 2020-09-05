package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static int compareWords(String word1, String word2) //returns number of similar letters
	{
		String smaller = "";
		int count = 0;
		if (word1.length() > word2.length())
			smaller = word2;
		else
			smaller = word1;
		for (int i = 0; i < smaller.length(); i++)
		{
			if (word1.charAt(i) == word2.charAt(i))
				count++;
			else
				break;
		}
		return count;
	}
	
	public static TrieNode buildTrie(String[] allWords) 
	{
		TrieNode root = new TrieNode(null, null, null);
		root.firstChild = new TrieNode((new Indexes(0,(short) 0,(short) ((allWords[0]).length()-1))), null, null);
		TrieNode prev = root.firstChild;
		TrieNode ptr = root.firstChild;
		int start = -1;
		int end = -1;
		int same = -1;
		int wordIndex = -1;
		
		for (int i = 1; i < allWords.length; i++) // all the words that will be added to the tree
		{
			String insert = allWords[i];
			
			while (ptr != null) // length of the tree - instead of going through the original set of words, go through the actual tree
			{
				start = ptr.substr.startIndex;
				end = ptr.substr.endIndex;
				wordIndex = ptr.substr.wordIndex;
				/*String prefix = allWords[ptr.substr.wordIndex].substring(start, end+1);
				
				same = compareWords(insert, prefix) -1;*/
				int r = 0;
				while(r < allWords[wordIndex].substring(start, end+1).length() && r < insert.substring(start).length()
						&& allWords[wordIndex].substring(start, end+1).charAt(r) == insert.substring(start).charAt(r)) {
					r++;
					}
				same = (r-1);
				
				
				if (start > insert.length())
				{
					prev = ptr;
					ptr = ptr.sibling;
					continue;
				}
				if (same != -1)
					same += start;
				if(same == -1)
				{ 
					prev = ptr;
					ptr = ptr.sibling;
				}
				else
				{
					if (same < end)
					{ 
						prev = ptr;
						break;
					}
					else if(same == end)
					{ 
						prev = ptr;
						ptr = ptr.firstChild;
					}
				}
			}
			
			if (ptr == null)
			{
				Indexes indexes = new Indexes(i, (short)start, (short)(insert.length()-1));
				prev.sibling = new TrieNode(indexes, null, null);
			} 
			else
			{
				TrieNode currentFirstChild = prev.firstChild; 				
				Indexes currentIndexes = prev.substr; 
				Indexes currentWordNewIndexes = new Indexes(currentIndexes.wordIndex, (short)(same+1), currentIndexes.endIndex);
				currentIndexes.endIndex = (short)same; 
				prev.firstChild = new TrieNode(currentWordNewIndexes, null, null);
				prev.firstChild.firstChild = currentFirstChild;
				prev.firstChild.sibling = new TrieNode(new Indexes((short)i, (short)(same+1), (short)(insert.length()-1)), null, null);
			}
			
			same = start;
			start = end;
			end = wordIndex;
			wordIndex = -1;
			ptr = prev = root.firstChild;
		}
		
		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) 
	{
		if (root == null)
			return null;
		ArrayList<TrieNode> leafs = new ArrayList<>();
		TrieNode ptr = root;
		
		while (ptr != null)
		{
			if (ptr.substr == null)
				ptr = ptr.firstChild;
			String word = allWords[ptr.substr.wordIndex];
			String sub = word.substring(0, ptr.substr.endIndex+1);
			
			if (word.startsWith(prefix) || prefix.startsWith(sub))
			{
				if (ptr.firstChild != null)
				{ 
					leafs.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
				}
				else
				{ 
					leafs.add(ptr);
					ptr = ptr.sibling;
				}
			}
			else
				ptr = ptr.sibling;
		}		
		return leafs;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
