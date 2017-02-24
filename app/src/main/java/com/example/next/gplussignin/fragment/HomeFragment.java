package com.example.next.gplussignin.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.next.gplussignin.MainActivity;
import com.example.next.gplussignin.R;
import com.example.next.gplussignin.adapter.HomeAdapter;
import com.example.next.gplussignin.controller.HomeController;
import com.example.next.gplussignin.controller.UpdateController;
import com.example.next.gplussignin.interfaces.IHomeList;
import com.example.next.gplussignin.model.HomeModel;
import com.example.next.gplussignin.utility.FileWriteNRead;
import com.example.next.gplussignin.utility.NetworkCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by next on 14/2/17.
 */
public class HomeFragment extends Fragment
{
   private static ListView mListView;
    private static boolean mIsNetwoekAvilable;
    private static ArrayList<HomeModel> mModelArrayList;
    private static String TAG = "HomeFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        intview(view);
        mModelArrayList = new ArrayList<>();

        //check the internet connection
        NetworkCheck networkCheck = new NetworkCheck(getActivity());
        mIsNetwoekAvilable = networkCheck.checkInternetConn();


        //if network is avilable call to server else fetch data from file
        if (mIsNetwoekAvilable) {
           // Toast.makeText(getActivity(), "call to server:"+mIsNetwoekAvilable, Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getActivity().getSharedPreferences("updateData", Context.MODE_PRIVATE);

            //update the data
            if (!preferences.getString("data", "").equalsIgnoreCase("")) {
                String updateData = preferences.getString("data","");
                UpdateController controller = new UpdateController(getActivity());
                controller.updateData(updateData, getResources().getString(R.string.post_url));
            }
            //call the controller class
            String internalEmpURL = getResources().getString(R.string.string_url);
            HomeController controller = new HomeController(getActivity());
            controller.getDetails(internalEmpURL, new IHomeList() {
                @Override
                public void getListData(ArrayList<HomeModel> models) {
                   mModelArrayList = models;
                    callAdapter(mModelArrayList, getActivity());
                }
            });
        } else {
           // Toast.makeText(getActivity(), "call to file", Toast.LENGTH_SHORT).show();
            FileWriteNRead nRead = new FileWriteNRead();
            String data = nRead.readFromFile(getActivity());
            Log.i(TAG, "onCreateView: "+data);
            convertToJson(data, getActivity());

        }
        return view;
    }

    private void intview(View view) {
        mListView = (ListView) view.findViewById(R.id.list_view);
    }

    //call the adapter
    public  static void callAdapter(ArrayList<HomeModel> models, Context context)
    {

        HomeAdapter adapter =  new HomeAdapter(context, models);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    //convert to json object
    public static void convertToJson(String data, Context context)
    {

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
                mModelArrayList.add(model);
               callAdapter(mModelArrayList,context);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    // the model array list data interface method
    public static void modelList(final IHomeList iUpdateDesc)

    {
        iUpdateDesc.getListData(mModelArrayList);
    }

        //add the new title
    public static void addTitle(final Context context)
    {
        Toast.makeText(context, "tools bar", Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(context);
      //  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.toolbar_dilog, null);
        final EditText edTxtTitle = (EditText) dialogView.findViewById(R.id.edtxt_dialog_toolbar) ;
        Button btnOk = (Button) dialogView.findViewById(R.id.btn_toolbar_dialog) ;

        dialog.setContentView(dialogView);
        dialog.setTitle("Title");
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edTxtTitle.getText().toString();
                Log.i("mainActivity", "onClick title: "+title);
                Toast.makeText(context,title, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (!title.equalsIgnoreCase("")) {
                    HomeModel homeModel = new HomeModel();
                    if (title.length()>20) {
                        String subStrng = title.substring(0, 20)+"...";
                        homeModel.setTitle(subStrng);
                    } else {
                        homeModel.setTitle(title);
                    }
                    homeModel.setData("");
                    homeModel.setTime("1233212222");
                    homeModel.setsNo(mModelArrayList.size()+1);
                    homeModel.setUserId("jyothishkm@helixsolutions.com");
                    mModelArrayList.add(homeModel);
                    // update the file
                    FileWriteNRead update = new FileWriteNRead();
                    update.updateFile(mModelArrayList, context);
                    Log.i(TAG, "onClick: "+mModelArrayList.size());
                    //call adapter class
                    callAdapter(mModelArrayList, context);
                }


            }
        });
    }
}
