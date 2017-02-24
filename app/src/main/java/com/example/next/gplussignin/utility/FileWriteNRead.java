package com.example.next.gplussignin.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.next.gplussignin.model.HomeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by next on 22/2/17.
 */
public class FileWriteNRead {
private static String TAG = "FileWriteNRead";
    public void writeToFile(String data, Context context) {
        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("test.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            Log.i(TAG, "writeToFile: "+data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("test.json");
            Log.i(TAG, "readFromFile: "+inputStream.toString());
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                 ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("main activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("main activity", "Can not read file: " + e.toString());
        }

        Log.i(TAG, "readFromFile: "+ret);
        return ret;
    }

    public void updateFile(ArrayList<HomeModel> homeModels, Context context) {
        Toast.makeText(context, "file size"+homeModels.size(), Toast.LENGTH_SHORT).show();
        JSONObject jResult = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray
        for (int i = 0; i <homeModels.size() ; i++) {
            JSONObject jGroup = new JSONObject();// /sub Object
            try {
                jGroup.put("sno", homeModels.get(i).getsNo());
                jGroup.put("data", homeModels.get(i).getData());
                jGroup.put("userid", homeModels.get(i).getUserId());
                jGroup.put("title", homeModels.get(i).getTitle());
                jGroup.put("time", homeModels.get(i).getTime());

                jArray.put(jGroup);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jResult.put("list", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeToFile(jResult.toString(), context);
        Log.i(TAG, "updateFile: "+homeModels.size()+jResult.toString());
    }
}
