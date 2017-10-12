package se.mah.af6589.assignment2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class CommunicationService extends Service {

    public static String ip = "195.178.227.53";
    private int port = 7117;
    private Socket socket;

    private RunOnThread thread;
    private Buffer<String> receiveBuffer;
    private Receive receive;

    private DataInputStream input;
    private DataOutputStream output;

    private InetAddress address;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new RunOnThread();
        receiveBuffer = new Buffer<>();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalService();
    }

    public void connect() {
        thread.start();
        thread.execute(new Connect());
        Log.v("SERVICE", "Connecting...");
    }

    public void disconnect() {
        thread.start();
        thread.execute(new Disconnect());
    }

    public void send(String object) {
        thread.execute(new Send(object));
    }

    public String receive() throws InterruptedException {
        return receiveBuffer.get();
    }

    public class LocalService extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }

    private class Receive extends Thread {

        @Override
        public void run() {
            String result;
            try {
                while (receive != null) {
                    result = input.readUTF();
                    receiveBuffer.put(result);
                }
            } catch (Exception e) {
                receive = null;
            }
        }
    }

    private class Connect implements Runnable {

        @Override
        public void run() {
            try {
                address = InetAddress.getByName(ip);
                socket = new Socket(address, port);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.flush();
                Log.v("CONNECT", "CONNECTED");
                receive = new Receive();
                receive.start();
            } catch (Exception e) {
                Log.e("CONNECT", "EXCEPTION");
                Log.e("CONNECT", e.getMessage());
            }
        }
    }

    private class Disconnect implements Runnable {

        @Override
        public void run() {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();
                thread.stop();
                Log.v("DISCONNECT", "DISCONNECTED");
            } catch (Exception e) {
                Log.e("DISCONNECT", "EXCEPTION");
            }
        }
    }

    private class Send implements Runnable {

        private String json;

        public Send(String json) {
            this.json = json;
        }

        @Override
        public void run() {
            try {
                output.writeUTF(json);
                output.flush();
            } catch (Exception e) {
                Log.e("SEND", "EXCEPTION");
                Log.e("SEND", e.getMessage());
            }
        }
    }

}
