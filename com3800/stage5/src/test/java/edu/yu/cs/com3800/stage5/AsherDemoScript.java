package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.stage5.ZooKeeperPeerServerImpl;
import org.junit.runner.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AsherDemoScript
{
    private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

    private LinkedBlockingQueue<Message> outgoingMessages;
    private final LinkedBlockingQueue<Message> incomingMessages;
    private final int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080, 6969};
    //private int[] ports = {8010, 8020};
    private int leaderPort = this.ports[this.ports.length - 1];
    private int myPort = 9999;
    private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
    private ArrayList<ZooKeeperPeerServer> servers;
    private GatewayServer gatewayServer;

    private final static Logger logger = Logger.getLogger(AsherDemoScript.class.getName());

    public AsherDemoScript() throws Exception
    {
        FileHandler fileHandler = null;
        try
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/AsherDemoScript.log");
        }
        catch (SecurityException | IOException e)
        {
            e.printStackTrace();
        }
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.info("+-----------------------------------+");
        logger.info("Starting Asher's Demo Script");
        this.outgoingMessages = new LinkedBlockingQueue<>();
        UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages,this.myPort);
        logger.info("Creating servers");
        createServers();
        logger.info("All servers created now waiting 5 seconds to get the leader");
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        printLeaders();
        SimpleClient client = new SimpleClient("localhost",9000);
        client.start();
        logger.info("Starting Client");
        Thread.sleep(2000);
        int random = getRandomInteger(this.servers.size()-1);
        this.servers.get(random).shutdown();
        this.servers.remove(random);
        client.sendCompileAndRunRequest(this.validClass);
        logger.info("Client sending work : " + this.validClass);
        logger.info("Shutting down a server at random: \n" + random + " is being shut down");
        Thread.sleep(20000);
        ZooKeeperPeerServer leader = null;
        for(ZooKeeperPeerServer server: this.servers)
        {
            if(server.getUdpPort() == this.leaderPort)
            {
                server.shutdown();
                leader = server;
                logger.info("Shutting down the leader " + leader.getServerId());
            }
        }
        servers.remove(leader);
        Thread.sleep(30000);
        logger.info("Hopefully Doing the election again");
        printLeaders();
        long start = System.currentTimeMillis();
        for (int i = 0; i < this.ports.length; i++)
        {
            String code = this.validClass.replace("world!", "world! from code version " + i);
            logger.info("Client sending message: " + "world! from code version " + i);
            client.sendCompileAndRunRequest(code);
            Thread.sleep(5000);
            if(System.currentTimeMillis() - start > 40000)
            {
                logger.info("Request timed out");
                break;
            }
        }
        Util.startAsDaemon(sender, "Sender thread");
        this.incomingMessages = new LinkedBlockingQueue<>();
        UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort,null);
        Util.startAsDaemon(receiver, "Receiver thread");
        Thread.sleep(1000);
        printResponses();
        stopServers();
        logger.info("Mission Failed Successfully :)");
        logger.info("+------------------------------+");
    }

    private void printLeaders()
    {
        String results = "";
        for (ZooKeeperPeerServer server : this.servers)
        {
            Vote leader = server.getCurrentLeader();
            if (leader != null) {
                results += "Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name() + "\n";
                if(server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING)
                {
                    this.leaderPort = server.getUdpPort();
                }
            }
            else
            {
                results += "Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name() + "\n";
            }
        }
        logger.info(results);
    }

    private void stopServers()
    {
        for (ZooKeeperPeerServer server : this.servers)
        {
            server.shutdown();
            logger.info("Shutting down server " + server.getServerId());
        }
        this.gatewayServer.shutdown();
        logger.info("Shutting down Gateway");
    }

    private void printResponses() throws Exception {
        String completeResponse = "";
        for (int i = 0; i < this.ports.length; i++) {
            Message msg = this.incomingMessages.take();
            String response = new String(msg.getMessageContents());
            completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
        }
        logger.info("Complete response :\n" + completeResponse);
    }

    private void sendMessage(String code) throws InterruptedException
    {
        Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

        this.outgoingMessages.put(msg);
    }

    private void createServers() throws IOException {
        //create IDs and addresses
        try
        {
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969)
                {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                    this.gatewayServer.start();
                    logger.info("Starting Gateway Server with ID " + entry.getKey() + " on port 9000");
                }
                else
                {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                    logger.info("Starting server " + entry.getKey() + " on port " + entry.getValue().getPort());
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private static int getRandomInteger(int max)
    {
        double random = Math.floor(Math.random() * max);
        return (int)random;
    }
    public static void main(String[] args) throws Exception
    {
        new AsherDemoScript();
    }
}
