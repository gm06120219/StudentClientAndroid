package com.example.liguangming.student;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Map;

import static com.example.liguangming.student.MineHandler.HANDLE_MESSAGE_LIST;

/**
 * Created by liguangming on 2016/10/21.
 */

public class ApiRequester {

    protected Map params;
    protected String idcDomain = "";
    protected AppCompatActivity activity;

    public ApiRequester(AppCompatActivity ac) {

        this.activity = ac;
        SharedPreferences sharedPreferences = this.activity.getSharedPreferences("student_system", 0);
        idcDomain = "http://" + sharedPreferences.getString("server", "").toString() + "/student_system_api/";
    }


    public void Add(final Handler handler, Map param_map) {
        params = param_map;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Object v : params.values()) {
                    Log.i("gm", "value= " + v);
                }
                String url = idcDomain + "student.php?action=add_student";
                String strResult = HttpUtils.submitPostData(url, params, "utf-8");

                Log.i("gm", "run: result=>" + strResult);
                Message msg = new Message();
                msg.obj = strResult;
                handler.sendMessage(msg);
            }
        });

        t.start();
    }

    static public void Delete(final AppCompatActivity ac, final Handler h, final Map p) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = ac.getSharedPreferences("student_system", 0);

                String url = "http://" + sharedPreferences.getString("server", "").toString() + "/student_system_api/" + "student.php?action=delete_student";

                String strResult = HttpUtils.submitPostData(url, p, "utf-8");

                Log.i("gm", "List: result=>" + strResult);
                Message msg = new Message();
                msg.obj = strResult;
                msg.what = MineHandler.HANDLE_MESSAGE_REFRESH_UI;
                h.sendMessage(msg);
            }
        });

        t.start();
    }

    static public void List(final AppCompatActivity ac, final Handler h, final Map p) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = ac.getSharedPreferences("student_system", 0);

                String url = "http://" + sharedPreferences.getString("server", "").toString() + "/student_system_api/" + "student.php?action=list_student";

                String strResult = HttpUtils.submitPostData(url, p, "utf-8");

                Log.i("gm", "List: result=>" + strResult);
                Message msg = new Message();
                msg.obj = strResult;
                msg.what = MineHandler.HANDLE_MESSAGE_LIST;
                h.sendMessage(msg);
            }
        });

        t.start();

    }

    static public void Modify(final AppCompatActivity ac, final Handler h, final Map p) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = ac.getSharedPreferences("student_system", 0);

                String url = "http://" + sharedPreferences.getString("server", "").toString() + "/student_system_api/" + "student.php?action=modify_student";

                String strResult = HttpUtils.submitPostData(url, p, "utf-8");

                Log.i("gm", "List: result=>" + strResult);
                Message msg = new Message();
                msg.obj = strResult;
                h.sendMessage(msg);
            }
        });

        t.start();

    }
    static public void Find(final AppCompatActivity ac, final Handler h, final Map p) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = ac.getSharedPreferences("student_system", 0);

                String url = "http://" + sharedPreferences.getString("server", "").toString() + "/student_system_api/" + "student.php?action=find_student";

                String strResult = HttpUtils.submitPostData(url, p, "utf-8");

                Log.i("gm", "List: result=>" + strResult);
                Message msg = new Message();
                msg.obj = strResult;
                msg.what = MineHandler.HANDLE_MESSAGE_FIND;
                h.sendMessage(msg);
            }
        });

        t.start();

    }
}
