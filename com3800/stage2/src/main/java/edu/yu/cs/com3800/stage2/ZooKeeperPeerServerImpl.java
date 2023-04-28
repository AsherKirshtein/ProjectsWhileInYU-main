package edu.yu.cs.com3800.stage2;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.Message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class ZooKeeperPeerServerImpl extends Thread implements ZooKeeperPeerServer
{
    private final InetSocketAddress myAddress;
    private final int myPort;
    private ServerState state;
    private volatile boolean shutdown;

    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;

    private Long id;
    private long peerEpoch;
    private volatile Vote currentLeader;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private UDPMessageSender senderWorker;
    private UDPMessageReceiver receiverWorker;

    public ZooKeeperPeerServerImpl(int myPort, long peerEpoch, Long id, Map<Long,InetSocketAddress> peerIDtoAddress)
    {
        this.myPort = myPort;
        this.myAddress = new InetSocketAddress("localhost", myPort);

        this.peerEpoch = peerEpoch;
        this.id = id;
        this.peerIDtoAddress = peerIDtoAddress;
        this.state = ServerState.LOOKING;
        this.shutdown = false;
        this.senderWorker = null;
        this.receiverWorker = null;
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outgoingMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public void shutdown()
    {
        this.shutdown = true;
        this.senderWorker.shutdown();
        this.receiverWorker.shutdown();
    }

    @Override
    public void run()
    {
       
        try {
            this.senderWorker = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 1: create and run thread that sends broadcast messages
            this.senderWorker.start();
            this.receiverWorker = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, this);
            //step 2: create and run thread that listens for messages sent to this server
            this.receiverWorker.start();
        } 
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //step 3: main server loop
        try
        {
            while (!this.shutdown)
            {
                switch (getPeerState())
                {
                    case LOOKING:
                        //start leader election, set leader to the election winner
                        ZooKeeperLeaderElection election = new ZooKeeperLeaderElection(this, this.incomingMessages);
                        //System.out.println("Looking for leader");
                        this.currentLeader = election.lookForLeader();
                        setCurrentLeader(this.currentLeader);
                }
            }
        }
        catch (Exception e)
        {
           
        }
    }

    @Override
    public void setCurrentLeader(Vote v) throws IOException
    {
        this.currentLeader = v; 
    }

    @Override
    public Vote getCurrentLeader() 
    {
        return this.currentLeader;
    }

    @Override
    public void sendMessage(MessageType type, byte[] messageContents, InetSocketAddress target) throws IllegalArgumentException 
    {
        Message message = new Message(type, messageContents, this.myAddress.getHostString(), this.myPort, target.getHostString(), target.getPort());
        this.outgoingMessages.offer((message));
    }

    @Override
    public void sendBroadcast(MessageType type, byte[] messageContents) 
    {
        for(InetSocketAddress socketAddress : this.peerIDtoAddress.values())
        {
            sendMessage(type, messageContents, socketAddress);
        }   
    }

    @Override
    public ServerState getPeerState() 
    {
        return this.state;
    }

    @Override
    public void setPeerState(ServerState newState) 
    {
        this.state = newState;  
    }

    @Override
    public Long getServerId() 
    {
        return this.id;
    }

    @Override
    public long getPeerEpoch() 
    {
        return this.peerEpoch;
    }

    @Override
    public InetSocketAddress getAddress() 
    {
        return this.myAddress;
    }

    @Override
    public int getUdpPort() 
    {
        return this.myPort;
    }

    @Override
    public InetSocketAddress getPeerByID(long peerId) 
    {
        return this.peerIDtoAddress.get(peerId);
    }

    @Override
    public int getQuorumSize() 
    {
        return (this.peerIDtoAddress.size()/2) + 1;
    }

}
