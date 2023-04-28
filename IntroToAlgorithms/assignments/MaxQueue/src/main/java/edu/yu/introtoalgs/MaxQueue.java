package edu.yu.introtoalgs;

/** Enhances the Queue enqueue() and dequeue() API with a O(1) max()
 * method and O(1) size().  The dequeue() method is amortized O(1),
 * enqueue() is amortized O(1).  The implementation is O(n) in space.
 *
 * 
 */

import java.util.NoSuchElementException;

public class MaxQueue 
{
        private int queueSize;
        private int front;
        private int index;
        private int maxFront;

        public Integer[] queue;
        private Integer[] maxValues;

        public MaxQueue() 
        {
            this.front = 0;
            this.queueSize = 0;
            this.index = 0;
            this.maxFront = 0;

            this.queue = new Integer[10];
            this.maxValues = new Integer[10];
        }

        
        //Check if we are out of space(if so double the array)
        //Check if our new value is our max (if so set to our new max)
        //add to the queue
        
        public void enqueue(int x) 
        {
            if(this.queueSize == this.queue.length-1)
            {
                doubleQueue();
            }
        
            int position = (this.front + this.queueSize) % queue.length; 
            queue[position] = x;
            this.queueSize++;

            if(index == this.maxValues.length-2)
            {
                doubleMax();
            }
            //Check to see if this is the first value being inserted
            if(this.maxValues[index] == null)
            {
                this.maxValues[index] = x;
            }
            //Check to see if this value is greater than the one to the left of it
            //If it is greater we can ignore all of the values that are less than it and to its left 
            if(this.maxValues[index] < x)
            {
                while(this.maxValues[index] != null && this.maxValues[index] < x && index > 0)
                {
                    this.maxValues[index] = null;
                    this.index--;
                }
                //This if statement is needed for a case when our value is the 2nd highest value in the max Array;
                //Without this if statement it would overwrite the max value since the index !>0;
                if(this.maxValues[index] < x && index == 0)
                {
                    index--;
                }

                index++;
                this.maxValues[index] = x;
            }
            //If its less than the value to the left put it on the right
            if(this.maxValues[index] > x)
            {
                index++;
                this.maxValues[index] = x;
            }

            
        }

        //Take out the item at the top of the queue
        //if we take out the max find new max

        public int dequeue() 
        {
            if(this.queueSize == 0)
            {
                throw new NoSuchElementException();
            }
            int value = this.queue[this.front];
            queue[this.front] = null;
            this.front= (this.front + 1) % queue.length;
            this.queueSize--;
            
            //If we remove our max go over to the right to get our next value
            if(value == max())
            {
                this.maxFront++;
            }   

            return value;
            
        }

        /** Returns the number of elements in the queue
         *
         * @return number of elements in the queue
         */
        public int size() 
        {
            return this.queueSize;
        }


        // Returns the element with the maximum value
        public int max() 
        {
            if(this.queueSize == 0)
            {
                throw new NoSuchElementException();
            }
            int max = this.maxValues[this.maxFront];
            return max;

        }

        //Doubles the array length and makes a copy. O(n) time 
        private void doubleQueue()
        {
            Integer[] copy = new Integer[this.queue.length*2];
            for(int i = 0; i < this.queueSize; i++)
            {
                copy[i] = this.queue[i];
            }
            this.queue = copy;
        }

        private void doubleMax()
        {
            Integer[] copy = new Integer[this.maxValues.length*2];
            for(int i = 0; i < this.maxValues.length; i++)
            {
                copy[i] = this.maxValues[i];
            }
            this.queue = copy;
        }
  
}