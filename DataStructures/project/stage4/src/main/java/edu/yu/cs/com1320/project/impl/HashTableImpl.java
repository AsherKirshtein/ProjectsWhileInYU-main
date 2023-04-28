package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl.LinkList.Link;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> 
{
    private LinkList[] table;
    private Integer tableSize;
    private Integer maxValues;
    private Integer valuesInTable;

public HashTableImpl()
{
    this.tableSize = 5;
    this.maxValues = this.tableSize*10;
    this.valuesInTable = 0;
    LinkList list = new LinkList();
    this.table = (LinkList[]) java.lang.reflect.Array.newInstance(list.getClass(),this.tableSize);
}
private void doubleArray()
{
    this.tableSize = this.tableSize*2;
    this.maxValues = this.tableSize*10;
    LinkList listClass = new LinkList();
    LinkList[] newTable = (LinkList[]) java.lang.reflect.Array.newInstance(listClass.getClass(),this.tableSize);
    for(int i = 0; i < this.tableSize/2; i++)
    {
        Link front = this.table[i].head; 
        while(front != null)
        {
            Key key = (Key) front.data.key;
            Value value = (Value) front.data.value;
            Entry<Key,Value> entry = new Entry<Key,Value>(key, value);
            int index = hashFunction(key);
            if(newTable[index] == null)
            {
                LinkList list = new LinkList();
                newTable[index] = list;
            }
            newTable[index].addLink(entry);
            front= front.next;
        }
    }
    this.table = newTable;
    return;
}
private int hashFunction(Key key)
{
    return (key.hashCode() & 0x7fffffff) % this.tableSize;
}
@Override
public Value get(Key k) 
{   
    int index = hashFunction(k);
    if(this.table[index] == null)
    {
        return null;
    }
    Link front = this.table[index].head; 
    while(front != null)
    {
        if(front.data.key.equals(k))
        {
            return (Value) front.data.value;
        }
        front= front.next;
    }
    return null;    
}
@Override
	public Value put(Key k, Value v)
    {
        int index = hashFunction(k);
        Entry<Key,Value> entry = new Entry<Key,Value>(k, v);
        if(this.table[index] == null)
        {
            LinkList list = new LinkList();
            this.table[index] = list;
        }
        Link front = this.table[index].head; 
        while(front != null)
        {
            if(front.data.key.equals(k))
            {
                Value prev =(Value) front.data.value;
                front.data = entry;
                return prev;
            }
            front = front.next;
        }
        this.table[index].addLink(entry);
        this.valuesInTable++;
        if(this.valuesInTable > this.maxValues)
        {
            doubleArray();
        }
        return null;    
    }
class Entry<Key,Value>
{
    Key key;
    Value value;

    public Entry(Key k, Value v)
    {
        this.key = k;
        this.value = v;
    }
}

public class LinkList
{
	public class Link
	{    
        Entry<Key,Value> data;    
        Link next;            
    	public Link(Entry<Key,Value> data) 
    	{    
            this.data = data;    
            this.next = null;    
        }
        Key getKey()
        {
            return data.key;
        }
        Value getValue()
        {
            return data.value;
        }
    }
    public Link head = null;    
    public Link tail = null;

    public void addLink(Entry<Key,Value> data) 
    {      
        Link newLink = new Link(data);        
        if(head == null) 
        {        
            this.head = newLink;    
            this.tail = newLink;    
        }    
        else 
        {      
            this.tail.next = newLink;       
            this.tail = newLink;    
        }
    }          
}

}


