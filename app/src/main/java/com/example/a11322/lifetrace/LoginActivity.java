package com.example.a11322.lifetrace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgetpassword();
        Button signup = (Button) findViewById(R.id.sign_up_button);
        Button Login = (Button) findViewById(R.id.log_in_button);
        //注册按钮
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        //登录按钮
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText account = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                String acc = account.getText().toString();
                String paswd = password.getText().toString();
                if (TextUtils.isEmpty(acc)||TextUtils.isEmpty(paswd)){
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空，请重新输入。", Toast.LENGTH_SHORT).show();
                }else{
                    String msg = "LOGIN:account:" + acc + ":password:" + paswd;
                    SocketThread mThread = new SocketThread(mHandler,msg);
                    Thread thread = new Thread(mThread);
                    thread.start();
                }
            }
        });
    }
    //设置修改密码点击事件
  public void forgetpassword(){
       TextView fgps = findViewById(R.id.forget_password);
       SpannableStringBuilder style = new SpannableStringBuilder();
       style.append("forgent password?");
      ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
      ClickableSpan clickableSpan = new ClickableSpan() {
          @Override
          public void onClick(View widget) {
               Toast.makeText(LoginActivity.this, "修改密码", Toast.LENGTH_SHORT).show();
            }
      };
      style.setSpan(clickableSpan,0,17,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      style.setSpan(foregroundColorSpan,0,17,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      fgps.setMovementMethod(LinkMovementMethod.getInstance());
      fgps.setText(style);
 }
//Handler处理消息
 private Handler mHandler = new Handler(Looper.myLooper()){
     @Override
     public void handleMessage(Message msg) {
         super.handleMessage(msg);
         switch(msg.what){
             case 0:{
                 String content = (String)msg.obj;
                 String[] sArray = content.split(":");
                 if(sArray[0].equals("Login succeed")){
                     Toast.makeText(LoginActivity.this,sArray[0] , Toast.LENGTH_SHORT).show();
                    // user.setName(sArray[1]);
                    // user.setEmail(sArray[2]);
                     //user.setPhonenumber(sArray[3]);
                     Intent intent = new Intent();
                     intent.putExtra("UserName",sArray[1]);
                     intent.putExtra("UserEmail",sArray[2]);
                     intent.putExtra("UserPhone",sArray[3]);
                     setResult(RESULT_OK,intent);
                     finish();
                        //此处还有UI更新
                 }else
                     Toast.makeText(LoginActivity.this, content, Toast.LENGTH_SHORT).show();
                 }
                 break;
             }
         }

 };

}
