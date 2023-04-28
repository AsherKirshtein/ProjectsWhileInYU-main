package edu.yu.cs.com3800.stage3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.Message.MessageType;

public class JavaRunnerFollower extends Thread
{
    ZooKeeperPeerServerImpl myServer;
    LinkedBlockingQueue<Message> work;
    InetSocketAddress leader;
    
    public JavaRunnerFollower(ZooKeeperPeerServerImpl zooKeeperPeerServerImpl, LinkedBlockingQueue<Message> incomingMessages, InetSocketAddress leader) 
    {
        setDaemon(true);
        this.myServer = zooKeeperPeerServerImpl;
        this.work = incomingMessages;
        this.leader = leader;
    }
    public void run()
    {
        while(!this.work.isEmpty())
        {
            Message job = this.work.poll();
            if(job.getMessageType() == MessageType.WORK)
            {
                JavaRunner runner = null;
                //System.out.println("Server " + myServer.getAddress() + " got WORK messasge");
                try 
                {
                    runner = new JavaRunner();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                InputStream targetStream = new ByteArrayInputStream(job.getMessageContents());
                String response = "";
                try
                {
                    //System.out.println(myServer.getAddress() + ": Compile and running target with size: " + job.getMessageContents().length);
                    response = runner.compileAndRun(targetStream);
                } 
                catch (IllegalArgumentException | IOException | ReflectiveOperationException e)
                {
                    e.printStackTrace();
                }
                System.out.println(myServer.getAddress() + ": Has COMPLETED_WORK and is now sending message to leader: " + leader);
                this.myServer.sendMessage(MessageType.COMPLETED_WORK, response.getBytes(), leader);
            }
        }
    }
}
