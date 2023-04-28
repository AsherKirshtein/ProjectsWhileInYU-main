package edu.yu.cs.com1320.project.stage5.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.impl.BTreeImpl;

public class DocumentStoreImpl implements DocumentStore
{
    Stack<Undoable> cmdStack = new StackImpl<>();
    Trie<Document> trie = new TrieImpl<>();
    MinHeap<Document> heap = new MinHeapImpl<Document>();
    BTree<URI,Document> bTree = new BTreeImpl<URI,Document>(); 
    private Integer documentLimit = Integer.MAX_VALUE;
    private Integer byteLimit = Integer.MAX_VALUE;
    private Integer documentsInStore = 0;
    private Integer bytesInStore = 0;

    public int putDocument(InputStream input, URI uri, DocumentFormat format)
    {
        if(input == null)
        {
            return 0; 
        }
        Document old = getDocument(uri);
        removeFromTrie(old);
        if(old != null)
        {
            this.documentsInStore--;
        }
        Document document = null;
        switch(format)
        {
            case BINARY: byte[] bytes = readInput(input);
            document = new DocumentImpl(uri, bytes);
            this.bytesInStore+= bytes.length;
            this.documentsInStore++;
            break;
            case TXT: byte[] temp = readInput(input);
            String text = new String(temp);
            document = new DocumentImpl(uri, text);
            this.documentsInStore++;
            this.bytesInStore+= text.getBytes().length;
            break;
        }
        Document doc = document;
        Function<URI,Boolean> undoPut = (URI u) -> 
        {
            removeFromTrie(doc);
            this.heap.remove();
            this.bTree.put(uri, old);
            switch(format)
            {
                case BINARY: this.bytesInStore -= doc.getDocumentBinaryData().length;
                this.documentsInStore--;
                break;
                case TXT: this.bytesInStore -= doc.getDocumentTxt().getBytes().length;
                this.documentsInStore--;
            }
            return true;
        };
        GenericCommand<Target> cmd = new GenericCommand(uri, undoPut);
        this.cmdStack.push(cmd);
        addToTrie(document);
        document.setLastUseTime(System.nanoTime());
        this.heap.insert(document);
        this.bTree.put(uri, doc);
        while(isFullStore()) //will need to add some value to the bTree indicating the doc is going to disk
        {
            Document deleteME = this.heap.remove();
            URI uriDelete = deleteME.getKey();
            deleteDocument(uriDelete);
            try {
                this.bTree.moveToDisk(uriDelete);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.documentsInStore--;
            switch (format) 
            {
                case TXT:
                this.bytesInStore -= deleteME.getDocumentTxt().getBytes().length;
                break;
                case BINARY:
                this.bytesInStore-= deleteME.getDocumentBinaryData().length;
                break;
            }
        }
        if(old == null)
        {
            return 0;
        }
        return old.hashCode();
    }
    public Document getDocument(URI uri) 
    {   
        Document document = this.bTree.get(uri);
        if(document !=null)
        {
            document.setLastUseTime(System.nanoTime());
        }   
        return document;   
        
    }
    public boolean deleteDocument(URI uri)
    {
        Document old = getDocument(uri);
        if(old == null)
        {
            return false;
        }
        Function<URI,Boolean> undoDelete = (URI u) ->
        {
            addToTrie(old);
            this.heap.insert(old);
            this.bTree.put(uri, old);
            if(old.getDocumentBinaryData() == null)
            {
                this.bytesInStore += old.getDocumentTxt().getBytes().length;
                this.documentsInStore++;
            }
            else
            {
                this.bytesInStore += old.getDocumentBinaryData().length;
                this.documentsInStore++;
            }
            return true;
        };
        GenericCommand<Target> cmd = new GenericCommand(uri, undoDelete);
        cmdStack.push(cmd); 
        try
        {
            String [] wordsInDoc = old.getDocumentTxt().split(" ");
            for(int i = 0; i < wordsInDoc.length; i++)
            {
                this.trie.delete(wordsInDoc[i], old);
            }
        }
        catch(NullPointerException e)
        {

        }    
        removeFromHeap(uri);
        this.bTree.put(uri, null);
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
        Undoable command = this.cmdStack.pop();
        command.undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException
    {
        Stack<Undoable> temp = new StackImpl<>();
        boolean uriInStack = false; 
        while(this.cmdStack.size() != 0)
        {            
            if(this.cmdStack.peek() instanceof GenericCommand)
            {     
                GenericCommand<URI> command = (GenericCommand) this.cmdStack.peek();
                if(command.getTarget().equals(uri))
                {
                    Undoable com = this.cmdStack.pop();
                    com.undo();
                    uriInStack = true;
                }  
            }
            else if(this.cmdStack.peek() instanceof CommandSet)
            {
                CommandSet<URI> commands =  (CommandSet) this.cmdStack.peek();
                if(commands.containsTarget(uri))
                {
                    uriInStack = true;
                }
            }
            Undoable tempVar = this.cmdStack.pop();
            temp.push(tempVar);
        }
        while(temp.size() != 0)
        {
            Undoable cmd = temp.pop();
            this.cmdStack.push(cmd);
        }
        if(uriInStack == false)
        {
            throw new IllegalStateException();
        }
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
    private void addToTrie(Document document)
    {
        for(String word: document.getWords())
        {
            this.trie.put(word.toLowerCase(), document);
        }
        return;
    }
    private void removeFromTrie(Document document) 
    {
        if(document == null)
        {
            return;
        }
        for(String word: document.getWords())
        {
            this.trie.delete(word.toLowerCase(), document);
        }
        return;
    }
    @Override
    public List<Document> search(String keyword) 
    {
        Comparator<Document> comparator = (Document d1, Document d2) ->
        {
            DocumentImpl doc1 = (DocumentImpl) d1;
            DocumentImpl doc2 = (DocumentImpl) d2;

            return doc2.wordCount(keyword) - doc1.wordCount(keyword); 
        }; 
        return this.trie.getAllSorted(keyword.toLowerCase(), comparator);
    }
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) 
    {
        Comparator<Document> comparator = (Document d1, Document d2) ->
        {
            DocumentImpl doc1 = (DocumentImpl) d1;
            DocumentImpl doc2 = (DocumentImpl) d2;

            return doc2.wordCount(keywordPrefix) - doc1.wordCount(keywordPrefix); 
        }; 
        return this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
    }
    @Override
    public Set<URI> deleteAll(String keyword)
    {
        Set<URI> uriSet = new HashSet<>();
        Set<Document> docSet = this.trie.deleteAll(keyword.toLowerCase());
        CommandSet<URI> commands = new CommandSet<>();
        for(Document d: docSet)
        {
            URI uri = d.getKey();
            Function<URI,Boolean> undoDelete = (URI u) -> 
            {
                addToTrie(d);
                this.heap.insert(d);
                this.bTree.put(uri, d);
                if(getDocument(uri).getDocumentBinaryData() == null)
            {
                this.bytesInStore += getDocument(uri).getDocumentTxt().getBytes().length;
                this.documentsInStore++;
            }
            else
            {
                this.bytesInStore += getDocument(uri).getDocumentBinaryData().length;
                this.documentsInStore++;
            }
                return true;
            };
            GenericCommand<URI> command = new GenericCommand<URI>(uri, undoDelete);
            uriSet.add(uri);
            removeFromHeap(uri);
            this.bTree.put(uri,null);
            commands.addCommand(command);
        }
        this.cmdStack.push(commands);
        return uriSet;
    }
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) 
    {
        Set<URI> uriSet = new HashSet<>();
        CommandSet<URI> commands = new CommandSet<>();
        Set<Document> docSet = this.trie.deleteAllWithPrefix(keywordPrefix);
        for(Document d: docSet)
        {
            URI uri = d.getKey();
            Function<URI,Boolean> undoDelete = (URI u) -> 
            {
                addToTrie(d);
                this.heap.insert(d);
                this.bTree.put(uri, d);
                if(getDocument(uri).getDocumentBinaryData() == null)
                {
                    this.bytesInStore += getDocument(uri).getDocumentTxt().getBytes().length;
                    this.documentsInStore++;
                }
                else
                {
                    this.bytesInStore += getDocument(uri).getDocumentBinaryData().length;
                    this.documentsInStore++;
                }
                return true;
            };
            GenericCommand<URI> command = new GenericCommand<URI>(uri, undoDelete);
            uriSet.add(uri);
            removeFromHeap(uri);
            this.bTree.put(uri, null);
            commands.addCommand(command);
        }
        return uriSet;
    }
    @Override
    public void setMaxDocumentCount(int limit) {
        this.documentLimit = limit;
        while(isFullStore())
        {
            Document deleteME = this.heap.remove();
            URI uriDelete = deleteME.getKey();
            deleteDocument(uriDelete);
            this.documentsInStore--;
            if(deleteME.getDocumentBinaryData() == null)
            {
                this.bytesInStore-= deleteME.getDocumentTxt().getBytes().length;

            }
            else{
                this.bytesInStore-= deleteME.getDocumentBinaryData().length;
            }
        }
        
    }
    @Override
    public void setMaxDocumentBytes(int limit) {
        this.byteLimit = limit;
        while(isFullStore())
        {
            Document deleteME = this.heap.remove();
            URI uriDelete = deleteME.getKey();
            deleteDocument(uriDelete);
            this.documentsInStore--;
            if(deleteME.getDocumentBinaryData() == null)
            {
                this.bytesInStore-= deleteME.getDocumentTxt().getBytes().length;

            }
            else
            {
                this.bytesInStore-= deleteME.getDocumentBinaryData().length;
            }
        }
        
    }
    private boolean isFullStore()
    {
        if(this.bytesInStore > this.byteLimit || this.documentsInStore > this.documentLimit)
        {
            return true;
        }
        return false;
    }
    private void removeFromHeap(URI uri)
    {
        Stack<Document> stk = new StackImpl<>();
        Document desiredDoc = getDocument(uri);
        boolean exception = false;
        while(exception = false)
        {
            try
            {
                Document document = this.heap.remove();
                if(!document.equals(desiredDoc))
                {
                    stk.push(document);
                }
            }
            catch(NoSuchElementException e)
            {
                exception = true;
            }
        }
        for(int i = 0; i < stk.size(); i++)
        {
            Document d = stk.pop();
            this.heap.insert(d);
        }
    }
}