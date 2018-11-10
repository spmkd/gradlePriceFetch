package functions.db;

import objects.SingleGame;
import objects.Top25;
import settings.DataBaseConnectionDetails;

import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckAndUpdateMerchants {

    final static Log logger = LogFactory.getLog(CheckAndUpdateMerchants.class);

    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;

    public static void Process(Top25 top25){

        for (int i=0; i < top25.getSingleGameListArray().size() ; i++){

            SingleGame singleGame = top25.getSingleGameListArray().get(i);

            if (!singleGame.getName().isEmpty()) {
                CheckSingleGame(singleGame);
            }

        }

    }

    public static void CheckSingleGame(SingleGame singleGame){

        try{

            con = DriverManager.getConnection(DataBaseConnectionDetails.getURL(), DataBaseConnectionDetails.getUSER(), DataBaseConnectionDetails.getPASSWORD());
            st = con.createStatement();

            PreparedStatement preparedStatement = null;
            String selectSQL = "SELECT * FROM PRICE_FETCH.AllMerchants WHERE merchantName = ?";

            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, singleGame.getMerchant());

            rs = preparedStatement.executeQuery();

            // If no result is returned we need to create a new entry
            if (!rs.next()){
                String SQL_INSERT = "INSERT INTO PRICE_FETCH.AllMerchants (merchantName) VALUES (?)";

                try(PreparedStatement statement = con.prepareStatement(SQL_INSERT)){
                    statement.setString(1, singleGame.getMerchant());
                    statement.executeUpdate();
                }
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

}
