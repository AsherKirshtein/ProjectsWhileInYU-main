package edu.yu.cs.com1320.project.impl;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.server.ObjID;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.lang.model.element.Element;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;

/**
 * Beginnings of a MinHeap, for Stage 4 of project. Does not include the additional data structure or logic needed to reheapify an element after its last use time changes.
 * @param <E>
 */
public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> 
{
    //change back to protected after testing
    protected E[] elements;
    protected int count;
    private int arrayLength;

    public MinHeapImpl() 
    {
        this.arrayLength = 8;
        this.elements = (E[]) new Comparable[this.arrayLength];
        this.count = 0;
    }
    @Override
    public void reHeapify(E element)
    {
        int index = getArrayIndex(element);
        if(index > 1 && this.isGreater(index / 2, index))
        {
            upHeap(index);
        }
        else if(2 * index <= this.count)
        {
            downHeap(index);
        }
        return;
    }
    @Override
    public int getArrayIndex(E element)
    {
        int arrayIndex = -1;
        for(int i = 0; i < this.arrayLength; i++)
        {
            if(this.elements[i] == element)
            {
                arrayIndex = i;
                return arrayIndex;
            }
        }
        throw new NoSuchElementException();
    }
    @Override
    public void doubleArraySize()
    {
        this.arrayLength = this.arrayLength*2;
        E[] temp = (E[]) new Comparable[this.arrayLength];
        for(int i = 0; i < this.elements.length; i++)
        {
            temp[i] = this.elements[i];
        }
        this.elements = temp;

    }

    protected boolean isEmpty()
    {
        return this.count == 0;
    }

    /**
     * is elements[i] > elements[j]?
     */
    public boolean isGreater(int i, int j) { 
        return this.elements[i].compareTo(this.elements[j]) > 0;
    }

    /**
     * swap the values stored at elements[i] and elements[j]
     */
    protected void swap(int i, int j) {
        E temp = this.elements[i];
        this.elements[i] = this.elements[j];
        this.elements[j] = temp;
    }

    /**
     * while the key at index k is less than its
     * parent's key, swap its contents with its parent’s
     */
    protected void upHeap(int k) {
        while (k > 1 && this.isGreater(k / 2, k)) {
            this.swap(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * move an element down the heap until it is less than
     * both its children or is at the bottom of the heap
     */
    protected void downHeap(int k) {
        while (2 * k <= this.count) {
            //identify which of the 2 children are smaller
            int j = 2 * k;
            if (j < this.count && this.isGreater(j, j + 1)) {
                j++;
            }
            //if the current value is < the smaller child, we're done
            if (!this.isGreater(k, j)) {
                break;
            }
            //if not, swap and continue testing
            this.swap(k, j);
            k = j;
        }
    }

    public void insert(E x) {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1) {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        this.elements[++this.count] = x;
        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null; //null it to prepare for GC
        return min;
    }
    public int getLengthofElements() // delete later, just for testing
    {
        return this.elements.length;
    }
}