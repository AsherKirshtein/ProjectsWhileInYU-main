package edu.yu.parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandLineTradePublisher {

    public static void main(String[] args) {
        final String host = System.getProperty("host", "localhost");
        final int port = Integer.parseInt(System.getProperty("port", "8625"));

        System.out.format("Connecting to server on port %d ...", port);

        try (Socket socket = new Socket(host, port))
        {
            System.out.println(" Done");

            Scanner keyboard = new Scanner(System.in);
            Scanner scanner = new Scanner(System.in);
            while (true) 
            {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(scanner.nextLine());
                System.out.println(in.readLine());
            }

        } 
        catch (IOException ex) 
        {
            System.out.println("I/O error: " + ex.getMessage());
        }
        catch (NoSuchElementException e)
        {
            System.out.println("No more command line input");
        }
    }
}
