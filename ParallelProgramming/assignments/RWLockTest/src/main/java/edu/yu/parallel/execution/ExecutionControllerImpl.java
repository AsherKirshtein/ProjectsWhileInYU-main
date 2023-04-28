package edu.yu.parallel.execution;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;

import edu.yu.parallel.RWLockInterface;

public class ExecutionControllerImpl implements ExecutionController
{
    private RWLockInterface lock;

    private Phaser phaser;

    private CountDownLatch lockPermission;
    private CountDownLatch completePermission;

    public ExecutionControllerImpl(Phaser p, RWLockInterface lock)
    {
        this.phaser = p;
        this.lock = lock;    
        this.lockPermission = new CountDownLatch(1);
        this.completePermission = new CountDownLatch(1);
        this.phaser.register();
    }

    @Override
    public void awaitForAllThreadsToHaveStarted()
    {
        this.phaser.arriveAndAwaitAdvance();
    }

    @Override
    public void awaitPermissionToLock()
    {
        // call await on one of the latches
        //The name tells you which one to call on
        try 
        {
            this.lockPermission.await();
        } 
        catch (InterruptedException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   
    }

    @Override
    public void awaitPermissionToCompleteExecution()
    {
        // same as above but with other latch
        try 
        {
            this.completePermission.await();
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }   
    }

    @Override
    public void permitLockRequest() 
    {
        // same as above but instead of calling await, call countDown
        this.lockPermission.countDown(); 
    }

    @Override
    public void completeExecution()
    {
        // same as above but instead of calling await, call countDown
        this.completePermission.countDown();
    }

    @Override
    public void lockForRead()
    {
        this.lock.lockRead();  
    }

    @Override
    public void lockForWrite()
    {
        this.lock.lockWrite();   
    }

    @Override
    public void unlock() 
    {
       this.lock.unlock(); 
    }
    
}
