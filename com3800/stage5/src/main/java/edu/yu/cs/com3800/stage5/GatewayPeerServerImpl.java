package edu.yu.cs.com3800.stage5;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.UDPMessageReceiver;
import edu.yu.cs.com3800.UDPMessageSender;

public class GatewayPeerServerImpl extends ZooKeeperPeerServerImpl
{
    public GatewayPeerServerImpl(int udpPort, long peerEpoch, Long serverID, Map<Long, InetSocketAddress> peerIDtoAddress, Long gatewayID, int numberOfObservers)
    {
        super(udpPort, peerEpoch, serverID, peerIDtoAddress, gatewayID, numberOfObservers);
        super.setPeerState(ServerState.OBSERVER);
        System.out.println("Created OBSERVER on port "+ udpPort);
    }
    @Override
    public void setPeerState(ServerState state)
    {
        super.setPeerState(ServerState.OBSERVER);
    }

    public void removeFromPeerIDtoAddress(long id)
    {
        super.removeFromPeerIDtoAddress(id);
    }
}
