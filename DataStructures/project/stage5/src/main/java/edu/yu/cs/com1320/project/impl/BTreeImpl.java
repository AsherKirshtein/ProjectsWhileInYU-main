package edu.yu.cs.com1320.project.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    
    private static final int MAX = 4;
    private Node root; // root of the B-tree
    private Node leftMostExternalNode;
    private int height; // height of the B-tree
    private int n; // number of key-value pairs in the B-tree
    private PersistenceManager<URI,Document> pm;

    public BTreeImpl()
    {
        this.root = new Node(0);
        this.leftMostExternalNode = this.root;
        this.pm = new DocumentPersistenceManager(null);
    }


    @Override
    public Value get(Comparable key)
    {
        {
            if (key == null)
            {
                throw new IllegalArgumentException("argument to get() is null");
            }
            Entry entry = this.get(this.root, key, this.height);
            if(entry == null)
            {
                return null;
            }
            if(entry.val != null)
            {
                return (Value) entry.val;
            }
            try
            {
                URI uri = (URI) key;
                Document doc = this.pm.deserialize(uri);
                doc.setLastUseTime(System.nanoTime());
                this.put((Key)key, (Value)doc);
                return (Value) doc;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return null;
        }
    }
    private Entry get(Node currentNode, Comparable key, int height)
    {
        Entry[] entries = currentNode.entries;
        if (height == 0)
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                if(isEqual(key, entries[j].key))
                {
                    return entries[j];
                }
            }
            return null;
        }
        else
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key))
                {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            return null;
        }
    }
    private static boolean less(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) < 0;
    }

    private static boolean isEqual(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) == 0;
    }
    @Override
    public Value put(Key key, Value value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        Entry alreadyThere = this.get(this.root, key, this.height);
        if(alreadyThere != null)
        {
            Value old = (Value) alreadyThere.val;
            alreadyThere.val = value;
            return old;
        }
        Node newNode = this.put(this.root, key, (Value)value, this.height);
        this.n++;
        if(newNode == null)
        {
            return null;
        }
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        this.height++;
        if(alreadyThere == null)
        {
            return null;
        }
        return (Value) alreadyThere.val;
    }
    private Node put(Node currentNode, Comparable key, Value val, int height)
    {
        int j;
        Entry newEntry = new Entry(key, val, null);
        if (height == 0)
        {
            for (j = 0; j < currentNode.entryCount; j++)
            {
                if (less(key, currentNode.entries[j].key))
                {
                    break;
                }
            }
        }
        else
        {
            for (j = 0; j < currentNode.entryCount; j++)
            {
                if ((j + 1 == currentNode.entryCount) || less(key, currentNode.entries[j + 1].key))
                {
                    Node newNode = this.put(currentNode.entries[j++].child, key, val, height - 1);
                    if (newNode == null)
                    {
                        return null;
                    }
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        for (int i = currentNode.entryCount; i > j; i--)
        {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX)
        {
            return null;
        }
        else
        {
            return this.split(currentNode, height);
        }
    }

    private Node split(Node currentNode, int height)
    {
        Node newNode = new Node(BTreeImpl.MAX / 2);
        currentNode.entryCount = BTreeImpl.MAX / 2;
        for (int j = 0; j < BTreeImpl.MAX / 2; j++)
        {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        if (height == 0)
        {
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }

    private Entry replace(Node currentNode,Comparable key, Entry entry)
    {
        Entry[] entries = currentNode.entries;
        if (height == 0)
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                if(isEqual(key, entries[j].key))
                {
                    entries[j] = entry;
                }
            }
            return null;
        }
        else
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key))
                {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            return null;
        }
    }
    @Override
    public void moveToDisk(Comparable k) throws Exception
    {
        Value doc =  get(k);
        Document d = (Document) doc;
        URI uri = (URI) k;
        this.pm.serialize(uri, d);
        delete((Key)k);    
    }

    @Override
    public void setPersistenceManager(PersistenceManager pm) 
    {
        this.pm = pm;
    }
    public boolean isEmpty()
    {
        return this.size() == 0;
    }
    public int size()
    {
        return this.n;
    }
    public void delete(Key key)
    {
        put(key, null);
    }
    private static final class Node
    {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children
        private Node next;
        private Node previous;

        // create a node with k entries
        private Node(int k)
        {
            this.entryCount = k;
        }

        private void setNext(Node next) {
            this.next = next;
        }

        private Node getNext() {
            return this.next;
        }

        private void setPrevious(Node previous) {
            this.previous = previous;
        }

        private Node getPrevious() {
            return this.previous;
        }

        private Entry[] getEntries() {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    public static class Entry 
    {
        private Comparable key;
        private Object val;
        private Node child;

        public Entry(Comparable key, Object val, Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }

        public Object getValue() {
            return this.val;
        }

        public Comparable getKey() {
            return this.key;
        }
    }
}
