package edu.yu.cs.com3800.stage5.demowork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.UDPMessageReceiver;
import edu.yu.cs.com3800.UDPMessageSender;
import edu.yu.cs.com3800.Util;
import edu.yu.cs.com3800.Vote;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.stage5.GatewayPeerServerImpl;
import edu.yu.cs.com3800.stage5.GatewayServer;
import edu.yu.cs.com3800.stage5.SimpleClient;

import java.net.URI;
import edu.yu.cs.com3800.stage5.ZooKeeperPeerServerImpl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.FileHandler;

public class Stage5PeerServerDemo {
    public static void main(String[] args) throws Exception
    {
        new Stage5PeerServerDemoDeadLeaderFewServersNoClient();
        new Stage5PeerServerDemoDeadLeaderFewServers();
        new Stage5PeerServerDemoDeadRandomFewServers();
        new Stage5PeerServerDemoDeadRandom();
        new Stage5PeerServerDemoDeadLeader();
        new Stage5PeerServerDemoSmoothRunFewServers();
        new Stage5PeerServerDemoSmoothRun();
        new Stage5PeerServerDemoDeadLeaderFewServersAllFail();
    }

    public static class Stage5PeerServerDemoDeadLeaderFewServersAllFail {
        private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

        private LinkedBlockingQueue<Message> outgoingMessages;
        private final int[] ports = {8010, 8020, 8030, 8040, 6969};
        //private int[] ports = {8010, 8020};
        private int leaderPort = this.ports[this.ports.length - 1];
        private int myPort = 9999;
        private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
        private ArrayList<ZooKeeperPeerServer> servers;
        private GatewayServer gatewayServer;

        private boolean passedTest = false;
        private int roundRobinPort;



        public Stage5PeerServerDemoDeadLeaderFewServersAllFail() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            stopServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            Thread.sleep(1000);
            ZooKeeperPeerServer leader = null;
            for (ZooKeeperPeerServer server : this.servers) {
                if (server.getUdpPort() == this.leaderPort) {
                    server.shutdown();
                    leader = server;
                    System.out.println("Shutting down the leader");
                }
            }
            servers.remove(leader);
            Thread.sleep(25000);
            System.out.println("Hopefully Doing the election again");
            printLeaders();

            Thread.sleep(1000);

            stopServers();
            passedTest = true;
        }

        public boolean isPassedTest()
        {
            return this.passedTest;
        }
        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {

        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }
    public static class Stage5PeerServerDemoDeadLeaderFewServersNoClient {
        private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

        private LinkedBlockingQueue<Message> outgoingMessages;
        private final int[] ports = {8010, 8020, 8030, 8040, 6969};
        //private int[] ports = {8010, 8020};
        private int leaderPort = this.ports[this.ports.length - 1];
        private int myPort = 9999;
        private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
        private ArrayList<ZooKeeperPeerServer> servers;
        private GatewayServer gatewayServer;

        private boolean passedTest = false;
        private int roundRobinPort;



        public Stage5PeerServerDemoDeadLeaderFewServersNoClient() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            Thread.sleep(1000);
            ZooKeeperPeerServer leader = null;
            for (ZooKeeperPeerServer server : this.servers) {
                if (server.getUdpPort() == this.leaderPort) {
                    server.shutdown();
                    leader = server;
                    System.out.println("Shutting down the leader");
                }
            }
            servers.remove(leader);
            Thread.sleep(25000);
            System.out.println("Hopefully Doing the election again");
            printLeaders();

            Thread.sleep(1000);

            stopServers();
            passedTest = true;
        }

