package functions.db;

import objects.SingleGame;
import objects.Top25;
import objects.SingleGameDB;
import objects.Top25DB;
import settings.DataBaseConnectionDetails;

import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateDBObjectFromTop25 {

    final static Log logger = LogFactory.getLog(CreateDBObjectFromTop25.class);

    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;

    public static Top25DB Process(Top25 top25) {

        Top25DB top25DB = new Top25DB();

        top25DB.setDate(top25.getDate());

        ArrayList<SingleGameDB> singleGameDBArrayList = new ArrayList<SingleGameDB>();

        for (int i=0; i < top25.getSingleGameListArray().size() ; i++){

            SingleGame singleGame = top25.getSingleGameListArray().get(i);

            if (!singleGame.getName().isEmpty()) {

                SingleGameDB singleGameDB = new SingleGameDB();

                singleGameDB.setMerchant( CheckSingleGameMerchant(singleGame));
                singleGameDB.setName( CheckSingleGameName(singleGame));
                singleGameDB.setRank( singleGame.getRank());
                singleGameDB.setPrice( singleGame.getPrice());

                singleGameDBArrayList.add(singleGameDB);
            }
        }

        top25DB.setSingleGameListDBArray(singleGameDBArrayList);

        return top25DB;
    }

    private static int CheckSingleGameName(SingleGame singleGame) {

        int singleGameName = 0;

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT id FROM PRICE_FETCH.AllGameNames WHERE gameTitle = ?";

            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, singleGame.getName());

            rs = preparedStatement.executeQuery();

            if (rs.next()){
                singleGameName = rs.getInt("id");
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

        return singleGameName;
    }

    private static int CheckSingleGameMerchant(SingleGame singleGame){

        int singleMerchantId = 0;

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT id FROM PRICE_FETCH.AllMerchants WHERE merchantName = ?";

            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, singleGame.getMerchant());

            rs = preparedStatement.executeQuery();

            if (rs.next()){
                singleMerchantId = rs.getInt("id");
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

        return singleMerchantId;

    }

}
