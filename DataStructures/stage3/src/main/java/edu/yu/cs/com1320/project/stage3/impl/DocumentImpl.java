package edu.yu.cs.com1320.project.stage3.impl;

import java.net.URI;
import java.util.Arrays;

import edu.yu.cs.com1320.project.stage3.Document;

public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;

    public DocumentImpl(URI uri, String txt)
    {
        if(uri == null || uri.toString() == "" || txt == null || txt =="")
        {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
    }
    public DocumentImpl(URI uri, byte[] binaryData)
    {
        if(uri == null || uri.toString() == "" || binaryData == null || binaryData.length < 1) 
        {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }
    
    public String getDocumentTxt() {
        return this.txt;
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

}