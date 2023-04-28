package edu.yu.cs.com3800;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public interface LoggingServer
{

    default Logger initializeLogging(String fileNamePreface) throws IOException
    {
        return initializeLogging(fileNamePreface,false);
    }
    default Logger initializeLogging(String fileNamePreface, boolean disableParentHandlers) throws IOException 
    {
        return createLogger("Logger " + fileNamePreface, fileNamePreface, disableParentHandlers);
    }

    static Logger createLogger(String loggerName, String fileNamePreface, boolean disableParentHandlers) throws IOException    
    {
        Logger logger = Logger.getLogger(loggerName);
        logger = Logger.getLogger(fileNamePreface);
        FileHandler fileHandler = null;
        try 
        {
            fileHandler = new FileHandler("src/main/java/edu/yu/cs/com3800/stage5/logs/log.log");
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();  
        fileHandler.setFormatter(formatter); 
        return logger;
    }
}