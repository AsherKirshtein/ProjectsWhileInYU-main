package edu.yu.cs.com3800.stage3;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.Message.MessageType;

public class RoundRobinLeader extends Thread
{
    private LinkedBlockingQueue <Message> work;
    private Queue<InetSocketAddress> workers;
    private ZooKeeperPeerServer leaderServer;
    private InetSocketAddress client;     
    
    public RoundRobinLeader(ZooKeeperPeerServer server, LinkedBlockingQueue<Message> incomingMessages, Map<Long, InetSocketAddress> peerIDtoAddress)
    {
        setDaemon(true);
        this.work = incomingMessages;
        this.workers = new LinkedList<>();
        this.leaderServer = server;

        for(InetSocketAddress a: peerIDtoAddress.values())
        {
            this.workers.add(a);
        }   
    }
    
    public void run()
    {
        while(!this.work.isEmpty())
        {
            Message job = this.work.poll();
            InetSocketAddress worker = this.workers.poll();
            this.workers.add(worker);
            //I remove the worker from the queue and readd it so it is now at the end of the line
            if(job.getMessageType() == MessageType.WORK)
            {
                this.leaderServer.sendMessage(MessageType.WORK, job.getMessageContents(), worker);
                int clientPort = job.getSenderPort();
                String clientString = job.getSenderHost();
                this.client = new InetSocketAddress(clientString, clientPort);
            }
            if(job.getMessageType() == MessageType.COMPLETED_WORK)
            {
                System.out.println("Leader got COMPLETED_WORK message from: " + worker);
                //put client port in message
                this.leaderServer.sendMessage(MessageType.COMPLETED_WORK, job.getMessageContents(), this.client);
            }
        }
    } 
}
