package com.example.next.gplussignin.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.next.gplussignin.fragment.HomeFragment;
import com.example.next.gplussignin.interfaces.IHomeList;
import com.example.next.gplussignin.model.HomeModel;
import com.example.next.gplussignin.utility.FileWriteNRead;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by next on 15/2/17.
 */

public class HomeController
{
    Context mContext;
    String mData = "";
ProgressDialog mDialog;
    public HomeController(Context mContext)
    {
        this.mContext = mContext;
    }

    public void getDetails(String urlget, final IHomeList homeList)
    {
        new FetchTodolistTask(this,homeList).execute(urlget);
    }

    private class FetchTodolistTask extends AsyncTask<String, Void, ArrayList<HomeModel>>
    {
        HomeController controller;
        IHomeList iHomeList;

        public FetchTodolistTask(HomeController controller, IHomeList iHomeList) {
            this.controller = controller;
            this.iHomeList = iHomeList;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("please wait.....");
            mDialog.show();
           // Toast.makeText(mContext, "onPreExecute", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList<HomeModel> doInBackground(String... params)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = "";
            ArrayList<HomeModel> modelArrayList = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream is = urlConnection.getInputStream();
                if (is == null) {
                    // Nothing to do.
                    mDialog.dismiss();
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(is));
                while ((mData = reader.readLine()) != null) {
                    result += mData;
                }
                modelArrayList = convertToJson(result);
                if (result != null) {
                    FileWriteNRead writeNRead = new FileWriteNRead();
                    writeNRead.writeToFile(result, mContext);


                }

            } catch (IOException e) {
                Log.e("MainActivity", "error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return modelArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<HomeModel> data)
        {
            super.onPostExecute(data);
            if (mDialog != null) {
                mDialog.dismiss();
            }
            iHomeList.getListData(data);


        }
    }

    //convert to json object
    public ArrayList<HomeModel> convertToJson(String data)
    {
    ArrayList<HomeModel> modelArrayList = new ArrayList<>();
        JSONObject json = null;
        try {

            json = new JSONObject(data);
            // json.getString("code");
            // json.getString("message");
            //retriving the json array
            Log.i("Main activity", "convertToJson: "+json.toString());
            JSONArray jsonArray=  json.getJSONArray("list");
            for (int i = 0; i <jsonArray.length() ; i++) {
                HomeModel model = new HomeModel();
                JSONObject childObject = jsonArray.getJSONObject(i);
                model.setsNo(childObject.getInt("sno"));
                model.setData(childObject.getString("data"));
                model.setUserId(childObject.getString("userid"));
                model.setTitle(childObject.getString("title"));
                model.setTime(childObject.getString("time"));

                //add the model class to arraylist
                modelArrayList.add(model);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return modelArrayList;
    }



}



