package edu.yu.cs.com3800.stage4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
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
    private ServerSocket gatewaySocket;
    
    public RoundRobinLeader(ZooKeeperPeerServer server,  Map<Long, InetSocketAddress> peerIDtoAddress)
    {
        setDaemon(true);

        this.workers = new LinkedList<>();
        this.leaderServer = server;

        try 
        {
            this.gatewaySocket = new ServerSocket(server.getUdpPort());
        } 
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        for(long peerId: peerIDtoAddress.keySet())
        {
            int peerPort = peerIDtoAddress.get(peerId).getPort();
            this.workers.add(peerPort);
        }
        
        logger = Logger.getLogger(RoundRobinLeader.class.getName());
        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage4/RoundRobinLeader.log");
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
    
    public void run()
    {
        while(!this.isInterrupted())
        {
            int worker = this.workers.poll();
            this.workers.add(worker);
            
            ServerSocket serverSocket;
            Socket socket;

            try
            {
                Socket clientSocket = this.gatewaySocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//get the client's message
                
                String clientMessage = in.readLine();
                logger.info("RoundRobinLeader received message from client: " + clientMessage);

                serverSocket = new ServerSocket(worker);
                socket = serverSocket.accept();

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);//send message to worker
                out.println(clientMessage);
                
                logger.info("RoundRobinLeader sent message to worker: ");
                BufferedReader in2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));//get the worker's message
                String workerMessage = in2.readLine();

                logger.info("RoundRobinLeader received message from worker: " + workerMessage);
                PrintWriter out2 = new PrintWriter(clientSocket.getOutputStream(), true);//send message to client
                out2.println(workerMessage);

                logger.info("RoundRobinLeader sent message to client:"); 
            }
            catch (IllegalArgumentException| IOException e)
            {
                e.printStackTrace();
            }        
        }
    } 
}
