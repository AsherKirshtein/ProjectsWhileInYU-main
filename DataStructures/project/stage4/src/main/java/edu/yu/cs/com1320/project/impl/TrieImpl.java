package edu.yu.cs.com1320.project.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> 
{
    final static int alphabetSize = 256;
    protected Node<Value> root;

    public TrieImpl()
    {
        this.root = new Node<Value>();
    }
    public void put(String key, Value val)
    {
        if(key == null)
        {
            throw new IllegalArgumentException();
        }
        if (val == null)
        {
            return;
        }
        String realKey = key.toLowerCase();
        if(inTrie(realKey, val))
        {
            return;
        }
        Node<Value> current = this.root;
        for(int treeLevel = 0; treeLevel < realKey.length(); treeLevel++)
        {
            int index = realKey.charAt(treeLevel);
            if(current.children[index] == null)
            {
                current.children[index] = new Node<Value>(); 
            }
            current = current.children[index]; 
        }
        current.isLeaf = true;
        current.values.add(val);
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) //need to figure out how to order
    {
        if(key == null || comparator == null)
        {
            throw new IllegalArgumentException();
        }
        String lowerKey = key.toLowerCase();
        Node<Value> node = getNode(lowerKey);
        List<Value> list = new ArrayList<>();
        if(node == null)
        {
            return list;
        }
        list = node.values;
        Collections.sort(list, comparator);
        return list;  
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator)
    {
        if(prefix == null || comparator == null)
        {
            throw new IllegalArgumentException();
        }
        if(prefix == "")
        {
            return new ArrayList<>();
        }
        String lowerPrefix = prefix.toLowerCase();
        List<Value> list = new ArrayList<>();
        Set<Value> set = getAllWithPrefix(lowerPrefix);
        list.addAll(set);
        Collections.sort(list, comparator);
        return list;    
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix)
    {
        if(prefix == null)
        {
            throw new IllegalArgumentException();
        }
        if(prefix == "")
        {
            return new HashSet<>();
        }
        String lowerPrefix = prefix.toLowerCase();
        Set<Value> set = getAllWithPrefix(lowerPrefix);
        deleteWithPrefix(lowerPrefix);
        return set;
    }
    public Set<Value> deleteAll(String key)
    {
        if(key == null)
        {
            throw new IllegalArgumentException();
        }
        String lowKey = key.toLowerCase();
        Node<Value> node = getNode(lowKey);
        Set<Value> set = new HashSet<>();
        if(node == null)
        {
            return set;
        }
        List<Value> list = node.values;
        if(node.isLeaf == false)
        {
            return set;
        }
        for(Value v: list)
        {
            set.add(v);
        }
        List<Value> newNodeValues = new ArrayList<Value>();
        node.values = newNodeValues;
        node.isLeaf = false;
        return set;
    }

    public Value delete(String key, Value val)
    {
        if(key == null || val == null)
        {
            throw new IllegalArgumentException();
        }
        String lowKey = key.toLowerCase();
        Node<Value> current  = getNode(lowKey);
        if(!inTrie(lowKey, val))
        {
            return null;
        }
        current.values.remove(val);
        return val;
    }
    private Set<Value> getAllWithPrefix(String key)
    {
        String lowKey = key.toLowerCase();
        Set<Value> set = new HashSet<>();
        Node<Value> current = getNode(lowKey);
        if(current == null)
        {
            return set;
        }
        set.addAll(current.values);
        for(int i = 0; i < this.alphabetSize; i++)
        {
            if(current.children[i] != null)
            {
                char nodeChar = (char) i;
                String newKey = lowKey + nodeChar;
                if(getNode(newKey).isLeaf == true)
                {
                    set.addAll(getNode(newKey).values);
                }
                set.addAll(getAllWithPrefix(newKey));
            }
        }
        return set;
    }
    private void deleteWithPrefix(String prefix)
    {
        String lowPrefix = prefix.toLowerCase();
        Node<Value> current = getNode(lowPrefix);
        for(int index = 0; index < this.alphabetSize; index++)
        {
            current.children[index] = null;
        }
        deleteAll(prefix);
    }
    private boolean inTrie(String key, Value value)
    {
        String lowKey = key.toLowerCase();
        Node<Value> current = getNode(lowKey);
        if(current == null)
        {
            return false;
        }
        List<Value> values = current.values;
        for(Value v: values)
        {
            if(v.equals(value))
            {
                return true;
            }
        }
        return false;
    }
    private Node<Value> getNode(String key)
    {
        String lowKey = key.toLowerCase();
        Node<Value> current = this.root;
        for(int treeLevel = 0; treeLevel < lowKey.length(); treeLevel++)
        {
            int index = lowKey.charAt(treeLevel);
            if(current.children[index] == null)
            {
                return null; 
            }
            current = current.children[index]; 
        }
        return current;
    }   
    public class Node<Value>
    {
        protected boolean isLeaf;
        protected List<Value> values;
        protected Node<Value>[] children;

        Node()
        {
            isLeaf = false;
            values = new ArrayList<Value>();
            children = new Node[TrieImpl.alphabetSize];
        }
    }
}