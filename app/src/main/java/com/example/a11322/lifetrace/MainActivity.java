package com.example.a11322.lifetrace;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Login_item:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.User_Center:
                Intent intent2 = new Intent(MainActivity.this,UserCenterActivity.class);
                startActivity(intent2);
                default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    user.setName(data.getStringExtra("UserName"));
                    user.setEmail(data.getStringExtra("UserEmail"));
                    user.setPhonenumber(data.getStringExtra("UserPhone"));
                }
        }
    }
}
