package edu.yu.parallel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class RWLock implements RWLockInterface
{

  private Queue<Thread> lockRequesters = new LinkedList<Thread>();
  private List<Thread> writers = new ArrayList<>();
  private Boolean writingLock = false;

  @Override
  public void lockRead() 
  {  
    synchronized(this)
    { 
      Thread reader = Thread.currentThread();
      System.out.println("Thread: " + reader.getName() + " is added to the queue");
      lockRequesters.add(reader);
      while(writingLock)
      {
        try 
        {
          System.out.println("Thread: " + reader.getName() + " is blocked");
          this.wait();
        } 
        catch (InterruptedException e) 
        {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void lockWrite()
  {
    synchronized(this)
    {
      Thread writer = Thread.currentThread();
      lockRequesters.add(writer);
      writers.add(writer);
      while(writingLock || lockRequesters.peek() != writer)
      {
        try 
        {
          System.out.println("Thread: " + writer.getName() + " is blocked");
          this.wait();
        } 
        catch (InterruptedException e) 
        {
          e.printStackTrace();
        }
      }
      writingLock = true;
    }
  }

  @Override
  public void unlock() throws IllegalMonitorStateException
  {
    synchronized(this)
    {
      if(lockRequesters.isEmpty())
      {
        throw new IllegalMonitorStateException();
      }  
      Thread lockOwner = lockRequesters.remove();
      if(writers.contains(lockOwner))
      {
        writers.remove(lockOwner);
        writingLock = false;
      }        
      notifyAll();  
    }
  }

} // class
