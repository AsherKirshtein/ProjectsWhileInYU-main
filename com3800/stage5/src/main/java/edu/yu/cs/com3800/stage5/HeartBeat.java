package edu.yu.cs.com3800.stage5;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.Util;
import edu.yu.cs.com3800.Vote;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.Message.MessageType;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HeartBeat extends Thread
{
    private final int GOSSIP = 3000;
    private final int FAIL = GOSSIP * 10;
    private final int CLEAN_UP = FAIL*2;
    private Integer heartBeat = 0;
    private ZooKeeperPeerServerImpl myServer;
    private Map<Long,InetSocketAddress> peerIDtoAddress;
    private Long[] IDS;
    private LinkedBlockingQueue<Message> incomingMessages;

    private int beatNum;
    private Set<Integer> deadPorts;

    private boolean leaderDown;
    private int leaderPort;

    private long Bff;
    private Map<Long, Integer> heartBeatTable;

    private static Logger summary;
    private static Logger verbose;
    private FileHandler fileHandler1;
    private FileHandler fileHandler2;

    HeartBeat(ZooKeeperPeerServerImpl server, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages)
    {
        this.myServer = server;
        this.peerIDtoAddress = peerIDtoAddress;
        this.incomingMessages = incomingMessages;
        this.heartBeatTable = new ConcurrentHashMap<>();
        this.IDS = peerIDtoAddress.keySet().toArray(new Long[0]);
        if(this.myServer.getServerId() < peerIDtoAddress.size())
        {
            this.Bff = this.IDS[Integer.parseInt(myServer.getServerId().toString())];
        }
        for(Long id: peerIDtoAddress.keySet())
        {
            heartBeatTable.put(id,0);
            //System.out.println("Adding (" + id + ",0) to the table");
        }
        //System.out.println(this.myServer.getServerId() + " my BFF is " + this.Bff);
        this.beatNum = 0;
        this.deadPorts = new HashSet<>();
        this.leaderDown = false;

        summary = Logger.getLogger(HeartBeat.class.getName()+"_"+myServer.getUdpPort());
        verbose = Logger.getLogger(HeartBeat.class.getName());

        try
        {
            fileHandler1 = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/HeartBeatSummary_"+myServer.getUdpPort()+".log");
            fileHandler2 = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/HeartBeatVerbose.log");
        }
        catch (SecurityException | IOException e)
        {
            e.printStackTrace();
        }

        summary.addHandler(fileHandler1);
        verbose.addHandler(fileHandler2);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler1.setFormatter(formatter);
        fileHandler2.setFormatter(formatter);
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(this.myServer.getUdpPort()+9), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/summary", new MyHandler());
        httpServer.createContext("/verbose", new MyHandler());
        httpServer.start();

        summary.info("ZooKeeperPeerServerImpl created");
    }

    @Override
    public void run()
    {
        List<Long> failed = new ArrayList<>();
        while (!isInterrupted())
        {
            beatNum++;
            this.heartBeatTable.put(this.myServer.getServerId(), beatNum);
            this.Bff++;
            if(this.Bff == this.myServer.getServerId())
            {
                this.Bff++;
            }
            if(this.Bff >= this.heartBeatTable.size())
            {
                this.Bff = 0;
                if(this.myServer.getServerId() == 0)
                {
                    this.Bff = 1;
                }
            }
            try
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(this.heartBeatTable);
                byte[] bytes = bos.toByteArray();
                InetSocketAddress receiver = this.peerIDtoAddress.get(this.Bff);
                while(receiver == null)
                {
                    receiver = this.peerIDtoAddress.get(this.Bff++);
                    if(this.Bff > this.peerIDtoAddress.size())
                    {
                        this.Bff = 0;
                    }
                }
                this.myServer.sendMessage(MessageType.GOSSIP, bytes,receiver);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            try
            {
                Thread.sleep(GOSSIP/3);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            while(!this.incomingMessages.isEmpty()) {

                Message m =  this.incomingMessages.peek();
                if(m != null) {
                    if (m.getMessageType() == MessageType.GOSSIP) {
                        this.incomingMessages.poll();
                        verbose.info(this.myServer.getServerId() + ": got a message from " + m.getSenderPort() + " " + m.getMessageContents() + " at " + System.currentTimeMillis());
                        byte[] messageBytes = m.getMessageContents();
                        ConcurrentHashMap<Long, Integer> hashtable = null;
                        try {
                            ByteArrayInputStream bais = new ByteArrayInputStream(messageBytes);
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            hashtable = (ConcurrentHashMap<Long, Integer>) ois.readObject();
                            ois.close();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            //throw new RuntimeException(e);
                            break;
                        } catch (ClassNotFoundException e) {
                            break;
                            //e.printStackTrace();
                            //throw new RuntimeException(e);
                        }
                        for (Long id : this.heartBeatTable.keySet()) {
                            if (hashtable.get(id) != null && this.heartBeatTable.get(id) != null && this.heartBeatTable.get(id) < hashtable.get(id)) {
                                this.heartBeatTable.put(id, hashtable.get(id));
                                summary.info(this.myServer.getServerId() + ": updating my table because of " + id + "'s message at " + System.currentTimeMillis());
                            }
                            if (this.heartBeatTable.get(id) != null && this.heartBeatTable.get(id) < this.beatNum - 10 && !failed.contains(id) && id != 8) {
                                failed.add(id);
                                System.out.println(this.myServer.getServerId() + " is marking " + id + " as failed");
                                summary.info(this.myServer.getServerId() + ": no heartbeat from " + id + " it needs mouth to mouth SERVER FAILED");
                                if(this.myServer.getCurrentLeader() == null || this.myServer.getCurrentLeader().getProposedLeaderID() == id)
                                {
                                    summary.info(this.myServer.getServerId() + ": is switching state to LOOKING");
                                    System.out.println(this.myServer.getServerId() + ": is switching state to LOOKING");
                                    this.leaderDown = true;
                                }
                            }
                        }
                        List<Long> copy = new ArrayList<Long>(failed);
                        for (long id : copy) {
                            if (this.heartBeatTable.get(id) != null && this.heartBeatTable.get(id) < this.beatNum - 15) {
                                this.myServer.reportFailedPeer(id);
                                //System.out.println(this.myServer.getServerId() + " is reporting " + id);
                                this.heartBeatTable.remove(id);
                                failed.remove(id);
                            }
                        }
                    }
                }
            }
        }
    }
    public void setLeaderPort(int port)
    {
        this.leaderPort = port;
    }

    public void newLeaderNow()
    {
        this.leaderDown = false;
    }
    public boolean isLeaderDown()
    {
        return this.leaderDown;
    }

    private class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException
        {
            if(t.getRequestURI().toString().equals("/summary"))
            {
                String hiJudah = "";
                BufferedReader br = new BufferedReader(new FileReader("src/main/java/edu/yu/cs/com3800/stage5/logs/HeartBeatSummary"+myServer.getUdpPort()+".log"));
                String line;
                while ((line = br.readLine()) != null) {
                    hiJudah+=line+"\n";
                }
                t.sendResponseHeaders(200, hiJudah.length());
                OutputStream os = t.getResponseBody();
                os.write(hiJudah.getBytes());
                os.close();
            }

            if(t.getRequestURI().toString().equals("/verbose"))
            {
                System.out.println("++++++++++++++++++++++++++++++++++++");
                String what = "";
                BufferedReader br = new BufferedReader(new FileReader("src/main/java/edu/yu/cs/com3800/stage5/logs/HeartBeatVerbose.log"));
                String line;
                while ((line = br.readLine()) != null) {
                    what+=line+"\n";
                }
                t.sendResponseHeaders(200, what.length());
                OutputStream os = t.getResponseBody();
                os.write(what.getBytes());
                os.close();
            }
        }
    }
}