        public boolean isPassedTest()
        {
            return this.passedTest;
        }
        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {

        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoDeadLeaderFewServers {
        private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

        private LinkedBlockingQueue<Message> outgoingMessages;
        private final LinkedBlockingQueue<Message> incomingMessages;
        private final int[] ports = {8010, 8020, 8030, 8040, 6969};
        //private int[] ports = {8010, 8020};
        private int leaderPort = this.ports[this.ports.length - 1];
        private int myPort = 9999;
        private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
        private ArrayList<ZooKeeperPeerServer> servers;
        private GatewayServer gatewayServer;

        private boolean passedTest = false;
        private int roundRobinPort;



        public Stage5PeerServerDemoDeadLeaderFewServers() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            this.gatewayServer.start();
            Thread.sleep(1000);
            ZooKeeperPeerServer leader = null;
            for (ZooKeeperPeerServer server : this.servers) {
                if (server.getUdpPort() == this.leaderPort) {
                    server.shutdown();
                    leader = server;
                    System.out.println("Shutting down the leader");
                }
            }
            servers.remove(leader);
            Thread.sleep(25000);
            System.out.println("Hopefully Doing the election again");
            printLeaders();
            //System.out.println("Shutting down : " + this.servers.get(random2).getUdpPort());
            for (int i = 0; i < this.ports.length; i++) {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                sendMessage(code);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
            passedTest = true;
        }

        public boolean isPassedTest()
        {
            return this.passedTest;
        }
        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoDeadLeader {
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
        private int roundRobinPort;

        public Stage5PeerServerDemoDeadLeader() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            this.gatewayServer.start();
            Thread.sleep(1000);
            ZooKeeperPeerServer leader = null;
            for (ZooKeeperPeerServer server : this.servers) {
                if (server.getUdpPort() == this.leaderPort) {
                    server.shutdown();
                    leader = server;
                    System.out.println("Shutting down the leader");
                }
            }
            servers.remove(leader);
            Thread.sleep(50000);
            System.out.println("Hopefully Doing the election again");
            printLeaders();
            //System.out.println("Shutting down : " + this.servers.get(random2).getUdpPort());
            for (int i = 0; i < this.ports.length; i++) {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                sendMessage(code);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
        }

        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoDeadRandom {
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
        private int roundRobinPort;

        public Stage5PeerServerDemoDeadRandom() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            this.gatewayServer.start();
            Thread.sleep(1000);
            int random = getRandomInteger(this.servers.size() - 1);
            System.out.println("Killing " + servers.get(random).getServerId());
            servers.remove(random);
            Thread.sleep(20000);
            SimpleClient client = new SimpleClient("localhost", 9000);
            client.start();
            for (int i = 0; i < this.ports.length; i++) {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                sendMessage(code);
                Thread.sleep(5000);
                client.sendCompileAndRunRequest(code);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
        }

        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoSmoothRun {
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
        private int roundRobinPort;

        public Stage5PeerServerDemoSmoothRun() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            SimpleClient client = new SimpleClient("localhost", 9000);
            client.start();
            for (int i = 0; i < this.ports.length; i++)
            {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                client.sendCompileAndRunRequest(code);
                Thread.sleep(5000);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
        }

        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoSmoothRunFewServers {
        private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

        private LinkedBlockingQueue<Message> outgoingMessages;
        private final LinkedBlockingQueue<Message> incomingMessages;
        private final int[] ports = {8050, 8060, 8070, 8080, 6969};
        //private int[] ports = {8010, 8020};
        private int leaderPort = this.ports[this.ports.length - 1];
        private int myPort = 9999;
        private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
        private ArrayList<ZooKeeperPeerServer> servers;
        private GatewayServer gatewayServer;
        private int roundRobinPort;

        public Stage5PeerServerDemoSmoothRunFewServers() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            SimpleClient client = new SimpleClient("localhost", 9000);
            client.start();
            for (int i = 0; i < this.ports.length; i++)
            {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                client.sendCompileAndRunRequest(code);
                Thread.sleep(5000);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
        }

        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
            if (this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader() == null) {
                System.out.println("Gateway server has no leader");
            } else {
                System.out.println("Gateway server has the following ID as its leader: " + this.gatewayServer.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID());
                return;
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }
    }

    public static class Stage5PeerServerDemoDeadRandomFewServers {
        private String validClass = "package edu.yu.cs.com3800.stage5;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

        private LinkedBlockingQueue<Message> outgoingMessages;
        private final LinkedBlockingQueue<Message> incomingMessages;
        private final int[] ports = {8010, 8020, 8030, 8040,6969};
        //private int[] ports = {8010, 8020};
        private int leaderPort = this.ports[this.ports.length - 1];
        private int myPort = 9999;
        private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
        private ArrayList<ZooKeeperPeerServer> servers;
        private GatewayServer gatewayServer;
        private int roundRobinPort;

        public Stage5PeerServerDemoDeadRandomFewServers() throws Exception {
            //step 1: create sender & sending queue
            this.outgoingMessages = new LinkedBlockingQueue<>();
            UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
            //step 2: create servers

            createServers();
            //step2.1: wait for servers to get started
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            printLeaders();
            this.gatewayServer.start();
            Thread.sleep(1000);
            int random = getRandomInteger(this.servers.size() - 1);
            System.out.println("Killing " + servers.get(random).getServerId());
            servers.remove(random);
            Thread.sleep(20000);
            SimpleClient client = new SimpleClient("localhost", 9000);
            client.start();
            for (int i = 0; i < this.ports.length; i++) {
                String code = this.validClass.replace("world!", "world! from code version " + i);
                sendMessage(code);
                Thread.sleep(5000);
                client.sendCompileAndRunRequest(code);
            }
            Util.startAsDaemon(sender, "Sender thread");
            this.incomingMessages = new LinkedBlockingQueue<>();
            UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
            Util.startAsDaemon(receiver, "Receiver thread");
            //step 4: validate responses from leader
            Thread.sleep(1000);
            printResponses();
            //step 5: stop servers
            stopServers();
        }

        private void printLeaders() {
            for (ZooKeeperPeerServer server : this.servers) {
                Vote leader = server.getCurrentLeader();
                if (leader != null) {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
                    if (server.getPeerState() == ZooKeeperPeerServer.ServerState.LEADING) {
                        this.leaderPort = server.getUdpPort();
                    }
                } else {
                    System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has no leader and its state is " + server.getPeerState().name());
                }
            }
        }

        private void stopServers() {
            for (ZooKeeperPeerServer server : this.servers) {
                server.shutdown();
            }
            this.gatewayServer.shutdown();
        }

        private void printResponses() throws Exception {
            String completeResponse = "";
            for (int i = 0; i < this.ports.length; i++) {
                Message msg = this.incomingMessages.take();
                String response = new String(msg.getMessageContents());
                completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
            }
            System.out.println(completeResponse);
        }

        private void sendMessage(String code) throws InterruptedException {
            Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);

            this.outgoingMessages.put(msg);
        }

        private void createServers() throws IOException {
            //create IDs and addresses
            HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
            for (int i = 0; i < this.ports.length; i++) {
                peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
            }
            //create servers
            this.servers = new ArrayList<>(3);
            for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
                if (entry.getValue().getPort() == 6969) {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    this.gatewayServer = new GatewayServer(9000, map);
                    this.servers.add(gatewayServer.gatewayPeerServerImpl);
                } else {
                    HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
                    map.remove(entry.getKey());
                    ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, Long.valueOf(69), 1);
                    this.servers.add(server);
                    server.start();
                }
            }
        }

        private int getRandomInteger(int max) {
            double random = Math.floor(Math.random() * max);
            return (int) random;
        }

    }
}

