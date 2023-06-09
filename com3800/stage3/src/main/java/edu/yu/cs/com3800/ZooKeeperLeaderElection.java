package edu.yu.cs.com3800;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.yu.cs.com3800.Message.MessageType;
import edu.yu.cs.com3800.ZooKeeperPeerServer.ServerState;

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
    private ZooKeeperPeerServer myPeerServer;

    private long proposedLeader;
    private long proposedEpoch;
    // static private Logger logger;
    // static private FileHandler fileHandler;
    

    public ZooKeeperLeaderElection(ZooKeeperPeerServer server, LinkedBlockingQueue<Message> incomingMessages)
    {   
        this.incomingMessages = incomingMessages;
        this.myPeerServer = server;
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
        //send initial notifications to other peers to get things started
         sendNotifications();
         //Loop, exchanging notifications with other servers until we find a leader
         while (this.myPeerServer.getPeerState() == ZooKeeperPeerServer.ServerState.LOOKING)
         {
             //Remove next notification from queue, timing out after 2 times the termination time
             Message message = null;
            try
            {
                message = this.incomingMessages.poll(exponentialBackOff*2, TimeUnit.MILLISECONDS);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
             //if no notifications received..
             while(message.equals(null))
             {
                 //..resend notifications to prompt a reply from others..
                 sendNotifications();
                 //.and implement exponential back-off when notifications not received..
                 if(exponentialBackOff < maxNotificationInterval)
                 {
                    exponentialBackOff*=2;
                 }
                 else
                 {
                    exponentialBackOff = maxNotificationInterval;
                 }
             }
             //if/when we get a message and it's from a valid server and for a valid server..
             if(!message.equals(null))
             {
             //switch on the state of the sender:
             switch(getNotificationFromMessage(message).getState())
             {
                 //case LOOKING: //if the sender is also looking
                 case LOOKING:
                     //if the received message has a vote for a leader which supersedes mine, change my vote and tell all my peers what my new vote is.
                     if(supersedesCurrentVote(getNotificationFromMessage(message).getProposedLeaderID(), getNotificationFromMessage(message).getPeerEpoch()))
                     {
                        this.proposedLeader = getNotificationFromMessage(message).getProposedLeaderID();
                        this.proposedEpoch = getNotificationFromMessage(message).getPeerEpoch();
                        sendNotifications();
                     }
                     //keep track of the votes I received and who I received them from.
                     votes.put(getNotificationFromMessage(message).getSenderID(), getNotificationFromMessage(message));
                     ////if I have enough votes to declare my currently proposed leader as the leader:
                     if(haveEnoughVotes(votes, getCurrentVote()))
                     {
                         //first check if there are any new votes for a higher ranked possible leader before I declare a leader. If so, continue in my election loop
                         boolean canDeclareWinner = true;
                         while(!this.incomingMessages.isEmpty())
                         {
                            Message newMessage = this.incomingMessages.poll();
                            if(supersedesCurrentVote(getNotificationFromMessage(newMessage).getProposedLeaderID(), getNotificationFromMessage(newMessage).getPeerEpoch()))
                            {
                                canDeclareWinner = false;
                                this.incomingMessages.offer(newMessage);
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
                            sendNotifications();
                            return myVote;
                         }
                     }
                     break;
                 //case FOLLOWING: case LEADING: //if the sender is following a leader already or thinks it is the leader
                    case FOLLOWING: case LEADING:
                     //IF: see if the sender's vote allows me to reach a conclusion based on the election epoch that I'm in, i.e. it gives the majority to the vote of the FOLLOWING or LEADING peer whose vote I just received.
                     if(haveEnoughVotes(votes, getNotificationFromMessage(message)))
                     {
                         //if so, accept the election winner.
                         myVote = acceptElectionWinner(getNotificationFromMessage(message));
                         //As, once someone declares a winner, we are done. We are not worried about / accounting for misbehaving peers.
                     }
                      //ELSE: if n is from a LATER election epoch
                      else
                      {
                         //IF a quorum from that epoch are voting for the same peer as the vote of the FOLLOWING or LEADING peer whose vote I just received.
                         if(getNotificationFromMessage(message).getProposedLeaderID() == this.proposedLeader)
                         {
                            //THEN accept their leader, and update my epoch to be their epoch
                            myVote = acceptElectionWinner(getNotificationFromMessage(message));
                         }
                         //ELSE:
                         else
                         {
                             //keep looping on the election loop.
                             continue;
                         }
                      }
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
        sendNotifications();
        return myVote;           
    }

    private void sendNotifications()
    {
        //iterates over map given in zookeeperimpl Inetsocket contains host and port being the values passed into the messages
        ElectionNotification eNotification = new ElectionNotification(this.proposedLeader, this.myPeerServer.getPeerState(), this.myPeerServer.getServerId(), this.proposedEpoch);
        byte[] message = buildMsgContent(eNotification);
        this.myPeerServer.sendBroadcast(MessageType.ELECTION, message);
    }

    private Vote acceptElectionWinner(ElectionNotification n)
    {
        //set my state to either LEADING or FOLLOWING
        this.proposedLeader = n.getProposedLeaderID();
        this.proposedEpoch = n.getPeerEpoch();
        sendNotifications();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //clear out the incoming queue before returning
        return vote;
    }

    /*
     * We return true if one of the following three cases hold:
     * 1- New epoch is higher
     * 2- New epoch is the same as current epoch, but server id is higher.
     */
     protected boolean supersedesCurrentVote(long newId, long newEpoch) 
     {
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
        ZooKeeperPeerServer.ServerState state = ZooKeeperPeerServer.ServerState.getServerState(buffer.getChar());

        ElectionNotification notification = new ElectionNotification(proposedLeaderID, state, senderID, peerEpoch);

        return notification;
    }
}
