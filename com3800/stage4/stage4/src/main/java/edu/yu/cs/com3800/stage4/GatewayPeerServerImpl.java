package edu.yu.cs.com3800.stage4;

import java.net.InetSocketAddress;
import java.util.Map;

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

    @Override
    public ServerState getPeerState()
    {
        return ServerState.OBSERVER;
    }

}
