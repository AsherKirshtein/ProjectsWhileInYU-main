package edu.yu.cs.com3800.stage1;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.SimpleServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.net.httpserver.*;

public class SimpleServerImpl implements SimpleServer
{
    //public SimpleServerImpl(int port) throws IOException
    HttpServer server = null;    
    static Logger logger;
    static FileHandler fileHandler;

    SimpleServerImpl(int port)
    { 
        logger = Logger.getLogger(SimpleServerImpl.class.getName());
        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage1/log.log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter);   
        logger.info("My first log");
    }

    public static void main(String[] args)
    {
        int port = 9000;
        if(args.length > 0)
        {
            port = Integer.parseInt(args[0]);
        }
        SimpleServer myserver = null;
        try
        {
            myserver = new SimpleServerImpl(port);
            myserver.start();
            logger.info("Server Started Successfully");
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            logger.info("Error Thrown when starting server\n" + e.getMessage());
            myserver.stop();
        }
    }


    /**
     * start the server
     */
    public void start()
    {
        HttpServer server = null;
        try 
        {
            server = HttpServer.create(new InetSocketAddress(9000), 0);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        server.createContext("/compileandrun", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    /**
     * stop the server
     */
    public void stop()
    {
        server.stop(0);
    }

    private class MyHandler implements HttpHandler
    {

        private MyHandler()
        {

        }
        @Override
        public void handle(HttpExchange t) throws IOException
        {
            InputStream is = t.getRequestBody();
            String requestMethod = t.getRequestMethod();
            if(!requestMethod.equals("POST"))
            {
                t.sendResponseHeaders(400, 0);
                t.close();
                logger.info("Didn't make a POST request; Please make a POST Request");
            }
            JavaRunner runner = new JavaRunner();
            try 
            {
                runner.compileAndRun(new ByteArrayInputStream(is.readAllBytes()));
            } 
            catch (IllegalArgumentException | ReflectiveOperationException e) 
            {
                String error = e.getMessage() + "\n";
                logger.info(error);
            }
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            logger.info("Got Response Successfully");   
        } 

    }
}