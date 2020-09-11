package com.example.smartpowerclient;

        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.wifi.WifiConfiguration;
        import android.net.wifi.WifiManager;
        import android.os.Environment;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.os.Bundle;
        import android.widget.CompoundButton;
        import android.widget.Toast;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int sub = 1001;
    private String wifissd;
    private String wifiPassword;
    private String PO_Ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button m_BUser = (Button) findViewById(R.id.userbtn);
        Button m_BAdmin = (Button) findViewById(R.id.adminbtn);

        m_BAdmin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Admin_Activity.class);
                startActivityForResult(intent, sub);
            }
        });
        m_BUser.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), User.class);
                startActivityForResult(intent, sub);
            }

        });
    }

    public void MessageBox_F(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
