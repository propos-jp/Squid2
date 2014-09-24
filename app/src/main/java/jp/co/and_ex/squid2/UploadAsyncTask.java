package jp.co.and_ex.squid2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.File;
import java.io.IOException;

public class UploadAsyncTask
        extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = UploadAsyncTask.class.getSimpleName();
    private String fileName;
    Context context;

    public UploadAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        fileName = params[0];
        String uri = params[1];


        CloseableHttpClient httpclient = HttpClients.createSystem();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = new File(fileName);
        FileBody fb = new FileBody(file);
        multipartEntityBuilder.addPart("csv_file", fb);

        HttpPost httppost = new HttpPost(uri);
        httppost.setEntity(multipartEntityBuilder.build());

        try {
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                StatusLine line = response.getStatusLine();
                Log.d(TAG,line.getReasonPhrase());

            } finally {
                response.close();
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (fileName != null){
            final File file = new File(fileName);
            if (file.exists()){
                file.delete();
            }
        }

    }

    @Override
    protected void onPreExecute() {

    }
}