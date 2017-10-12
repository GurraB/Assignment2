package se.mah.af6589.assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by Gustaf Bohlin on 04/10/2017.
 */

public class ImageFromServerView extends ImageView {

    public ImageFromServerView(Context context) {
        super(context);
    }

    public ImageFromServerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFromServerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(String imageId, int port) {
        byte[] bitmapArr = ((MainActivity) getContext()).getController().getDataFragment().getDownloadedBitmaps().get(imageId);
        if (bitmapArr != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
            setImageBitmap(bitmap);
            return;
        }
        GetImage imageGetter = new GetImage(port);
        imageGetter.execute(imageId);
    }

    private class GetImage extends AsyncTask<String, String, byte[]> {

        private InetAddress address;
        private int port;
        private String imageId;

        public GetImage(int port) {
            try {
                this.address = InetAddress.getByName(CommunicationService.ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.port = port;
        }

        @Override
        protected byte[] doInBackground(String... imageId) {
            try {
                byte downloadArray[];
                this.imageId = imageId[0];
                Socket socket = new Socket(address, port);
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                output.writeUTF(imageId[0]);
                output.flush();
                downloadArray = (byte[]) input.readObject();
                socket.close();
                return downloadArray;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(byte[] downloadArray) {
            super.onPostExecute(downloadArray);
            Bitmap bitmap = BitmapFactory.decodeByteArray(downloadArray, 0, downloadArray.length);
            setImageBitmap(bitmap);
            HashMap<String, byte[]> hashMap = ((MainActivity) getContext()).getController().getDataFragment().getDownloadedBitmaps();
            Log.v("ADD BITMAP", String.valueOf(hashMap.size()));
            hashMap.put(imageId, downloadArray);
            Log.v("ADDED BITMAP", String.valueOf(hashMap.size()));
        }
    }
}
