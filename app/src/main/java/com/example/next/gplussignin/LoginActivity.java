package com.example.next.gplussignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.next.gplussignin.utility.NetworkCheck;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 010;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton mBtnSignIn;
    private Button mBtnRevokeAccess, mBtnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mBtnSignIn = (SignInButton) findViewById(R.id.button_signIn);
       // mBtnSignOut = (Button) findViewById(R.id.btn_sign_out);
       // mBtnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);

        mBtnSignIn.setOnClickListener(this);
       // mBtnSignOut.setOnClickListener(this);
      //  mBtnRevokeAccess.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        mBtnSignIn.setSize(SignInButton.SIZE_STANDARD);
        mBtnSignIn.setScopes(gso.getScopeArray());


    }


    private void signIn()
    {
        NetworkCheck networkCheck = new NetworkCheck(LoginActivity.this);
        boolean isAvailable = networkCheck.checkInternetConn();
        if (isAvailable) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

    }




    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personPhotoUrl = String.valueOf(acct.getPhotoUrl());
            String email = acct.getEmail();

            //add the user information to the bundle
            Bundle bundle = new Bundle();
            bundle.putString("name", personName);
            bundle.putString("email",email);
            bundle.putString("profUrl", personPhotoUrl);
            Log.i("loginactivity", "handleSignInResult: "+personPhotoUrl);
            /*mDetailsFragment = new DetailsFragment(LoginActivity.this);
            mDetailsFragment.setArguments(bundle);
*/
           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
           intent.putExtra("data",bundle);
            startActivity(intent);
            finish();


            //getFragmentManager().beginTransaction().replace(R.id.frame,mDetailsFragment).commit();
            // Signed in successfully, show authenticated UI.


            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id) {
            case R.id.button_signIn:
                signIn();
                break;

            /*case R.id.btn_sign_out:
                signOut();
                break;

            case R.id.btn_revoke_access:
                revokeAccess();
                break;*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            mBtnSignIn.setVisibility(View.GONE);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog()
    {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("please wait while....");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn)
    {
        if (isSignedIn) {
            mBtnSignIn.setVisibility(View.GONE);
           // mBtnSignOut.setVisibility(View.VISIBLE);
           //mBtnRevokeAccess.setVisibility(View.VISIBLE);
        } else {
            mBtnSignIn.setVisibility(View.VISIBLE);
          // mBtnSignOut.setVisibility(View.GONE);
           // mBtnRevokeAccess.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }
}

