package edu.yu.cs.com1320.project.stage4.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.yu.cs.com1320.project.stage4.Document;

public class DocumentImpl implements Document{
    private URI uri;
    private String txt;
    private String ogText;
    private byte[] binaryData;
    private long lastUseTime;
    public HashMap<String,Integer> words;

    public DocumentImpl(URI uri, String txt)
    {
        if(uri == null || uri.toString() == "" || txt == null || txt =="")
        {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.ogText = txt;
        this.txt = txt.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        this.txt = this.txt.trim().replaceAll(" +", " ");
        this.words = new HashMap<String,Integer>();
        mapAdd();
        setLastUseTime(System.nanoTime());
    }
    public DocumentImpl(URI uri, byte[] binaryData)
    {
        if(uri == null || uri.toString() == "" || binaryData == null || binaryData.length < 1) 
        {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.words = new HashMap<String,Integer>();
        mapAdd();
        setLastUseTime(System.nanoTime());
    }
    
    public String getDocumentTxt() {
        return this.ogText;
    }
    
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }
    
    public URI getKey() {
        return this.uri;
    }
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof Document))
        {
            return false;
        }
        Document document = (Document) o;
        if(this.uri.hashCode() == document.getKey().hashCode())
        {
            return true;
        }
        return false; 
    }
    @Override
    public int hashCode() {
    int result = uri.hashCode();
    result = 31 * result + (this.txt != null ? this.txt.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(binaryData);
    return result;
    }
    @Override
    public int wordCount(String word) 
    {
        if(this.txt == null || word == "")
        {
            return 0;
        }
        int wordcount = 0;
        word = word.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String [] words = this.txt.split(" ");
        for(int i = 0; i < words.length; i++)
        {
            if(words[i].equalsIgnoreCase(word))
            {
                wordcount++;
            }
        }
        return wordcount;
    }
    @Override
    public Set<String> getWords() 
    {
        if(this.txt == null)
        {
            return new HashSet<>();
        }
        Set<String> wordsInDoc = new HashSet<>();
        String [] words = this.txt.split(" ");
        for(int i = 0; i < words.length; i++)
        {
            if(wordsInDoc.contains(words[i].toLowerCase()) == false)
            {
                wordsInDoc.add(words[i].toLowerCase());
            }
        }
        return wordsInDoc;
    }
    private void mapAdd()
    {
        Set<String> words = getWords();
        for(String w: words)
        {
            int wordcount = wordCount(w);
            this.words.put(w, wordcount);
        }
    }
    @Override
    public int compareTo(Document o)
    {
            long ourDoc = getLastUseTime();
            long otherDoc = o.getLastUseTime();
            if(ourDoc > otherDoc) //ourDoc is more recently used
            {
                return 1;
            }
            if(otherDoc > ourDoc)
            {
                return -1;
            }
            else
            {
                return 0;
            }
    }
    @Override
    public long getLastUseTime() {
        return this.lastUseTime;
    }
    @Override
    public void setLastUseTime(long timeInNanoseconds)
    {
        this.lastUseTime = timeInNanoseconds;  
    }
}