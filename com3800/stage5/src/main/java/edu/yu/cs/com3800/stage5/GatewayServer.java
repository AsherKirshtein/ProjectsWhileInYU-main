package edu.yu.cs.com3800.stage5;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.net.httpserver.*;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.Util;
import edu.yu.cs.com3800.Vote;
import edu.yu.cs.com3800.Message.MessageType;

public class GatewayServer extends Thread
{
    //creates an HTTPServer on whatever port number is passed to it in its constructor

    //keeps track of what node is currently the leader and sends it all client requests over a TCP connection

    //is a peer in the ZooKeeper cluster, but as an OBSERVER only – it does not get a vote in leader elections, rather it merely
    //observes and watches for a winner so it knows who the leader is to which it should send client requests.

        //An OBSERVER must never change its state to any other ServerState

        //Other nodes must not count an OBSERVER when determining how many votes are needed for a quorum, and
        //must not count any votes sent by an OBSERVER when determining if there is a quorum voting for a given server

        //Think though carefully what changes this will require to ZooKeeperLeaderElection!

    //…will have a number of threads running:

        //an HttpServer to accept client requests

        //a GatewayPeerServerImpl, which is a subclass of ZooKeeperPeerServerImpl which can only be an OBSERVER

        //for every client connection, the HttpServer creates and runs an HttpHandler (in a thread in a thread pool) which
        //will, in turn, synchronously communicate with the master/leader over TCP to submit the client request and get
        //a response that it will then return to the client. Be careful to not have any instance variables in your HttpHandler
        //– its methods must be thread safe! Only use local variables in your methods.

    private static Logger logger;
    private FileHandler fileHandler;
    private HttpServer server;

    private Map<Long,InetSocketAddress>peerIDtoAddress;
    public GatewayPeerServerImpl gatewayPeerServerImpl;

    private Queue<Message> messages;
    private int myPort;

    public GatewayServer(int port, Map<Long, InetSocketAddress> peerIDtoAddress)
    {
        logger = Logger.getLogger(GatewayServer.class.getName());
        this.myPort = port;
        try
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/GatewayServer.log");
        }
        catch (SecurityException | IOException e)
        {
            e.printStackTrace();
        }
        this.peerIDtoAddress =peerIDtoAddress;
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        try
        {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        logger.info("GatewayServer created and starting");

        this.gatewayPeerServerImpl = new GatewayPeerServerImpl(6969, Long.valueOf(0), Long.valueOf(8), peerIDtoAddress, Long.valueOf(port), 1);
        logger.info("GatewayPeerServerImpl started");
        this.gatewayPeerServerImpl.start();
        this.messages = new LinkedList<>();
        this.server.createContext("/compileandrun", new MyHandler(this.gatewayPeerServerImpl)); 
        this.server.setExecutor(null); // creates a default executor
        this.server.start();
    }

    public void addToWorkMessages(Message message)
    {
        this.messages.offer(message);
    }
    public void removeFromPeerIDToAddress(long id)
    {
        if(this.peerIDtoAddress.containsKey(id))
        {
            this.peerIDtoAddress.remove(id);
        }
    }

    @Override
    public void run()
    {
        logger.info("GatewayServer running");

        while(!this.isInterrupted())
        {// got the http from the client
            while(this.gatewayPeerServerImpl.getCurrentLeader() == null)
            {
                try
                {
                    Thread.sleep(10000);
                    logger.info("Waiting for leader to be elected");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }


        }
    }

    private class MyHandler implements HttpHandler
    {
        private MyHandler(GatewayPeerServerImpl gatewayPeerServerImpl)
        {

        }

        @Override
        public void handle(HttpExchange t) throws IOException
        {
            InputStream is = t.getRequestBody();
            String requestMethod = t.getRequestMethod();
            if(!requestMethod.equals("POST"))
            {
                t.sendResponseHeaders(400, 0);
                t.close();
                logger.info("Didn't make a POST request; Please make a POST Request");
            }
            logger.info("GatewayServer received a request from a client");

            int leaderPort = gatewayPeerServerImpl.getPeerByID(gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID()).getPort();
            logger.info("Leader port is " + leaderPort);
            String receiverHost =  gatewayPeerServerImpl.getPeerByID(gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID()).getHostName();
            Socket s  = new Socket("localhost", leaderPort + 2);;
            OutputStream output = s.getOutputStream();
            Message m = new Message(MessageType.WORK, is.readAllBytes(), "localhost", myPort, receiverHost ,leaderPort);
            output.write(m.getNetworkPayload());
            InputStream input = s.getInputStream();
            t.sendResponseHeaders(200, 0);

            OutputStream os = t.getResponseBody();
            os.write(Util.readAllBytesFromNetwork(input));
            os.close();
            s.close();
        }

    }

    public void shutdown()
    {
        this.server.stop(0);
        this.gatewayPeerServerImpl.shutdown();
        this.interrupt();
    }
}
