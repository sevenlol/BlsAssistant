package stethoscope.com.blsassistant.blsmodel;

/**
 * Created by stephen on 2014/12/21.
 */
public class BlsTemplateFactory {
    public BlsTemplate getTemplate(String type, String title, BlsData[] data){
        if (type == null)
            return null;

        if (type.equalsIgnoreCase("GUIDE"))
            return new BlsGuide(title, data);
        else if (type.equalsIgnoreCase("MAP"))
            return new BlsMap();
        else if (type.equalsIgnoreCase("SEARCH")){
            String tmpTitleArr[] = {"CPR", "Burn Injury", "AED Map"};
            int tmpIndexArr[] = {0,1,2};
            return new BlsSearch();
        }
        else if (type.equalsIgnoreCase("TEST_GUIDE")){
            //test object for BlsGuide
            BlsData[] testBlsData = new BlsData[1];
            String[] testUrl = new String[1];
            testUrl[0] = "asset.png";
            String[] testDescription = new String[5];
            testDescription[0] = "Check for awareness.";
            testDescription[1] = "Call for help.";
            testDescription[2] = "Compression";
            testDescription[3] = "Airway";
            testDescription[4] = "Breathing";
            String[] testDisplayOrder = {"URL", "DESCRIPTION", "DESCRIPTION", "DESCRIPTION", "DESCRIPTION", "DESCRIPTION" };

            testBlsData[0] = new BlsData("CPR", testUrl, testDescription, testDisplayOrder);
            return new BlsGuide("CPR", testBlsData);
        }

        return null;
    }
}
