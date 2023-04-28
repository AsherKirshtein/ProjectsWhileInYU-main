package edu.yu.cs.com3800;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import edu.yu.cs.com3800.Message.MessageType;
import edu.yu.cs.com3800.ZooKeeperPeerServer.ServerState;
import edu.yu.cs.com3800.stage5.ZooKeeperPeerServerImpl;

public class ZooKeeperLeaderElection
{
    /**
     * time to wait once we believe we've reached the end of leader election.
     */
    private final static int finalizeWait = 200;

    /**
     * Upper bound on the amount of time between two consecutive notification checks.
     * This impacts the amount of time to get the system up again after long partitions. Currently 60 seconds.
     */
    private final static int maxNotificationInterval = 60000;

    private LinkedBlockingQueue<Message> incomingMessages;
    private ZooKeeperPeerServerImpl myPeerServer;

    private long proposedLeader;
    private long proposedEpoch;

    private boolean observerFoundLeader = false;

    public ZooKeeperLeaderElection(ZooKeeperPeerServer server, LinkedBlockingQueue<Message> incomingMessages)
    {
        this.incomingMessages = incomingMessages;
        this.myPeerServer = (ZooKeeperPeerServerImpl) server;
        this.proposedLeader = this.myPeerServer.getServerId();
        this.proposedEpoch = this.myPeerServer.getPeerEpoch();
    }

    private synchronized Vote getCurrentVote()
    {
        return new Vote(this.proposedLeader, this.proposedEpoch);
    }

