package edu.yu.cs.com3800.stage5;

import java.io.ByteArrayInputStream;
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

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.Util;
import edu.yu.cs.com3800.Message.MessageType;

public class JavaRunnerFollower extends Thread
{
    private ZooKeeperPeerServerImpl myServer;
    private LinkedBlockingQueue<Message> incomingMessages;
    private int leader;
    private HashMap<Long,Integer> heartBeats;
    private Map<Long, InetSocketAddress> peerIDtoAddress;

    private FileHandler fileHandler;
    private static Logger logger;
    private HeartBeat heartBeat;

    private ServerSocket serverSocket;
    public JavaRunnerFollower(ZooKeeperPeerServerImpl zooKeeperPeerServerImpl, LinkedBlockingQueue<Message> incomingMessages, int leader, Map<Long, InetSocketAddress> peerIDtoAddress, HeartBeat heartBeat, ServerSocket serverSocket)
    {
        setDaemon(true);
        this.serverSocket = serverSocket;
        this.myServer = zooKeeperPeerServerImpl;
        this.incomingMessages = incomingMessages;
        this.leader = leader;
        this.peerIDtoAddress = peerIDtoAddress;
        this.heartBeat = heartBeat;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        Queue<Message> completedWork = new LinkedList<>();
        while(!this.isInterrupted())
        {
            try
            {
                Socket socket = this.serverSocket.accept();
                InputStream in = socket.getInputStream();
                JavaRunner runner = new JavaRunner();
                try
                {
                    Message message = new Message(Util.readAllBytesFromNetwork(in));
                    if(MessageType.WORK == message.getMessageType())
                    {
                        InputStream targetStream = new ByteArrayInputStream(message.getNetworkPayload());
                        String reply = runner.compileAndRun(targetStream);//code stops here fix that
                        OutputStream output = socket.getOutputStream();
                        Message replyMessage = new Message(MessageType.COMPLETED_WORK, reply.getBytes(), "localhost", this.leader, "localhost", this.leader);
                        if(this.heartBeat.isLeaderDown())
                        {
                            completedWork.offer(replyMessage);
                        }
                        else
                        {
                            for(Message m: completedWork)
                            {
                                output.write(m.getNetworkPayload());
                            }
                            completedWork.clear();
                        }
                    }
                } 
                catch (IllegalArgumentException | ReflectiveOperationException e) 
                {
                    // TODO Auto-generated catch block
                    if(e instanceof IllegalArgumentException)
                    {
                        String reply = "IllegalArgumentException";
                        Message replyMessage = new Message(MessageType.COMPLETED_WORK, reply.getBytes(), "localhost", this.leader, "localhost", this.leader);
                        OutputStream output = socket.getOutputStream();
                        output.write(replyMessage.getNetworkPayload());
                    }
                    //e.printStackTrace();
                }
                socket.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
               // e.printStackTrace();
            }
        }
        //System.out.println("Interuption occured");
    }
}
