package com.example.liguangming.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1. get view controler
        Button btn_login = (Button) findViewById(R.id.btnLogin);
        final EditText ed_username = (EditText) findViewById(R.id.editUsername);
        final EditText ed_password = (EditText) findViewById(R.id.editPassword);
        final EditText ed_server = (EditText) findViewById(R.id.editServer);

        final String start_session_url = "/student_system_api/session.php?action=start_session";
        final Intent intent = new Intent(this, ListActivity.class);
        final SharedPreferences sharedPreferences = getSharedPreferences("student_system", 0);

        // 2. get SharedPreferences value
        String get_username = sharedPreferences.getString("username", "");
        String get_password = sharedPreferences.getString("password", "");
        String get_server = sharedPreferences.getString("server", "");
        Log.d("gm", "onCreate: get shared username=>" + get_username + "  password=>" + get_password + "  server=>" + get_server);

        // 3. init edit value
        ed_username.setText(get_username);
        ed_password.setText(get_password);
        ed_server.setText(get_server);

        // 4. on login
        btn_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 1. get input
                        String username = String.valueOf(ed_username.getText());
                        String password = String.valueOf(ed_password.getText());
                        String server = String.valueOf(ed_server.getText());

                        // 2. set SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("server", server);
                        editor.commit();

                        // 3. init request param
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", password);
                        Log.d("gm", "before login, param [username]:" + username + ", [password]:" + password + ", [server]:" + server);

                        // 4. send start_session request
                        String session_token = "";
                        String url = "http://" + server + start_session_url;
                        Log.d("gm", "run: url=>" + url);
                        String strResult = HttpUtils.submitPostData(url, params, "utf-8");
                        Log.d("gm", "start_session result: " + strResult);
                        try {
                            JSONObject result_json = new JSONObject(strResult);
                            Log.d("gm", "code: " + result_json.getString("code"));
                            if (result_json.getString("code").equals("SUCCESS_MSG")) {
                                Log.w("gm", "Login Success.");
                                JSONObject data_json = new JSONObject(result_json.getString("data"));
                                session_token = data_json.getString("session_token");
                                Log.d("gm", "session_token: " + session_token);

                                // push parameter to list activity
                                Bundle bundle = new Bundle();
                                bundle.putString("session_token", session_token);
                                intent.putExtras(bundle);

                                // save sessionToken and time
                                editor.putString("session_token", session_token);
                                editor.putString("time", String.valueOf(new Date().getTime()));
                                editor.commit();

                                // start list activity
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("gm", "Login Fail.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
