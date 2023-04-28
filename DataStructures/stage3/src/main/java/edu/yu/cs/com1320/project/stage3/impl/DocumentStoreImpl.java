package edu.yu.cs.com1320.project.stage3.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

public class DocumentStoreImpl implements DocumentStore
{
    HashTable<URI,Document> table = new HashTableImpl<>();
    Stack<Command> cmdStack = new StackImpl<>();

    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException 
    {
        Document old = getDocument(uri);
        Function<URI,Boolean> undoPut = (URI u) -> 
        {
            this.table.put(uri, old);
            return true;
        };
        Command cmd = new Command(uri, undoPut);
        cmdStack.push(cmd);
        Document document = null;
        if(input == null)
        {
            Document prev = this.table.put(uri,null);
            if(prev == null)
            {
                return 0;
            }
            return prev.hashCode();  
        }
        switch(format)
        {
            case BINARY: byte[] bytes = readInput(input);
            document = new DocumentImpl(uri, bytes);
            break;
            case TXT: byte[] temp = readInput(input);
            String text = new String(temp);
            document = new DocumentImpl(uri, text);
            break;
        }
        Document prev = this.table.put(uri,document);
        if(prev == null)
        {
            return 0;
        }
        return prev.hashCode();
    }
    public Document getDocument(URI uri) 
    {   
       return this.table.get(uri);
    }
    public boolean deleteDocument(URI uri)
    {
        Document old = getDocument(uri);
        Function<URI,Boolean> undoDelete = (URI u) ->
        {
            this.table.put(uri,old);
            return true;
        };
        Command cmd = new Command(uri, undoDelete);
        cmdStack.push(cmd);
        if(this.table.get(uri) == null)
        {
            return false;
        }
        this.table.put(uri,null);
        return true;
    }

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException
    {
        if(this.cmdStack.size() == 0)
        {
            throw new IllegalStateException();
        }
        Command command = this.cmdStack.pop();
        command.undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException
    {
        Stack<Command> tempStack = new StackImpl<>();
        while(this.cmdStack.size() != 0)
        {
            if(this.cmdStack.peek().getUri() == uri)
            {
                Command command = this.cmdStack.pop();
                command.undo();
                while(tempStack.size() != 0)
                {
                    Command cmd = tempStack.pop();
                    this.cmdStack.push(cmd);
                }
                return;
            }
            Command command = this.cmdStack.pop();
            tempStack.push(command);
        }
        while(tempStack.size() != 0) //Runs before the Illegal state to restack the stack before returning with an exception
                {
                    Command cmd = tempStack.pop();
                    this.cmdStack.push(cmd);
                }
        throw new IllegalStateException();
    }
    private byte[] readInput(InputStream input)
    {
        
        byte[] bytes;
        try 
        {
            bytes = input.readAllBytes();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return null;
        }
        return bytes;
    }
}