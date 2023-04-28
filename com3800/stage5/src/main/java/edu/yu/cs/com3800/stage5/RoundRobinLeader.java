package edu.yu.cs.com3800.stage5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.Message.MessageType;

public class RoundRobinLeader extends Thread
{
    private LinkedBlockingQueue <Message> work;
    private Queue<Integer> workers;// a queue of worker ports
    private ZooKeeperPeerServer leaderServer;
    private InetSocketAddress client;
    
    private static Logger logger;
    private static FileHandler fileHandler;

    //stage 4 additions
    private HashMap<Long,Integer> heartBeats;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Map<Long, InetSocketAddress> peerIDtoAddress;
    private HeartBeat heartBeat;

    private ServerSocket serverSocket;
    public RoundRobinLeader(ZooKeeperPeerServer server, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages, HeartBeat heartBeat, ServerSocket socket)
    {
        setDaemon(true);
        this.serverSocket = socket;
        this.workers = new LinkedList<>();
        this.leaderServer = server;
        this.incomingMessages = incomingMessages;
        this.peerIDtoAddress = peerIDtoAddress;
        //heartBeat.start();
        this.heartBeat = heartBeat;
        this.peerIDtoAddress.remove(6969);
        for(long peerId: this.peerIDtoAddress.keySet())
        {
                int peerPort = this.peerIDtoAddress.get(peerId).getPort();
                if(peerPort + 2 != 6971) {
                    this.workers.add(peerPort + 2);
                }
        }
        this.workers.remove(6971);

        //System.out.println("Workers \n" + workers);
        logger = Logger.getLogger(RoundRobinLeader.class.getName());
        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/RoundRobinLeader.log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter);

        logger.info("RoundRobinLeader started ");
    }
    @Override
    public void run()
    {
        Queue<Message> messages = new LinkedList<>();
        while(!this.isInterrupted())
        {
            try
            {
                logger.info("Waiting to receive on port " + this.leaderServer.getUdpPort());
                Socket clientSocket = this.serverSocket.accept();
                InputStream is = clientSocket.getInputStream();
                Message message = new Message(Util.readAllBytesFromNetwork(is));
                messages.offer(message);
                logger.info("RoundRobinLeader received message from Gateway: " + message.getMessageType());
                if(message.getMessageType() == MessageType.WORK)
                {
                    logger.info("RoundRobinLeader received WORK message from Gateway");
                    int worker = this.workers.remove();
                    this.workers.add(worker);
                    //logger.info("leader Connecting to " + worker);
                    Socket workerSocket = new Socket("localhost", worker);
                    OutputStream os = workerSocket.getOutputStream();
                    os.write(message.getNetworkPayload());
                    logger.info("RoundRobinLeader sent WORK message to worker: " + worker);
                    InputStream is2 = workerSocket.getInputStream();
                    logger.info("RoundRobinLeader received WORKER_RESPONSE message from worker: " + worker);
                    Message message2 = new Message(Util.readAllBytesFromNetwork(is2));
                    clientSocket.getOutputStream().write(message2.getNetworkPayload());
                    logger.info("RoundRobinLeader sent WORKER_RESPONSE message to Gateway");
                    clientSocket.close();
                    workerSocket.close();
                }
            }
            catch (Throwable e)
            {
                //System.out.println("RoundRobinLeader: " + e.getMessage());
            }
        }
    } 
}
