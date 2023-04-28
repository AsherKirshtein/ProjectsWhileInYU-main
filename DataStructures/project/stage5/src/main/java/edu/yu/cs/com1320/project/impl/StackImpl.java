package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

/**
 * @param <T>
 */
public class StackImpl<T> implements Stack<T>
{
    int stackMax;
    int top;
    T stack[];

    public StackImpl()
    {
        this.stackMax = 100;
        this.top = 0;
        this.stack = (T[]) new Object[this.stackMax];
    }
    
    public void push(T element) {
        this.stack[this.top] = element;
        this.top++;
        if(this.top == this.stackMax)
        {
            stackDouble();
        }
    }

    /**
     * removes and returns element at the top of the stack
     * 
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop() {
        if (this.top == 0) {
            return null;
        }
        T topItem = this.stack[this.top - 1];
        this.stack[this.top - 1] = null;
        this.top--;
        return topItem;
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek() {
        if (this.top == 0) {
            return null;
        }
        return this.stack[this.top - 1];
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size() {
        return this.top;
    }
    private void stackDouble()
    {
        this.stackMax = this.stackMax*2;
        T[] arrayDouble = (T[]) new Object[this.stackMax];
        for(int i =0; i < this.top; i++)
        {
            arrayDouble[i] = this.stack[i];
        }
        this.stack = arrayDouble;
        return;
    }
}