package com.example.a11322.lifetrace;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketThread implements Runnable {
    private String address = "172.20.10.9";
    private int port = 10086;

    private String msg;
    private Handler mHandler;

    public SocketThread(Handler handler,String msg){
        this.mHandler = handler;
        this.msg = msg;
    }

    @Override
    public void run() {
        sendSocket();
    }

    private void sendSocket(){
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        Socket socket = null;
        try{
            //1.创建监听制定服务器地址以及指定服务器监听的端口号
            socket = new Socket(address,port);
            //2.拿到客户端的socket对象的输出流发送给服务器数据
            OutputStream os = socket.getOutputStream();
            //写入要发送给服务器的数据
            os.write(msg.getBytes());
            os.flush();
            socket.shutdownOutput();
            //拿到socket的输入流，这里存储的是服务器返回的数据
            InputStream is = socket.getInputStream();
            //解析服务器返回的数据
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            String s = null;
            final StringBuffer sb = new StringBuffer();
            while((s = bufferedReader.readLine())!=null){
                sb.append(s);
            }
            //将返回的数据发送给主线程
            sendMsg(0,sb.toString());
        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (bufferedReader != null)
                    bufferedReader.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }try{
                if (socket!=null)
                    socket.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
    private void sendMsg(int what,Object object){
        //Message对象最为信息的载体传递给 Handler
        Message msg = new Message();
        msg.what = what;
        msg.obj = object;
        mHandler.sendMessage(msg);
    }
}
