package functions.db;

import objects.SingleGame;
import objects.Top25;
import settings.DataBaseConnectionDetails;

import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckAndUpdateGames {

    final static Log logger = LogFactory.getLog(CheckAndUpdateGames.class);

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
            String selectSQL = "SELECT * FROM PRICE_FETCH.AllGameNames WHERE GameTitle = ?";

            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, singleGame.getName());

            rs = preparedStatement.executeQuery();

            if (!rs.next()){
                String insertSQL = "INSERT INTO PRICE_FETCH.AllGameNames (gameTitle) VALUES (?)";

                try(PreparedStatement statement = con.prepareStatement(insertSQL)){
                    statement.setString(1, singleGame.getName());
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
