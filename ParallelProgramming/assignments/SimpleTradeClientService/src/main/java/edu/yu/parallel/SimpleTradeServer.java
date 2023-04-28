package edu.yu.parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleTradeServer
{
    private static Socket clientSocket;
    private ITradingAccountImpl tradingAccount;

    public SimpleTradeServer() {
        this.tradingAccount = new ITradingAccountImpl();
    }

    public static void main(String [] args) throws IOException 
    {       
        SimpleTradeServer server = new SimpleTradeServer();
        server.runMain();
    }

    private void runMain()
    {
        final int port = Integer.parseInt(System.getProperty("port", "8625"));
        System.out.format("Starting server on port %d ...", port);

        try(ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("Server started");
            while(true) 
            {
                clientSocket = serverSocket.accept();
                ThreadForClient tfc = new ThreadForClient();
                Thread newClient = new Thread(tfc);
                newClient.start();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
    
    public class ThreadForClient extends Thread 
    {
        @Override
        public void run() 
        {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) 
                {
                    // Separate "BUY|SELL" and "AMOUNT" OR handle "BALANCES" and "QUIT"
                    String[] info = inputLine.split(" ",0);
                    if(info.length > 2)
                    {
                        out.println("Unhandled Input");
                    }
                    String command = info[0].toUpperCase();

                    switch(command)
                    {
                        case "EXIT":
                        Thread Thread4 = Thread.currentThread();
                        out.println("[" + Thread4.getName() +":" + Thread4.getId() +":" + Thread4.getPriority() + "],GoodBye !"); 
                        clientSocket.close();
                        break;
                        case "BALANCES": 
                        String balances = tradingAccount.getBalanceString();
                        Thread Thread3 = Thread.currentThread();
                        out.println("[" + Thread3.getName() +":" + Thread3.getId() +":" + Thread3.getPriority() + "]," + balances);
                        break;
                        case "BUY": 
                        if(info.length < 2)
                        {
                            out.println("Invalid or missing trade amount");
                            break;
                        }
                        try
                        {
                            Double buyAmount = Double.parseDouble(info[1]);
                            if(buyAmount < 0)
                            {
                                Thread currentThread = Thread.currentThread();
                                out.println("["+currentThread.getName() +":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Invalid or missing trade amount"); 
                                break;
                            }
                            tradingAccount.Buy(buyAmount);
                            Thread currentThread = Thread.currentThread();
                            out.println("["+currentThread.getName() +":" + currentThread.getId() + ":" + currentThread.getPriority() +"],PrevPos=" + (-(tradingAccount.getCashBalance() - 1000000 + buyAmount)) + ",BUY=" + buyAmount + ",NewPos=" + (1000000 - tradingAccount.getCashBalance()));
                        }                        
                        catch(Exception e)
                        {
                            Thread currentThread = Thread.currentThread();
                            out.println("["+currentThread.getName() +":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Invalid or missing trade amount"); 
                            break;
                        }
                        break;
                        case "SELL":
                        if(info.length < 2)
                        {
                            Thread currentThread = Thread.currentThread();
                            out.println("["+currentThread.getName() +":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Invalid or missing trade amount");
                            break;
                        }
                        try
                        {
                            Double sellAmount = Double.parseDouble(info[1]);
                            if(sellAmount < 0)
                            {
                                Thread currentThread = Thread.currentThread();
                                out.println("[" + currentThread.getName() + ":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Invalid or missing trade amount"); 
                                break;
                            }
                            tradingAccount.Sell(sellAmount);
                            Thread currentThread = Thread.currentThread();
                            out.println("[" + currentThread.getName() + ":" + currentThread.getId() + ":" + currentThread.getPriority() + "],PrevPos=" + ((-(tradingAccount.getCashBalance() - 1000000)) + sellAmount) + ",SELL=" + sellAmount + ",NewPos=" + (-(tradingAccount.getCashBalance() - 1000000)));
                        }                        
                        catch(Exception e)
                        {
                            Thread currentThread = Thread.currentThread();
                            out.println("[" + currentThread.getName() + ":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Invalid or missing trade amount"); 
                            break;
                        }
                        break;
                        default:
                        Thread currentThread = Thread.currentThread(); 
                        out.println("[" + currentThread.getName() + ":" + currentThread.getId() + ":" + currentThread.getPriority() +"],Unhandled Input");
                        break;
                    }

                    // Print the responses like he says
                    // Done...

                }
            } 
            catch (IOException e) 
            {
                System.out.println("Error client socket closed");
            }
            
        }
    }
}



