package edu.yu.cs.com3800.stage4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.Message.MessageType;

public class JavaRunnerFollower extends Thread
{
    private ZooKeeperPeerServerImpl myServer;
    private LinkedBlockingQueue<Message> work;
    private InetSocketAddress leader;

    private FileHandler fileHandler;
    private static Logger logger;
    
    public JavaRunnerFollower(ZooKeeperPeerServerImpl zooKeeperPeerServerImpl, LinkedBlockingQueue<Message> incomingMessages, InetSocketAddress leader) 
    {
        setDaemon(true);
        this.myServer = zooKeeperPeerServerImpl;
        this.work = incomingMessages;
        this.leader = leader;

        logger = Logger.getLogger(JavaRunnerFollower.class.getName());

        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage4/JavaRunnerFollower:"+zooKeeperPeerServerImpl.getId()+".log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }

        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter);
        logger.info("JavaRunnerFollower created");
    }
    public void run()
    {
        while(!this.isInterrupted())
        {
            
        }
    }
}