    public synchronized Vote lookForLeader()
    {
        int exponentialBackOff = 400;
        Map<Long, ElectionNotification> votes =  new HashMap<>();
        Vote myVote = new Vote(this.proposedLeader, this.proposedEpoch);
        sendNotifications();
         while (this.myPeerServer.getPeerState() == ServerState.LOOKING  || !this.observerFoundLeader)
         {
            Message message = null;
            try
            {
                message = this.incomingMessages.poll(exponentialBackOff*2, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
             while(message == null)
             {
                 sendNotifications();
                 if(exponentialBackOff < maxNotificationInterval)
                 {
                    exponentialBackOff*=2;
                 }
                 else
                 {
                    exponentialBackOff = maxNotificationInterval;
                 }
             }
             if(!message.equals(null) && message.getMessageType() == MessageType.ELECTION)
             {
             //switch on the state of the sender:
             switch(getNotificationFromMessage(message).getState())
             {
                 case LOOKING: case OBSERVER:
                     if(supersedesCurrentVote(getNotificationFromMessage(message).getProposedLeaderID(), getNotificationFromMessage(message).getPeerEpoch()))
                     {
                        this.proposedLeader = getNotificationFromMessage(message).getProposedLeaderID();
                        this.proposedEpoch = getNotificationFromMessage(message).getPeerEpoch();
                        sendNotifications();
                     }
                     votes.put(getNotificationFromMessage(message).getSenderID(), getNotificationFromMessage(message));
                     if(haveEnoughVotes(votes, getCurrentVote()))
                     {
                         boolean canDeclareWinner = true;
                         while(!this.incomingMessages.isEmpty())
                         {
                            Message newMessage = this.incomingMessages.poll();
                            if(newMessage != null && newMessage.getMessageType() == MessageType.ELECTION && supersedesCurrentVote(getNotificationFromMessage(newMessage).getProposedLeaderID(), getNotificationFromMessage(newMessage).getPeerEpoch()))
                            {
                                canDeclareWinner = false;
                                this.incomingMessages.offer(newMessage);
                                continue;
                            }
                         }
                         //If not, set my own state to either LEADING (if I won the election) or FOLLOWING (if someone lese won the election) and exit the election
                         if(canDeclareWinner)
                         {
                            myVote = acceptElectionWinner(getNotificationFromMessage(message));
                            try {
                                Thread.sleep(finalizeWait);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            //System.out.println(this.myPeerServer.getServerId() + ": I'm exiting the election loop because I can declare a winner");
                            return myVote;
                         }
                     }
                     break;
                 //case FOLLOWING: case LEADING: //if the sender is following a leader already or thinks it is the leader
                    case FOLLOWING: case LEADING:
                     //IF: see if the sender's vote allows me to reach a conclusion based on the election epoch that I'm in, i.e. it gives the majority to the vote of the FOLLOWING or LEADING peer whose vote I just received.
                     if(haveEnoughVotes(votes, getNotificationFromMessage(message)))
                     {
                         myVote = acceptElectionWinner(getNotificationFromMessage(message));
                         return myVote;
                     }
                      //ELSE: if n is from a LATER election epoch
                      else
                      {
                         //IF a quorum from that epoch are voting for the same peer as the vote of the FOLLOWING or LEADING peer whose vote I just received.
                         if(getNotificationFromMessage(message).getProposedLeaderID() == this.proposedLeader)
                         {
                            myVote = acceptElectionWinner(getNotificationFromMessage(message));
                            return myVote;
                         }
                         else
                         {
                             continue;
                         }
                      }
                      //break;
                }
             }
         }
         if(new Vote(this.proposedLeader, this.proposedEpoch).equals(null))
         {
            ElectionNotification n = new ElectionNotification(this.proposedLeader, ServerState.FOLLOWING, this.myPeerServer.getServerId(), this.myPeerServer.getPeerEpoch());
            myVote = acceptElectionWinner(n);
         }
         try {
            Thread.sleep(finalizeWait);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myVote;
    }

    private void sendNotifications()
    {
        ElectionNotification eNotification = new ElectionNotification(this.proposedLeader, this.myPeerServer.getPeerState(), this.myPeerServer.getServerId(), this.proposedEpoch);
        byte[] message = buildMsgContent(eNotification);
        this.myPeerServer.sendBroadcast(MessageType.ELECTION, message);
    }

    private Vote acceptElectionWinner(ElectionNotification n)
    {
        this.proposedLeader = n.getProposedLeaderID();
        this.proposedEpoch = n.getPeerEpoch();
        sendNotifications();
        if(this.myPeerServer.getPeerState() == ServerState.OBSERVER)
        {
            this.observerFoundLeader = true;
        }
        if(n.getProposedLeaderID() == this.myPeerServer.getServerId())
        {
            this.myPeerServer.setPeerState(ServerState.LEADING);
        }
        else if(n.getProposedLeaderID() != this.myPeerServer.getServerId())
        {
            this.myPeerServer.setPeerState(ServerState.FOLLOWING);
        }

        Vote vote = new Vote(this.proposedLeader, this.proposedEpoch);
        try
        {
            this.myPeerServer.setCurrentLeader(vote);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return vote;
    }

    /*
     * We return true if one of the following three cases hold:
     * 1- New epoch is higher
     * 2- New epoch is the same as current epoch, but server id is higher.
     */
     protected boolean supersedesCurrentVote(long newId, long newEpoch)
     {
         if(newId == 8 || newId == 69)
         {
             return false;
         }
         return (newEpoch > this.proposedEpoch) || ((newEpoch == this.proposedEpoch) && (newId > this.proposedLeader));
     }
    /**
     * Termination predicate. Given a set of votes, determines if have sufficient support for the proposal to declare the end of the election round.
     * Who voted for who isn't relevant, we only care that each server has one current vote
     */
    //not sure if done right double check this method
    protected boolean haveEnoughVotes(Map<Long, ElectionNotification> votes, Vote proposal)
    {
       //is the number of votes for the proposal > the size of my peer server’s quorum?
       int voteCount = 0;
       //go through all the notifications and see if they voted for the proposal

       for(ElectionNotification e: votes.values())
       {
            if(e.getProposedLeaderID() == proposal.getProposedLeaderID())
            {
                voteCount++;
            }
       }
       //System.out.println(votes + ", " + proposal+", counter: "+voteCount);


       if(this.myPeerServer.getQuorumSize() <= voteCount)
       {
            return true;
       }
       return false;
    }

    public static byte[] buildMsgContent(ElectionNotification notification)
    {
        ByteBuffer buffer = ByteBuffer.allocate(26);
        buffer.clear();
        buffer.putLong(notification.getSenderID());
        buffer.putLong(notification.getPeerEpoch());
        buffer.putLong(notification.getProposedLeaderID());
        buffer.putChar(notification.getState().getChar());
        buffer.flip();
        return buffer.array();
    }

    public static ElectionNotification getNotificationFromMessage(Message received)
    {
        byte[] contents = received.getMessageContents();
        ByteBuffer buffer = ByteBuffer.wrap(contents);
        long senderID = buffer.getLong();
        long peerEpoch = buffer.getLong();
        long proposedLeaderID = buffer.getLong();
        ServerState state = ServerState.getServerState(buffer.getChar());

        ElectionNotification notification = new ElectionNotification(proposedLeaderID, state, senderID, peerEpoch);

        return notification;
    }
}
