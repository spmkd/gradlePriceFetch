package functions;

import objects.SingleGame;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GenerateTop25 {

    public static objects.Top25 Process(String pageHTML) {

        ArrayList<SingleGame> singleGameListArray = new ArrayList<SingleGame>();
        String date;

        date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

        singleGameListArray = ParseAllGames(pageHTML);

        objects.Top25 top25 = new objects.Top25();

        top25.setDate(date);
        top25.setSingleGameListArray(singleGameListArray);

        return top25;
    }

    public static ArrayList<SingleGame> ParseAllGames(String pageHTML) {

        ArrayList<SingleGame> singleGameListArrayParsed = new ArrayList<SingleGame>();

        Document doc = Jsoup.parse(pageHTML);

        Elements gamesName = doc.select("#Top25 .topclick-list-element-game-title");
        Elements gamesMerchant = doc.select("#Top25 .topclick-list-element-game-merchant");
        Elements gamesPrice = doc.select("#Top25 .topclick-list-element-price");

        for (int i=0; i<gamesName.size(); i++){

            SingleGame singleGame = new SingleGame();

            singleGame.setName(gamesName.get(i).ownText());
            singleGame.setMerchant(gamesMerchant.get(i).ownText());
            singleGame.setRank(i+1); //rank should not start at 0
            singleGame.setPrice(removeCurrencySign(gamesPrice.get(i).ownText()));

            singleGameListArrayParsed.add(singleGame);

        }

        return singleGameListArrayParsed;

    }

    private static BigDecimal removeCurrencySign(String ownText) {

        String truncatedPrice = "";

        truncatedPrice = ownText.substring(0,ownText.length()-1);

        return new BigDecimal(truncatedPrice);

    }

}
