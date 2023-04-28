package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.Message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;
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
    public Long gateWayId;

    private HeartBeat heartBeat;

    private List<ZooKeeperPeerServer> zooKeeperPeerServerList;
    private ServerSocket serverSocket;

    public ZooKeeperPeerServerImpl(int udpPort, long peerEpoch, Long serverID, Map<Long, InetSocketAddress> peerIDtoAddress, Long gatewayID, int numberOfObservers)
    {
        this.myPort = udpPort;
        this.zooKeeperPeerServerList = new ArrayList<>();
        try
        {
            if(udpPort != 6969) {
                this.serverSocket = new ServerSocket(udpPort + 2);
            }
        }
        catch (IOException e)
        {
            //throw new RuntimeException(e);
        }
        this.myAddress = new InetSocketAddress("localhost", myPort);
        this.gateWayId = gatewayID;
        this.peerEpoch = peerEpoch;
        this.id = serverID;
        this.observerNodes = numberOfObservers;
        this.gateWayId = gatewayID;
        this.peerIDtoAddress = peerIDtoAddress;
        this.state = ServerState.LOOKING;
        this.shutdown = false;
        this.senderWorker = null;
        this.receiverWorker = null;
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.currentLeader = null;
        this.observerNodes = numberOfObservers;
        this.gateWayId = gatewayID;

        this.leader = null;
        this.follower = null;
        this.heartBeat = new HeartBeat(this, this.peerIDtoAddress ,incomingMessages);
        logger = Logger.getLogger(ZooKeeperPeerServerImpl.class.getName());

        try
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/ZooKeeperPeerServerImpl " + myPort + ".log");
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
        logger.info(this.id+ ": Zookeeper Is shutting down");
    }

    @Override
    public void run()
    {
        logger.info(this.id + ": Server Running");
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
        try
        {
            while (!this.shutdown)
            {
                //System.out.println(this.id + " " + getPeerState());
                switch (getPeerState())
                {
                    case LOOKING:
                    case OBSERVER:
                        Thread.sleep(3000);
                        if (this.currentLeader == null)
                        {
                            //System.out.println(this.id + ": is running an election");
                            setCurrentLeader(new ZooKeeperLeaderElection(this, this.incomingMessages).lookForLeader());
                            //System.out.println(this.id + ": Ended election with leader " + getCurrentLeader().getProposedLeaderID() + " I am now a " + getPeerState());
                        }
                        Thread.sleep(5000);
                        if(getPeerState() == ServerState.OBSERVER)
                        {
                            if(!this.heartBeat.isAlive())
                            {
                                logger.info("Gateway is starting heartbeat");
                                this.heartBeat.start();
                            }
                        }
                    break;
                    case LEADING:
                    if(this.leader == null)
                    {
                        this.leader = new RoundRobinLeader(this, this.peerIDtoAddress, this.incomingMessages, this.heartBeat,this.serverSocket);
                        logger.info("ZooKeeperPeerServerImpl: " + this.id  + " in LEADING state, starting leader " + this.currentLeader);
                        int leaderPort = this.myPort;
                        this.heartBeat.setLeaderPort(leaderPort);
                        this.leader.start();
                        if(!this.heartBeat.isAlive())
                        {
                            logger.info("Leader " + this.id + " is starting heartbeat");
                            this.heartBeat.start();
                        }
                    }
                    break;
                    case FOLLOWING:
                        if(this.follower == null)
                    {
                        logger.info("Starting java runner follower on port: " + getUdpPort());
                        this.follower = new JavaRunnerFollower(this, this.incomingMessages, getUdpPort(),this.peerIDtoAddress,this.heartBeat, this.serverSocket);
                        int leaderPort = this.getPeerByID(this.getCurrentLeader().getProposedLeaderID()).getPort();
                        this.heartBeat.setLeaderPort(leaderPort);
                        this.follower.start();
                        if(!this.heartBeat.isAlive())
                        {
                            logger.info("JavaRunner " + this.id + " is starting heartbeat");
                            this.heartBeat.start();
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void reportFailedPeer(long peerID)
    {
        ZooKeeperPeerServer.super.reportFailedPeer(peerID);
        this.peerIDtoAddress.remove(peerID);
        if(this.currentLeader != null && this.currentLeader.getProposedLeaderID() == peerID)
        {
            logger.info(this.id + " Leader is down I REPEAT LEADER IS DOWN CODE RED CODE RED SOUND THE ALARM");
            setPeerState(ServerState.LOOKING);
            this.peerEpoch++;
            this.currentLeader = null;
        }
        else
        {
            logger.info(this.id + ": " + peerID + " is dead. Removing from PeerIDtoAddress and it should no longer be receiving any more work");
        }

    }

    @Override
    public void setCurrentLeader(Vote v) throws IOException
    {
        this.currentLeader = v;
        this.heartBeat.newLeaderNow();
        this.heartBeat.setLeaderPort((int) v.getProposedLeaderID());
        logger.info(this.id + ": setting current leader to " + v.getProposedLeaderID());
    }

    @Override
    public Vote getCurrentLeader()
    {
        return this.currentLeader;
    }

    public void removeFromPeerIDtoAddress(Long id)
    {
        this.peerIDtoAddress.remove(id);
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
        //System.out.println(this.id + ": is switching to " + newState);
        if(newState == ServerState.LOOKING && this.leader != null)
        {
            this.peerIDtoAddress.remove(leader);
            logger.info(this.id + ": Is removing the leader from the peerIDToAddress");
            this.leader = null;
        }
        else
        {
            logger.info(this.id + ": my state is now " + newState);
        }
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
