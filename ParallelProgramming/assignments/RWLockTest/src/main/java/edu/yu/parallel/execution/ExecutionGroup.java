package edu.yu.parallel.execution;

import java.util.ArrayList;
import java.util.concurrent.Phaser;

import edu.yu.parallel.RWLockInterface;

public class ExecutionGroup<T> extends AbstractExecutionGroup<T> 
{

    private ArrayList<ExecutionController> controllers = new ArrayList<>();

    private boolean threadsAreReadyToLock = false;
    private RWLockInterface rwLock;
    private Phaser phaser;
    
    public ExecutionGroup(RWLockInterface rwLock, long defaultWaitTime)
    {
        super(rwLock, defaultWaitTime);
        this.rwLock = rwLock;
        this.phaser = new Phaser(1);
    }

    protected ExecutionController newExecutionController()
    {
        ExecutionController controller = new ExecutionControllerImpl(this.phaser, this.rwLock);
        this.controllers.add(controller);
        return controller;
    }

    public void awaitReadyToLock()
    {
        // loop over something and use await for all threads to have started in the loop
        controllers.get(0).awaitForAllThreadsToHaveStarted();
        this.threadsAreReadyToLock = true;
    }

    protected boolean threadsAreReadyToLock()
    {
        return threadsAreReadyToLock;
    }

}
