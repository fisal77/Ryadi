package com.appz_world_hackathon.ryadi.AppUtil.mApp;

import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
/*import android.widget.Toast;

import com.appz_world_hackathon.ryadi.API.Project.APIUtil.APIError;
import com.appz_world_hackathon.ryadi.API.Project.APIUtil.ErrorUtils;
import com.appz_world_hackathon.ryadi.API.Project.ApiClient.APIClient;
import com.appz_world_hackathon.ryadi.API.Project.ApiInterface.APIInterface;
import com.appz_world_hackathon.ryadi.API.Response.apiUSER.LOGIN.LoginResponse;
import com.appz_world_hackathon.ryadi.Activities.MainActivity;
import com.appz_world_hackathon.ryadi.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;*/

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;
//    APIInterface apiInterface;
    int PRIVATE_MODE = 0;
    // boolean userAccountEnabled;

    public boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }

/*    public void isAccountEnabled(String username){
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LoginResponse> call3 = apiInterface.USER_STATUS_RESPONSE_CALL(""+username);
        call3.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess()==1) {
                        ctx.startActivity(new Intent(ctx, MainActivity.class));
                    } else {
                        Toast.makeText(ctx, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(ctx, ctx.getApplicationContext().getResources().getString(R.string.user_account_disabled_msg),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    APIError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(ctx, "Message : " + apiError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(ctx, "Error : " + apiError.getError().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                call.cancel();
                Toast.makeText(ctx, ""+ctx.getResources().getString(R.string.toastNoInternet), Toast.LENGTH_SHORT).show();
            }
        });
    }*/


    private static final String PREF_NAME = "snow-intro-slider";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String IS_COMPLETED_PROFILE = "IsCompletedProfile";


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public void setCompletedProfile(boolean isCompletedProfile) {
        editor.putBoolean(IS_COMPLETED_PROFILE, isCompletedProfile);
        editor.commit();
    }

    public boolean isCompletedProfile() {
        return prefs.getBoolean(IS_COMPLETED_PROFILE, false);
    }

}