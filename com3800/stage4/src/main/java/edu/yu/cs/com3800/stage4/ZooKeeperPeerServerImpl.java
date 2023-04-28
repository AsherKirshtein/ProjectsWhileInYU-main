package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.Message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


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

    private JavaRunnerFollower follower;
    private RoundRobinLeader leader;

    private static Logger logger;
    private FileHandler fileHandler;

    private int observerNodes;

    public ZooKeeperPeerServerImpl(int myPort, long peerEpoch, long id, Map<Long,InetSocketAddress> peerIDtoAddress)
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
        this.currentLeader = null;
        this.observerNodes = 0;

        this.leader = null;
        this.follower = null;

        logger = Logger.getLogger(ZooKeeperPeerServerImpl.class.getName());

        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage4/ZooKeeperPeerServerImpl " + myPort + ".log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }

        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter);
        logger.info("ZooKeeperPeerServerImpl created");
    }

    public ZooKeeperPeerServerImpl(int myPort, long peerEpoch, long id, Map<Long,InetSocketAddress> peerIDtoAddress, int observerNodes)
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
        this.currentLeader = null;

        this.leader = null;
        this.follower = null;
        this.observerNodes = observerNodes;

        logger = Logger.getLogger(ZooKeeperPeerServerImpl.class.getName());

        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage4/ZooKeeperPeerServerImpl " + myPort + ".log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }

        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter);
        logger.info("ZooKeeperPeerServerImpl created");
    }

    @Override
    public void shutdown()
    {
        this.shutdown = true;
        this.senderWorker.shutdown();
        this.receiverWorker.shutdown();
        logger.info("ZooKeeperPeerServerImpl shutdown");
    }

    @Override
    public void run()
    {
        logger.info("ZooKeeperPeerServerImpl running");
        try 
        {
            this.senderWorker = new UDPMessageSender(this.outgoingMessages, this.myPort);
            this.senderWorker.start();
            this.receiverWorker = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, this);
            this.receiverWorker.start();
        } 
        catch (IOException e1)
        {
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
                    logger.info("ZooKeeperPeerServerImpl in LOOKING state, looking for leader");
                    Thread.sleep(1000);
                        setCurrentLeader(new ZooKeeperLeaderElection(this, this.incomingMessages).lookForLeader());
                        break;
                    //additions for stage 3 Following and leading states
                    case LEADING:
                    if(this.leader == null)
                    {
                        this.leader = new RoundRobinLeader(this, this.peerIDtoAddress);
                        logger.info("ZooKeeperPeerServerImpl: " + this.id  + " in LEADING state, starting leader");
                    }
                    this.leader.run();
                    break;
                    case FOLLOWING:
                    if(this.follower == null)
                    {
                        this.follower = new JavaRunnerFollower(this, this.incomingMessages, getPeerByID(this.currentLeader.getProposedLeaderID()));
                        logger.info("ZooKeeperPeerServerImpl: " + this.id  + " in FOLLOWING state, starting follower");
                    }
                    this.follower.run();
                    break;
                    case OBSERVER:
                    logger.info("ZooKeeperPeerServerImpl in OBSERVING state, finding leader");
                    Thread.sleep(1000);
                        setCurrentLeader(new ZooKeeperLeaderElection(this, this.incomingMessages).lookForLeader());
                        break;
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
        logger.info("ZooKeeperPeerServerImpl: " + this.id  + " sending " + type + " message to " + target);
    }

    @Override
    public void sendBroadcast(MessageType type, byte[] messageContents) 
    {
        for(InetSocketAddress socketAddress : this.peerIDtoAddress.values())
        {
            sendMessage(type, messageContents, socketAddress);
        }  
        logger.info("ZooKeeperPeerServerImpl: " + this.id  + " sending " + type + " broadcast message to all peers"); 
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
        return (this.peerIDtoAddress.size()/2) + 1 - this.observerNodes;
    }

}
