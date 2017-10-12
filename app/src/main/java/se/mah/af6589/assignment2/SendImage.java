package se.mah.af6589.assignment2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by Gustaf Bohlin on 04/10/2017.
 */

public class SendImage extends AsyncTask<BitmapPackage, Integer, Boolean> {

    private InetAddress address;
    private int port;

    public SendImage(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    protected Boolean doInBackground(BitmapPackage... bitmaps) {
        try {
            Socket socket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            output.writeUTF(bitmaps[0].getImageId());
            output.flush();
            output.writeObject(bitmaps[0].getBitmap());
            output.flush();
            output.close();
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean)
            Log.v("SENDIMAGE", "UPLOAD SUCCESSFUL");
        else
            Log.v("SENDIMAGE", "UPLOAD FAILED");
    }
}
