package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stephen on 2014/12/23.
 */
public class BlsDataReader {
    private Context ctx;
    private BlsTemplate[] templateList = null;
    private String jsonStr;
    //private String BlsDataTestStr = "";

    //this file contains all the information about all bls templates in the assets folder
    private static final String TEMPLATE_LIST_NAME = "template_list.json";
    private static final String TEMPLATE_LIST_OBJ_KEY = "template";
    private static final String BLSDATA_OBJ_KEY = "data";

    public BlsDataReader(Context ctx){
        this.ctx = ctx;

        //load the data from the assets folder
        templateList = loadTemplateData();
    }

    private BlsTemplate[] loadTemplateData(){

        BlsTemplate[] templateFromJSON = null;
        //read json file to json string
        try {

            InputStream is = ctx.getAssets().open(TEMPLATE_LIST_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonStr = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        //parse json string and create BlsTemplate objects
        try{
            JSONObject obj = new JSONObject(jsonStr);
            JSONArray jsonArr = obj.getJSONArray(TEMPLATE_LIST_OBJ_KEY);

            if (jsonArr != null){
                templateFromJSON = new BlsTemplate[jsonArr.length()];
                BlsTemplateFactory tmpFactory = new BlsTemplateFactory();
                for (int i=0;i<jsonArr.length();i++){
                    JSONObject tmpObj = jsonArr.getJSONObject(i);
                    String title = tmpObj.getString("title");
                    String typeStr = tmpObj.getString("type");
                    String completePathStr = tmpObj.getString("path") + tmpObj.getString("filename");

                    templateFromJSON[i] = tmpFactory.getTemplate(typeStr, title, getBlsData(completePathStr));
                }
            }

        } catch (Exception e){

        }
        return templateFromJSON;
    }

    public String getJSONStr(){
        return jsonStr;
    }

    private BlsData[] getBlsData(String fileName){
        //TODO: load BlsData from "templateName".json file
        BlsData[] tmpBlsData = null;
        String tmpJsonStr;


        //read json file to json string
        try {

            InputStream is = ctx.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            tmpJsonStr = new String(buffer, "UTF-8");
            //BlsDataTestStr += tmpJsonStr;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }


        //parse json string and create BlsTemplate objects
        try{
            JSONObject obj = new JSONObject(tmpJsonStr);
            JSONArray jsonArr = obj.getJSONArray(BLSDATA_OBJ_KEY);

            if (jsonArr != null){
                tmpBlsData = new BlsData[jsonArr.length()];
                for (int i=0;i<jsonArr.length();i++){
                    JSONObject tmpObj = jsonArr.getJSONObject(i);
                    String title = tmpObj.getString("title");
                    String[] urlArr;
                    String[] descriptionArr;
                    String[] orderArr;

                    //url
                    JSONArray urlJSONArr = tmpObj.getJSONArray("url");
                    urlArr = new String[urlJSONArr.length()];
                    for (int j=0;j<urlJSONArr.length();j++)
                        urlArr[j] = urlJSONArr.getString(j);


                    //description
                    JSONArray descriptionJSONArr = tmpObj.getJSONArray("description");
                    descriptionArr = new String[descriptionJSONArr.length()];
                    for (int j=0;j<descriptionJSONArr.length();j++)
                        descriptionArr[j] = descriptionJSONArr.getString(j);


                    //order
                    JSONArray orderJSONArr = tmpObj.getJSONArray("order");
                    orderArr = new String[orderJSONArr.length()];
                    for (int j=0;j<orderJSONArr.length();j++)
                        orderArr[j] = orderJSONArr.getString(j);

                    tmpBlsData[i] = new BlsData(title, urlArr, descriptionArr, orderArr);

                }
            }

        } catch (Exception e){

        }

        return tmpBlsData;
    }

    /*public String getBlsTestStr(){
        return BlsDataTestStr;
    }*/

    public BlsTemplate[] getTemplates(){
        return templateList;
    }
}
