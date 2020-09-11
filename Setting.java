package com.example.smartpowerclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Setting extends AppCompatActivity {
    String arduinoip;
    String set_Id;
    String set_Password;
    EditText ArduinoIp,Set_Id,Set_Password;
    public static final int sub = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ArduinoIp=(EditText) findViewById(R.id.input_ArduinoIp);
        Set_Id=(EditText) findViewById(R.id.input_ID);
        Set_Password=(EditText) findViewById(R.id.input_Pass);

    }
    public void OnBtnClick(View view)
    {
        arduinoip=ArduinoIp.getText().toString();
        set_Id=Set_Id.getText().toString();
        set_Password=Set_Password.getText().toString();
        if(arduinoip!=null&&set_Id!=null&&set_Password!=null){
            Save(arduinoip,set_Id,set_Password);
        }
        else{
            MessageBox_F("오류!!","설정값중하나라도 입력하지않았습니다.\n확인하고 다시 저장해주세요!!");
        }
    }
    private  void Save(String arduinoip,String setid,String setpass ){
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/camdata");
        if(!saveFile.exists()){
            saveFile.mkdir();
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/Setting.txt", true));
            buf.append("arduinoip");
            buf.newLine();
            buf.append(arduinoip);
            buf.newLine();
            buf.append("setid");
            buf.newLine();
            buf.append(setid);
            buf.newLine();
            buf.append("setpass");
            buf.newLine();
            buf.append(setpass);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOnClose();
    }
    public void MessageBox_F(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(Setting.this);
        builder.setTitle((title));
        builder.setMessage(message);
        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
    public void mOnClose(){
        finish();
    }
}
