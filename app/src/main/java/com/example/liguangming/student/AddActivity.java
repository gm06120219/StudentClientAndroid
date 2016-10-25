package com.example.liguangming.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    public Handler mHandler;
    String name;
    String sex;
    int class_id;
    String session_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj != null && msg.obj != "") {
                    Log.i("gm", "handleMessage: " + msg.obj);
                    try {
                        JSONObject json_result = new JSONObject(msg.obj.toString());
                        if (json_result.getString("code").equals("SUCCESS_MSG")) {
                            new AlertDialog.Builder(AddActivity.this)
                                    .setTitle("Success Message")
                                    .setMessage("Add student infomation success.\nConfirm to return.").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    finish();
                                }
                            }).create().show();
                        } else {
                            Log.i("gm", "handleMessage: sorry!" + json_result.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Button btnSave = (Button) findViewById(R.id.btnSave);

        final EditText edName = (EditText) findViewById(R.id.edName);
        final EditText edClassid = (EditText) findViewById(R.id.edClassid);
        final RadioGroup rgSex = (RadioGroup) findViewById(R.id.rgSex);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                session_token = getSharedPreferences("student_system", 0).getString("session_token", "");

                // get name value
                name = String.valueOf(edName.getText());
                Log.i("gm", "onClick: name=>" + name);
                if (name == null || name == "" || name.length() == 0 || name.length() > 20) {
                    new AlertDialog.Builder(AddActivity.this).setTitle("Warning Message").setMessage("Please input right name, size less than 20.").setPositiveButton("Confirm", null).show();
                    return;
                }
                // get sex value
                int checked_id = rgSex.getCheckedRadioButtonId();
                if (checked_id == R.id.rbMale) {
                    sex = "m";
                } else if (checked_id == R.id.rbFemale) {
                    sex = "w";
                } else {
                    new AlertDialog.Builder(AddActivity.this).setTitle("Warning Message").setMessage("Please choose sex").setPositiveButton("Confirm", null).show();
                    return;
                }
                Log.i("gm", "onClick: sex=>" + sex);

                // get classid value
                try {
                    class_id = Integer.parseInt(String.valueOf(edClassid.getText()));
                    Log.i("gm", "onClick: class id=>" + class_id);
                } catch (NumberFormatException e) {
                    new AlertDialog.Builder(AddActivity.this).setTitle("Warning Message").setMessage("Please input class id").setPositiveButton("Confirm", null).show();
                    return;
                }
                add();
            }
        });
    }

    private void add() {
        Map params = new HashMap();
        params.put("name", name);
        params.put("session_token", session_token);
        params.put("sex", sex);
        params.put("classid", "" + class_id);

        new ApiRequester(this).Add(mHandler, params);
    }


}