package com.example.a11322.lifetrace;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.tablemanager.Connector;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //注册按钮
        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText email = (EditText) findViewById(R.id.emailaddress);
                EditText phonenum = (EditText) findViewById(R.id.phonenumber);
                EditText pswd = (EditText) findViewById(R.id.password);
                String usn = username.getText().toString();
                String eml = email.getText().toString();
                String phn = phonenum.getText().toString();
                String psw = pswd.getText().toString();
                if (TextUtils.isEmpty(usn)||TextUtils.isEmpty(eml)||TextUtils.isEmpty(phn)||TextUtils.isEmpty(psw)){
                    Toast.makeText(SignUpActivity.this, "注册信息输入不全，请重新输入。", Toast.LENGTH_SHORT).show();
                }else if (phn.length()!=11){
                    Toast.makeText(SignUpActivity.this, "手机号码格式不正确请重新输入。", Toast.LENGTH_SHORT).show();
                }
                else {
                    String msg = "SINUP:account:" + usn + ":email:" + eml + ":phonenum:" + phn + ":password:" +psw;
                    SocketThread mThread = new SocketThread(mHandler,msg);
                    Thread thread = new Thread(mThread);
                    thread.start();
                }
            }
        });
    }
    //此UI的Handler
    private Handler mHandler = new Handler(Looper.myLooper()){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case 0:{
                String content = (String)msg.obj;
                if(content.equals("Sign up succeed")){
                    Toast.makeText(SignUpActivity.this,content , Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    //startActivity(intent);//此处还有UI更新
                    finish();
                }else
                    Toast.makeText(SignUpActivity.this, content, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
};
}
