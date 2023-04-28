package edu.yu.cs.com1320.project.stage5.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document>
{
    File baseDir;
    
    public DocumentPersistenceManager(File baseDir)
    {
        if(baseDir == null)
        {
            File file = new File(System.getProperty("user.dir"));
            this.baseDir = file;
        }
        else
        {
            this.baseDir = baseDir;
        }
        
    }
    //serializes the doc to a string and saves it onto the disk
    //Don't forget to transient time
    @Override
    public void serialize(URI uri, Document val) throws IOException 
    {
        String path = uri.getAuthority();
        path += uri.getPath();
        path = path.replace("http://", "");
        path = path.replace("/", File.separator);
        path = this.baseDir + File.separator + path + ".json";
        //System.out.println("Path: "+ path);
        File file = new File(path);
        //System.out.println("File: " + file);
        File parent = new File(file.getParent());
        //System.out.println("Parent: "+ parent);
        parent.mkdirs();
        Gson gson = new Gson();
        String jsonString = "";
        if(val.getDocumentTxt() == null)
        {
            jsonString = gson.toJson(val);
        }
        else
        {
            jsonString = gson.toJson(val);//Where do I declare time count as transient
        }
        FileWriter writer = new FileWriter(path);
        writer.write(jsonString);
        writer.close();
        //System.out.println(jsonString);
    }
    @Override
    public Document deserialize(URI uri) throws IOException
    {
        Gson gson = new Gson();
        String path = uri.getAuthority();
        path += uri.getPath();
        path = path.replace("http://", "");
        path = path.replace("/", File.separator);
        path = this.baseDir + File.separator + path + ".json";
        File file = new File(path);
        FileReader reader = new FileReader(file);
        Document doc = gson.fromJson(reader, DocumentImpl.class);
        reader.close();
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException 
    {  
        String path = this.baseDir + File.separator +uri.toString();
        path = path.replace("http://", "");
        path = path.replace("/", File.separator);
        path += ".json";        
        File file  = new File(path);
        //System.out.println(file);
        if(file.delete())
        {
            return true;
        }
        return false;
    }
}
