package functions;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FetchHTML {

    final static Log logger = LogFactory.getLog(FetchHTML.class);

    public static String GetHTML(){

        String content = null;
        URLConnection connection = null;

        try {
            connection =  new URL("https://www.allkeyshop.com/blog/").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
        }catch ( Exception ex ) {
            logger.error(ex.getMessage(),ex);
        }

        return content;

    }

}
