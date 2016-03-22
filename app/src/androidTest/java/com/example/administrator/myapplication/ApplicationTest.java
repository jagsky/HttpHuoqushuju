package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity {
    public static final String PATH = "http://192.168.10.102:8080/MySever/MySever";
    Button start_btn;
    //创建消息队列
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String name = bundle.getString("name");
            start_btn.setText(name);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.btn_1);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MyThread myThread = new MyThread();
                //myThread.start();
                DomeThread domeThread = new DomeThread();
                domeThread.start();
            }
        });

    }

    class DomeThread extends Thread {
        @Override
        public void run() {
            List<NameValuePair> parma = new java.util.ArrayList<NameValuePair>();
            parma.add(new BasicNameValuePair("name", "张三"));
            parma.add(new BasicNameValuePair("password", "123456"));

            try {
                //请求执行对象
                HttpClient client = new DefaultHttpClient();
                //设置请求方式
                HttpPost post = new HttpPost(PATH);
                //绑定参数
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parma);
                //把绑定参数设置到请求对象中
                post.setEntity(entity);
                //执行请求对象
                HttpResponse response = client.execute(post);
                //判断是否成功
                if (response.getStatusLine().getStatusCode() == 200) {
                    //获取服务器数据
                    String str = EntityUtils.toString(response.getEntity());
                    //获取消息池对象,打开消息池
                    Message msg = handler.obtainMessage();
                    //创建数据储存对象
                    Bundle bundle = new Bundle();
                    bundle.putString("name", str);
                    //设置Message数据
                    msg.setData(bundle);
                    //将消息对象发送给消息队列
                    handler.sendMessage(msg);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    *获取服务器数据。
    *
    **/
    class MyThread extends Thread {
        String str;

        @Override
        public void run() {
            try {
                //构造请求地址
                URL url = new URL(PATH);
                //打开连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置可读取服务器数据
                connection.setDoInput(true);
                //设置连接超时
                connection.setConnectTimeout(5000);
                //设置请求方式
                connection.setRequestMethod("POST");
                //请求连接
                connection.connect();
                //判断是否连接成功
                int code = connection.getResponseCode();
                Log.e("tag", code + "");

                if (code == 200) {
                    //通过Connection获取服务端的输入流
                    InputStream is = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    //读取服务器数据
                    while ((length = is.read(buffer)) != -1) {
                        str = new String(buffer, 0, length);
                        Log.i("tag", str);
                    }
                }

                //冲这个消息池中获取对象。
                Message msg = handler.obtainMessage();
                //创建数据存放对象
                Bundle bundle = new Bundle();
                bundle.putString("name", str);
                //把数据存放到msg中
                msg.setData(bundle);
                //hanlder发送数据
                handler.sendMessage(msg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
