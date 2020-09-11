package com.example.smartpowerclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Admin_Activity extends AppCompatActivity {
    public static final int sub = 1001;
    ArrayList m_SpinnerLIst;
    private Object View;
    LineChart chart1;
    LineChart chart2;
    private String htmlPageUrl = "http://192.168.219.100/";
    private float m_fnow_Temp=0.0f;
    private float m_fnow_Current=0.0f;
    private Description jsoupAsyncTask;
    private Switch powersw;
    private void POSearch (String PowerOUlet_name)
{


}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_);
       // Intent intent = new Intent(this, Login_Activity.class);
        //
        if(GetData("arduinoip")!=null) {
            htmlPageUrl = GetData("arduinoip");
        }
         Spinner m_Spinner=(Spinner) findViewById(R.id.spinner);
        jsoupAsyncTask = new Description();
       // jsoupAsyncTask.execute();
        powersw=findViewById(R.id.switch1);
         GetDataThread gth=new GetDataThread();

        chart1 = (LineChart) findViewById(R.id.chart);
        chart2 = (LineChart) findViewById(R.id.chart1);
ChartInit(chart1);
ChartInit(chart2);
gth.start();

// don't forget to refresh the drawing
//        raw_Chart.invalidate();

}
public void ChartInit(LineChart chart){



    chart.setDrawGridBackground(true);
    chart.setBackgroundColor(Color.BLACK);
    chart.setGridBackgroundColor(Color.BLACK);

// touch gestures (false-비활성화)
    chart.setTouchEnabled(false);

// scaling and dragging (false-비활성화)
    chart.setDragEnabled(false);
    chart.setScaleEnabled(false);

//auto scale
    chart.setAutoScaleMinMaxEnabled(true);
//chart.setScaleX(float);
    //chart.setScaleY(float):
// if disabled, scaling can be done on x- and y-axis separately
    chart.setPinchZoom(false);

//X축
    chart.getXAxis().setDrawGridLines(true);
    chart.getXAxis().setDrawAxisLine(false);

    chart.getXAxis().setEnabled(true);
    chart.getXAxis().setDrawGridLines(false);

//Legend
    Legend l = chart.getLegend();
    l.setEnabled(true);
    l.setFormSize(10f); // set the size of the legend forms/shapes
    l.setTextSize(12f);
    l.setTextColor(Color.WHITE);

//Y축
    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.setEnabled(true);
    leftAxis.setTextColor(getResources().getColor(R.color.colorAccent));
    leftAxis.setDrawGridLines(true);
    leftAxis.setGridColor(getResources().getColor(R.color.colorAccent));

    YAxis rightAxis = chart.getAxisRight();
    rightAxis.setEnabled(false);
}
Handler handler=new Handler(){
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if(msg.what==0){
            double a=0;
            a=Math.random()*100;
            new AsyncTask() {//AsyncTask객체 생성
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Document doc;
                        doc = Jsoup.connect(htmlPageUrl).get();
                        Element mElementDataSize = doc.body();//필요한 녀석만 꼬집어서 지정
                        String str1 = mElementDataSize.select("p").text();
                        String str2 = mElementDataSize.select("p").text();
                        System.out.println(str1);
                        String _result_temp=str1.substring(5,9);
                        String _result_current= str2.substring(16,20);
                        System.out.println(_result_temp);
                        m_fnow_Temp=Float.parseFloat(_result_temp);
                        m_fnow_Current=Float.parseFloat(_result_current);
                        System.out.println(m_fnow_Temp);
                        System.out.println(m_fnow_Current);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }.execute();
            addEntry(chart1,m_fnow_Temp);
            addEntry(chart2,m_fnow_Current);
        }
    }
};
private class GetDataThread extends Thread{
        @Override
    public void run(){
            while(true){
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
}
    private void addEntry(LineChart chart,double num) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }



        data.addEntry(new Entry((float)set.getEntryCount(), (float)num), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(150);
        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);

    }
    private LineDataSet createSet() {



        LineDataSet set = new LineDataSet(null, "Real-time Line Data");
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setValueTextColor(Color.WHITE);
        set.setColor(Color.WHITE);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));

        return set;
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent,sub);
    }
    public void MessageBox_F(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Activity.this);
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
