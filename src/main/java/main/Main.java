package main;

import functions.db.ProcessTop25;
import functions.FetchHTML;
import functions.GenerateTop25;
import objects.Top25;
import settings.ApplicationSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Main {

    final static Log logger = LogFactory.getLog(Main.class);

    public static void main(String args[]){

        Main main = new Main();
        main.start();
        logger.info("Starting Application");

    }

    private void start() {

        do {

            String timeStampStart = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
            logger.info("Starting a cycle at " + timeStampStart);
            //System.out.println("Starting a cycle at " + timeStampStart);


            String pageHTML;
            Top25 top25 = new Top25();

            pageHTML = FetchHTML.GetHTML();
            top25 = GenerateTop25.Process(pageHTML);
            ProcessTop25.ProcessObject(top25);

            String timeStampEnd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
            logger.info("Finished the cycle at " + timeStampEnd);
            //System.out.println("Finished the cycle at " + timeStampEnd);

            try {
                Thread.sleep(ApplicationSettings.SECONDS_FOR_REPETITION * 1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }

        }while(true);

    }

}
