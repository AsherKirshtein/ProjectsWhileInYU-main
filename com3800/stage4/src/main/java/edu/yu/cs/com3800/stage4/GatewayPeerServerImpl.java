package edu.yu.cs.com3800.stage4;

import java.net.InetSocketAddress;
import java.util.Map;

public class GatewayPeerServerImpl extends ZooKeeperPeerServerImpl
{
    public GatewayPeerServerImpl(int myPort, long peerEpoch, long id, Map<Long, InetSocketAddress> peerIDtoAddress)
    {
        super(myPort, peerEpoch, id, peerIDtoAddress);
        super.setPeerState(ServerState.OBSERVER);
    }
    
    public GatewayPeerServerImpl(int myPort, long peerEpoch, long id, Map<Long, InetSocketAddress> peerIDtoAddress, int observerNodes)
    {
        super(myPort, peerEpoch, id, peerIDtoAddress, observerNodes);
        super.setPeerState(ServerState.OBSERVER);
    }

    @Override
    public void setPeerState(ServerState state)
    {
        super.setPeerState(ServerState.OBSERVER);
    }
}
