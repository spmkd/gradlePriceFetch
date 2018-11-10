package functions.db;

import objects.SingleGameDB;
import objects.Top25DB;
import settings.DataBaseConnectionDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckAndUpdateTop25 {

    final static Log logger = LogFactory.getLog(CheckAndUpdateTop25.class);

    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;

    private static String LatestDate;

    public static void Process(Top25DB top25DB) {

        Top25DB latestTop25DB = new Top25DB();

        LatestDate = FindLastDateInDB();

        if (LatestDate == null){
            PopulateTop25DB(top25DB);
        }else{
            latestTop25DB = GenerateLastTop25DB();

            if (!FindDiferences(latestTop25DB, top25DB))
            {
                GenerateChangesField(top25DB, latestTop25DB);
                PopulateTop25DB(top25DB);
            }

        }

    }

    private static void GenerateChangesField(Top25DB top25DB, Top25DB latestTop25DB) {

        // This function needs to check:
        // 1. Is this the first time this game appears in the list (1 true, 0 false)
        // 2. If the price of the game changed from the previous check (-1 price has decreased, +1 price has increased, 0 no changes)
        // 3. Has the merchant changed (1 true, 0 false)
        // 4. If the rank has changed from the previous check (-1 rank has decreased, +1 rank has increased, 0 no changes)

        for (int i = 0; i < top25DB.getSingleGameListDBArray().size() ; i++){

            // Check whether the game appears in the Top25 list or it is a NEW game
            SingleGameDB singleGameDB = top25DB.getSingleGameListDBArray().get(i);

            GenerateChangesField_IsItNewGame(singleGameDB);
            GenerateChangesField_CheckPriceRankMerchant(singleGameDB, latestTop25DB);

        }

    }

    private static void GenerateChangesField_CheckPriceRankMerchant(SingleGameDB singleGameDB, Top25DB latestTop25DB) {

        // the game we are checking might not be present in the latest check, but might have been on the list previously
        boolean gameFound = false;

        for (int i = 0 ; i < latestTop25DB.getSingleGameListDBArray().size() ; i++ ) {

            if (singleGameDB.getName() == latestTop25DB.getSingleGameListDBArray().get(i).getName()){

                gameFound = true;

                // checking change in price
                int result = singleGameDB.getPrice().compareTo(latestTop25DB.getSingleGameListDBArray().get(i).getPrice());
                if (result == 0){
                    singleGameDB.setPriceChange((byte)0);
                }else if (result == 1){
                    singleGameDB.setPriceChange((byte) 1);
                }else{
                    singleGameDB.setPriceChange((byte) -1);
                }

                //checking change in merchant
                if (singleGameDB.getMerchant() == latestTop25DB.getSingleGameListDBArray().get(i).getMerchant()){
                    singleGameDB.setMerchantChange((byte) 0);
                }else{
                    singleGameDB.setMerchantChange((byte) 1);
                }

                //checking change in rank
                if (singleGameDB.getRank() == latestTop25DB.getSingleGameListDBArray().get(i).getRank()){
                    singleGameDB.setRankChange((byte) 0);
                }else if (singleGameDB.getRank() < latestTop25DB.getSingleGameListDBArray().get(i).getRank()) {
                    singleGameDB.setRankChange((byte) -1);
                }else {
                    singleGameDB.setRankChange((byte) 1);
                }

                break;
            }

        }

        if ( (singleGameDB.getIsThisNewGame() == 0) && (gameFound == false)){
            singleGameDB.setComeBack( (byte) 1);
        }

    }

    private static void GenerateChangesField_IsItNewGame(SingleGameDB singleGameDB) {

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT AllGameNames.gameTitle FROM Top25 INNER JOIN AllGameNames ON Top25.gameName = AllGameNames.id INNER JOIN AllMerchants ON Top25.Merchant = AllMerchants.id WHERE AllGameNames.id = " + singleGameDB.getName() + " order by Top25.Date desc, Top25.Rank  LIMIT 1;";

            preparedStatement = con.prepareStatement(selectSQL);

            rs = preparedStatement.executeQuery();

            if (rs.next()){
                singleGameDB.setIsThisNewGame((byte) 0);
            }else{
                singleGameDB.setIsThisNewGame((byte) 1);;
            }

        }catch (SQLException ex) {
            logger.error(ex.getMessage(),ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }

    }

    private static boolean FindDiferences(Top25DB latestTop25DB, Top25DB top25DB) {

        boolean areBothEqual=true;
        int numberOfiterations = 0;

        // occasionally it might happen that there here been less than 25 games, and the last and current number of games is not the same
        if (latestTop25DB.getSingleGameListDBArray().size() >= top25DB.getSingleGameListDBArray().size()){
            numberOfiterations = top25DB.getSingleGameListDBArray().size();
        }else{
            numberOfiterations = latestTop25DB.getSingleGameListDBArray().size();
        }

        for (int i=0 ; i < numberOfiterations ; i++)
        {

            SingleGameDB singleGameDBfromLatest = latestTop25DB.getSingleGameListDBArray().get(i);
            SingleGameDB singleGameDBfromTop25 = top25DB.getSingleGameListDBArray().get(i);

            if ( ( Objects.equals( singleGameDBfromLatest.getName() , singleGameDBfromTop25.getName() ) ) &&
                 ( Objects.equals( singleGameDBfromLatest.getMerchant() , singleGameDBfromTop25.getMerchant() ) ) &&
                 ( Objects.equals( singleGameDBfromLatest.getRank() , singleGameDBfromTop25.getRank() ) ) &&
                 ( Objects.equals( singleGameDBfromLatest.getPrice() , singleGameDBfromTop25.getPrice() ) ) )
            {
                // All are equal, need to check the next
            }else{
                logger.info("Checking object " + i);
                PrintDiferentObjects(top25DB.getSingleGameListDBArray().get(i),latestTop25DB.getSingleGameListDBArray().get(i));
                areBothEqual = false;
            }

        }

        return areBothEqual;
    }

    private static void PrintDiferentObjects(SingleGameDB singleGameDB, SingleGameDB singleGameDB1) {

        logger.info("Printing Diferences:");
        logger.info("Game Name: " + singleGameDB.getName() + " " + singleGameDB1.getName());
        logger.info("Price: " + singleGameDB.getPrice() + " " + singleGameDB1.getPrice());
        logger.info("Rank: " + singleGameDB.getRank() + " " + singleGameDB1.getRank());
        logger.info("Merchant: " + singleGameDB.getMerchant() + " " + singleGameDB1.getMerchant());
        logger.info("End of Printing Diferences");
    }

    private static void PopulateTop25DB(Top25DB top25) {

        for (int i=0; i < top25.getSingleGameListDBArray().size() ; i++){

            PopulateSingleEntry(top25.getSingleGameListDBArray().get(i),top25.getDate());

        }

    }

    private static void PopulateSingleEntry(SingleGameDB singleGameDB, String date) {

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            String SQL_INSERT = "INSERT INTO PRICE_FETCH.Top25 (Date, gameName, Rank, Merchant, Price, isThisNewGame, priceChange, merchantChange, rankChange, comeBack) VALUES (?,?,?,?,?,?,?,?,?,?)";

            try(PreparedStatement preparedStatement = con.prepareStatement(SQL_INSERT)){
                preparedStatement.setString(1, date);
                preparedStatement.setInt(2, singleGameDB.getName());
                preparedStatement.setInt(3, singleGameDB.getRank());
                preparedStatement.setInt(4, singleGameDB.getMerchant());
                preparedStatement.setBigDecimal(5, singleGameDB.getPrice());
                preparedStatement.setByte(6,singleGameDB.getIsThisNewGame());
                preparedStatement.setByte(7,singleGameDB.getPriceChange());
                preparedStatement.setByte(8,singleGameDB.getMerchantChange());
                preparedStatement.setByte(9,singleGameDB.getRankChange());
                preparedStatement.setByte(10,singleGameDB.getComeBack());
                preparedStatement.executeUpdate();
            }


        }catch (SQLException ex) {
            logger.error(ex.getMessage(),ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }

    }

    private static String FindLastDateInDB() {

        String dateToReturn = null;

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT Date FROM PRICE_FETCH.Top25 order by id desc limit 1";

            preparedStatement = con.prepareStatement(selectSQL);

            rs = preparedStatement.executeQuery();

            if (rs.next()){
                dateToReturn = rs.getString("Date").toString();
            }else{
                dateToReturn = null;
            }

        }catch (SQLException ex) {
            logger.error(ex.getMessage(),ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }

        return dateToReturn;

    }

    private static Top25DB GenerateLastTop25DB() {

        Top25DB top25DB = new Top25DB();
        ArrayList<SingleGameDB> singleGameDBArrayList = new ArrayList<SingleGameDB>();

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT * FROM PRICE_FETCH.Top25 WHERE Date = ?";

            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, String.valueOf(LatestDate));

            rs = preparedStatement.executeQuery();

            int i=0;
            while (rs.next()){

                SingleGameDB singleGameDB = new SingleGameDB();

                singleGameDB.setRank(rs.getInt("Rank"));
                singleGameDB.setName(rs.getInt("gameName"));
                singleGameDB.setMerchant(rs.getInt("Merchant"));
                singleGameDB.setPrice(rs.getBigDecimal("Price"));

                singleGameDBArrayList.add(singleGameDB);
            }

            top25DB.setSingleGameListDBArray(singleGameDBArrayList);

        }catch (SQLException ex) {
            logger.error(ex.getMessage(),ex);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }

        return top25DB;

    }

    // TODO Select GameNames and find ID
    // TODO Select MerchantName and find ID



}
