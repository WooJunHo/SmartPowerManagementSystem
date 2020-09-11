package com.example.smartpowerclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static java.security.AccessController.getContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;



public class User extends AppCompatActivity {

    float npower = 0.0f;
    float rpower = 0.0f;
    float lpower = 1000.0f;
    boolean m_bSw = true;
    private IntentIntegrator qrScan;

    String m_sGetPowerOulet ;
    private String htmlPageUrl = "http://192.168.219.100/";
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private PrintWriter sockPrintWriter;
    private TextView _TVpOulet,_LimitPower,_NowPower,_RemaningPower;
    private Button pobtn;
    private GetDataMyThread gdth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final Notification.Builder builder = new Notification.Builder(getApplicationContext());
        final NotificationManager notificationManager =
                (NotificationManager)User.this.getSystemService(User.this.NOTIFICATION_SERVICE);
        final Intent intent = new Intent(User.this.getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Switch sw = (Switch) findViewById((R.id.powersw));
         _TVpOulet=(TextView) findViewById(R.id.PowerOulet);
         _LimitPower=(TextView) findViewById(R.id.LimitPower);
         _NowPower=(TextView) findViewById(R.id.nPower);
         _RemaningPower=(TextView) findViewById(R.id.rPower);
         pobtn=(Button) findViewById((R.id.Pogive));
        //intializing scan object
        qrScan = new IntentIntegrator(this);
        if(GetData("arduinoip")!=null) {
            htmlPageUrl = GetData("arduinoip");
        }
        gdth=new GetDataMyThread();
       // gdth.start();
        //scan option
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked) {
                   m_bSw=true;
                    Bitmap mLargeIconForNoti= BitmapFactory.decodeResource(getResources(),R.drawable.download);
                    PendingIntent pendnoti = PendingIntent.getActivity(User.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder        .setSmallIcon(R.drawable.download).
                            setContentTitle("남은전기량알림").
                            setContentText("사용가능전기량 50%이하")
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setLargeIcon((mLargeIconForNoti))
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setAutoCancel(false);
                    notificationManager.notify(1, builder.build());
                } else {
                    m_bSw=false;

                }
            }
        });
        pobtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //scan option
                qrScan.setPrompt("Scanning...");
                //qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
        /*
        if(NPowerPercentCal()<=50)
        {
            Bitmap mLargeIconForNoti= BitmapFactory.decodeResource(getResources(),R.drawable.download);
            PendingIntent pendnoti = PendingIntent.getActivity(User.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder        .setSmallIcon(R.drawable.download).
                            setContentTitle("남은전기량알림").
                            setContentText("사용가능전기량 50%이하")
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setLargeIcon((mLargeIconForNoti))
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setAutoCancel(false);
            notificationManager.notify(1, builder.build());
        }
        else if(NPowerPercentCal()<=10)
        {
            Bitmap mLargeIconForNoti= BitmapFactory.decodeResource(getResources(),R.drawable.download);
            PendingIntent pendnoti = PendingIntent.getActivity(User.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder
                            .setSmallIcon(R.drawable.download).
                            setContentTitle("남은전기량알림").
                            setContentText("사용가능전기량 50%이하")
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setLargeIcon((mLargeIconForNoti))
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setAutoCancel(false);
            notificationManager.notify(1, builder.build());
        }*/
    }
public float NPowerPercentCal()
{
    return npower/lpower*100;
}



    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(User.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(User.this, "스캔완료!", Toast.LENGTH_SHORT).show();

                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    gdth.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    Handler handler=new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                double a = 0;
                a = Math.random() * 100;
                new AsyncTask() {//AsyncTask객체 생성
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            Document doc;
                            doc = Jsoup.connect(htmlPageUrl).get();
                            Element mElementDataSize = doc.body();//필요한 녀석만 꼬집어서 지정
                            String str1 = mElementDataSize.select("p").text();
                            String str2=mElementDataSize.select("p").text();
                            String str3=mElementDataSize.select("p").text();
                            System.out.println(str1);
                            String _result_temp = str1.substring(5, 9);
                            String _result_current = str2.substring(16,20);
                        //    String _result_lpower=str3.substring(27,31);
                            System.out.println(_result_temp);
                            npower = Float.parseFloat(_result_temp);
                            npower = Float.parseFloat(_result_current);
                       //     lpower = Float.parseFloat(_result_lpower);
                            System.out.println(str1.length());
                            System.out.println("NowPower : " + npower);
                            System.out.println("LimitPower : " + lpower);
                            System.out.println("RemaningPower : " + rpower);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                }.execute();
            }
        }

    };


        private class GetDataMyThread extends Thread {
            @Override
            public void run() {
                while (true) {
                    handler.sendEmptyMessage(0);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _NowPower.setText("현재사용량 : " + Float.toString(npower)+" Wh");
                            _LimitPower.setText("전체한도량 : "+Float.toString(lpower)+"Wh");
                            _RemaningPower.setText("남은한도량 :" +Float.toString(rpower=lpower-npower)+"Wh");
                        }
                    });
                }
            }

        }
    public void MessageBox_F(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(User.this);
        builder.setTitle((title));
        builder.setMessage(message);
        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    private String GetData(String id_name) {
        String[] str = new String[5];
        String result_str = "";
        int idx = 0;
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/camdata"); // 저장 경로
// 폴더 생성
        if (!saveFile.exists()) { // 폴더 없을 경우
            MessageBox_F("오류", "설정값이 없습니다.");// 폴더 생성
            return null;
        }
        try {
            BufferedReader buf = new BufferedReader(new FileReader(saveFile + "/Setting.txt"));
            while ((buf.readLine()) != null) {
                str[idx] = buf.readLine();
                idx++;
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < str.length; i++) {
            if (str[idx] == id_name) {
                result_str = str[idx + 1];
                break;

            }
            return result_str;
        }
        return result_str;
    }


    }

