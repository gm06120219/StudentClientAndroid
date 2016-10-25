package com.example.liguangming.student;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    MineHandler mineHandler;
    AppCompatActivity editActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editActivity = this;

        // 1. get intent parameters
        Bundle bundle = getIntent().getExtras();
        Log.i("gm", String.format("onCreate: name=>%s, classid=>%s, sex=>%s", bundle.getString("name"), bundle.getString("classid"), bundle.getString("sex")));

        // 2. set init value
        final TextView tvPrev = (TextView) findViewById(R.id.tvPrev);
        final EditText ed_name = (EditText) findViewById(R.id.edName);
        final EditText ed_classid = (EditText) findViewById(R.id.edClassid);
        final RadioGroup rgSex = (RadioGroup) findViewById(R.id.rgSex);
        tvPrev.setText(bundle.getString("id"));
        ed_name.setText(bundle.getString("name"));
        ed_classid.setText(bundle.getString("classid"));
        if (bundle.getString("sex").equals("w")) {
            rgSex.check(R.id.rbFemale);
        } else {
            rgSex.check(R.id.rbMale);
        }

        // 3. set message handle and button listener
        mineHandler = new MineHandler() {
            @Override
            public void handleMessage(Message msg) {
                // get result
                Log.i("gm", "handleMessage: " + msg.obj.toString());
                finish();
            }
        };

        Button btn_save = (Button) findViewById(R.id.btnSave);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed_name.getText().toString();
                String id = tvPrev.getText().toString();
                int classid = Integer.valueOf(ed_classid.getText().toString());
                String sex = rgSex.getCheckedRadioButtonId() == R.id.rbMale ? "m" : "w";
                Log.i("gm", String.format("onClick: id=>%s, name=>%s, classid=>%d, sex=>%s, session_token=>%s", id, name, classid, sex, editActivity.getSharedPreferences("student_system", 0).getString("session_token", "")));
                Map params = new HashMap();
                params.put("id", id);
                params.put("name", name);
                params.put("sex", sex);
                params.put("classid",  String.valueOf(classid));
                params.put("session_token", editActivity.getSharedPreferences("student_system", 0).getString("session_token", ""));
                ApiRequester.Modify(editActivity, mineHandler, params);
            }
        });
    }
}
