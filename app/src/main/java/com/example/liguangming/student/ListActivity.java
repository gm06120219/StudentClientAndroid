package com.example.liguangming.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "gm ListActivity";
    MyAdapter adapter;
    Intent editIntent;
    Intent addIntent;
    Handler mHandler;
    JSONArray rows;
    boolean pause;
    String sessionToken;
    AppCompatActivity listActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. set viewer
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listActivity = this;

        // 2. init list view
        ListView lv = (ListView) findViewById(R.id.listViewVer);
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);
        addIntent = new Intent(this, AddActivity.class);

        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(addIntent);
            }
        });
        sessionToken = getSharedPreferences("student_system", 0).getString("session_token", "");
        if (sessionToken.equals("")) {
            // TODO session get null process
        }

        final EditText edSearch = (EditText) findViewById(R.id.edSearch);

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edSearch.getText().toString();
                Map params = new HashMap();
                params.put("session_token", listActivity.getSharedPreferences("student_system", 0).getString("session_token", ""));
                params.put("name", search);
                ApiRequester.Find(listActivity, mHandler, params);
            }
        });

        // 3. init message handler, process the api response
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case MineHandler.HANDLE_MESSAGE_REFRESH_UI:
                            Log.i("gm", "handleMessage: on process msg refresh ui.");
                            refresh();
                            break;

                        case MineHandler.HANDLE_MESSAGE_LIST:
                            Log.i("gm", "handleMessage: on process msg list.");
                            if (msg.obj != null && !msg.obj.equals("")) {
                                adapter.arr.clear();
                                String data = new JSONObject(msg.obj.toString()).getString("data");
                                if (!data.equals("")) {
                                    JSONObject json_data = new JSONObject(data);
                                    rows = new JSONArray(json_data.getString("row"));
                                    if (rows != null) {
                                        int len = rows.length();

                                        for (int i = 0; i < len; i++) {
                                            Log.i("gm", "refresh: " + rows.get(i).toString());
                                            adapter.arr.add(rows.get(i).toString());
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();

                                // TODO stop the loading animation
                            }
                        case MineHandler.HANDLE_MESSAGE_FIND:
                            Log.i(TAG, "handleMessage: " + msg.obj.toString());
                            break;

                        default:
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("gm", "onResume");
        refresh();
    }

    private void refresh() {
        Map param = new HashMap();
        param.put("session_token", sessionToken);
        ApiRequester.List(this, mHandler, param);
        // TODO add a loading animation
    }

    private class MyAdapter extends BaseAdapter {
        public Context context;
        private LayoutInflater inflater;
        public ArrayList<String> arr;
        final String delete_student_url = "/student_system_api/student.php?action=delete_student";

        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            arr = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            // 1. init view
            if (view == null) {
                view = inflater.inflate(R.layout.list_view_ver, null);
            }

            // 2. get controler
            final TextView tvId = (TextView) view.findViewById(R.id.tvId);
            final TextView tvName = (TextView) view.findViewById(R.id.tvName);
            final TextView tvSex = (TextView) view.findViewById(R.id.tvSex);
            final TextView tvClassid = (TextView) view.findViewById(R.id.tvClassid);
            final Button btnEdit = (Button) view.findViewById(R.id.btnEdit);
            final Button btnDelete = (Button) view.findViewById(R.id.btnDelete);

            // 3. set value
            String item = arr.get(position);
            try {
                JSONObject item_json = new JSONObject(item);
                tvId.setText(item_json.getString("id"));
                tvName.setText(item_json.getString("name"));
                tvSex.setText(item_json.getString("sex"));
                tvClassid.setText(item_json.getString("classid"));

                editIntent = new Intent(context, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", item_json.getString("id"));
                bundle.putString("name", item_json.getString("name"));
                bundle.putString("sex", item_json.getString("sex"));
                bundle.putString("classid", item_json.getString("classid"));
                editIntent.putExtras(bundle);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(editIntent);
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // init request param
                                Log.i("gm", "ListActivity: delete item => " + position + ", id => " + String.valueOf(tvId.getText()));
                                Map<String, String> params = new HashMap<String, String>();
                                SharedPreferences sharedPreferences = getSharedPreferences("student_system", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String server = sharedPreferences.getString("server", "");
                                String session_token = sharedPreferences.getString("session_token", "");
                                String url = "http://" + server + delete_student_url;
                                params.put("id", String.valueOf(tvId.getText()));
                                params.put("session_token", session_token);

                                // send request
                                ApiRequester.Delete(listActivity, mHandler, params);
                            }
                        });
                        thread.start();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }
}
