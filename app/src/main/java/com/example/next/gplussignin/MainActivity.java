package com.example.next.gplussignin;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.next.gplussignin.fragment.HomeFragment;
import com.example.next.gplussignin.model.HomeModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by next on 14/2/17.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                GoogleApiClient.OnConnectionFailedListener

{
    private ImageView mImageToolbar;
    private DrawerLayout mDrawer;
     TextView mTxtUserName, mTxtEmail;
     ImageView mProfileImage;
    private  ProgressDialog mProgressDialog;
    GoogleApiClient mGoogleApiClient;
    private static ListView mListView;
    private static ArrayList<HomeModel> mModelArrayList;
    private static boolean mIsNetwoekAvilable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        initView();
       // checkInternetConn();


        Bundle bundle = getIntent().getExtras().getBundle("data");
        if (bundle != null) {
            String name = bundle.getString("name");
            String email = bundle.getString("email");
            String profUrl = bundle.getString("profUrl");

            mTxtUserName.setText(name);
            mTxtEmail.setText(email);

            if (!profUrl.equalsIgnoreCase("null")) {
                Log.i("mainactivity", " if url not equal null: "+profUrl);
                // Execute DownloadImageTask AsyncTask
               new DownloadImageTask().execute(profUrl);
            }

        }





        //on click of toolbar icon function
        mImageToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.addTitle(MainActivity.this);

            }
        });

    }



    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    //initialize the id's
    private void initView()
    {
       // mListView = (ListView) findViewById(R.id.list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.plus_button));
        toolbar.setTitle(getResources().getString(R.string.app_name));

        mImageToolbar = (ImageView)findViewById(R.id.image_toolbar);

        //setSupportActionBar(toolbar);


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        initNavView(navHeaderView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //initialize the nav_header_view's
    private void initNavView(View navHeaderView)
    {
        mTxtUserName = (TextView) navHeaderView.findViewById(R.id.txt_nav_bar_name);
        mTxtEmail = (TextView) navHeaderView.findViewById(R.id.txt_nav_bar_emailId);
        mProfileImage = (ImageView) navHeaderView.findViewById(R.id.img_nav_bar_profile);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mImageToolbar.setVisibility(View.VISIBLE);
            // Handle the homme action
            getFragmentManager().beginTransaction().replace(R.id.frame_mainActivity, new HomeFragment()).addToBackStack("").commit();

            //gplus logout
        } else if (id == R.id.nav_logout) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();

            finish();

            //gplus revokes
        } else if (id == R.id.nav_revoke_access) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);

            Toast.makeText(getApplicationContext(),"revokes access", Toast.LENGTH_SHORT).show();

            finish();
        }


        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed:" + connectionResult);
    }

   /* //returns the model array list data interface method
    public static void modelList(final IHomeList iUpdateDesc) {
        iUpdateDesc.getListData(mModelArrayList);
    }*/


    // DownloadImageTask AsyncTask
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download profile Image ");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            mProfileImage.setImageBitmap(result);
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mImageToolbar.setVisibility(View.INVISIBLE);
    }
    /* //call the adapter
    public static void callAdapter(ArrayList<HomeModel> models, Context context) {

        HomeAdapter adapter =  new HomeAdapter(context, models);
        mListView.setAdapter(adapter);
    }

//read the data from file
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("test.json");

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

        convertToJson(ret);
        return ret;
    }
//convert to json object
    private  void convertToJson(String data) {
        ArrayList<HomeModel> modelArrayList = new ArrayList<>();
        JSONObject json = null;
        try {

             json = new JSONObject(data);
            json.getString("code");
            json.getString("message");
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
                callAdapter(modelArrayList,MainActivity.this);
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }*/


}

