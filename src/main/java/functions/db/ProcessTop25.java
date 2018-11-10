package functions.db;

import objects.Top25;
import objects.SingleGameDB;
import objects.Top25DB;

public class ProcessTop25 {

    public static void ProcessObject(Top25  top25){

        Top25DB top25DB = new Top25DB();

        CheckAndUpdateGames.Process(top25);
        CheckAndUpdateMerchants.Process(top25);
        top25DB = CreateDBObjectFromTop25.Process(top25);
        CheckAndUpdateTop25.Process(top25DB);

    }

}
