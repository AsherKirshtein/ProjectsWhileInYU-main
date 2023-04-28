package edu.yu.cs.com3800.stage4;

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
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.net.httpserver.*;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
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
    private long leaderId;

    public GatewayPeerServerImpl gatewayPeerServerImpl;

    public GatewayServer(int port, Map<Long, InetSocketAddress> peerIDtoAddress)
    {
        logger = Logger.getLogger(GatewayServer.class.getName());

        try
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage4/GatewayServer.log");
        }
        catch (SecurityException | IOException e)
        {
            e.printStackTrace();
        }

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
        this.gatewayPeerServerImpl = new GatewayPeerServerImpl(2600,0, Long.valueOf(69), peerIDtoAddress,Long.valueOf(3000),1);
        this.gatewayPeerServerImpl.start();
        this.server.createContext("/compileandrun", new MyHandler(this.gatewayPeerServerImpl)); // double check this
        this.server.setExecutor(null); // creates a default executor



    }
    @Override
    public void run()
    {
        while(!this.isInterrupted())
        {// got the http from the client
            while(null == this.gatewayPeerServerImpl.getCurrentLeader())
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            long leader = this.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID();
            logger.info(String.valueOf(leader));
        }
    }

    private class MyHandler implements HttpHandler
    {

        private GatewayPeerServerImpl gatewayPeerServerImpl;

        private MyHandler(GatewayPeerServerImpl gatewayPeerServerImpl)
        {
            this.gatewayPeerServerImpl = gatewayPeerServerImpl;
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
            //Stage 4 additions
            int leaderPort = this.gatewayPeerServerImpl.getPeerByID(this.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID()).getPort();

            ServerSocket serverSocket = new ServerSocket(leaderPort); //double check to make sure I'm using right port
            Socket socket = serverSocket.accept();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(requestMethod);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            // stage 4 additions end

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());

            serverSocket.close();
            os.close();
            logger.info("Got Response Successfully");
        }

    }
}
