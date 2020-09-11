package com.example.smartpowerclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Login_Activity extends AppCompatActivity {
    String m_sAdminId;
    String m_sAdminPassworld;
    String getM_sAdminId="abc";
    String getM_sAdminPassworld="1234";
    EditText m_ID,m_Passworld;
    public static final int sub = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
         m_ID=(EditText)findViewById(R.id.text_input_id_toggle);
         m_Passworld=(EditText)findViewById(R.id.text_input_password_toggle) ;
         if(GetData("setid")!=null){
             getM_sAdminId=GetData("setid");
             getM_sAdminPassworld=GetData("setpass");
         }
         else{
            MessageBox_F("로그인오류","로그인 설정정보가 없습니다. \n 아이디,비밀번호는 초기값인 admin 입니다.\n 로그인후 관리자 설정에서 변경바랍니다.");
         }
    }
    public void OnBtnClick(View view)
    {
        if(m_ID!=null&&m_Passworld!=null) {
            m_sAdminId=m_ID.getText().toString();
            m_sAdminPassworld=m_Passworld.getText().toString();
            if (m_sAdminId.equals(getM_sAdminId) && m_sAdminPassworld.equals(getM_sAdminPassworld)) {
                mOnClose();
            } else if (!m_sAdminId.equals(getM_sAdminId) && m_sAdminPassworld.equals(getM_sAdminPassworld)) {
                MessageBox_F("아이디가 틀렸습니다.");
            } else if (m_sAdminId.equals(getM_sAdminId) && !m_sAdminPassworld.equals(getM_sAdminPassworld)) {
                MessageBox_F("비밀번호가 틀렸습니다.");
            } else if (!m_sAdminId.equals(getM_sAdminId) && !m_sAdminPassworld.equals(getM_sAdminPassworld)) {
                MessageBox_F("가입정보가없습니다. 확인해주세요.");
            }
        }
    }
    private String GetData(String id_name){
        String[] str=new String[5];
        String result_str="";
        int idx=0;
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/camdata"); // 저장 경로
// 폴더 생성
        if(!saveFile.exists()){ // 폴더 없을 경우
            MessageBox_F("오류","설정값이 없습니다.");// 폴더 생성
            return null;
        }
        try {
            BufferedReader buf = new BufferedReader(new FileReader(saveFile+"/Setting.txt"));
            while((buf.readLine())!=null){
               str[idx]=  buf.readLine();
                idx++;
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<str.length;i++){
            if(str[idx]==id_name){
                result_str= str[idx+1];
                break;
            }
        }
        return result_str;
    }
    public void MessageBox_F(String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(Login_Activity.this);
        builder.setTitle(("로그인오류"));
        builder.setMessage(message);
        builder.setNeutralButton("로그인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
    public void MessageBox_F(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(Login_Activity.this);
        builder.setTitle((title));
        builder.setMessage(message);
        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
    public void mOnClose(){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
