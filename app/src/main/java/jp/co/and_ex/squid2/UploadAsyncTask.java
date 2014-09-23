package jp.co.and_ex.squid2;

import android.content.Context;
import android.os.AsyncTask;

public class UploadAsyncTask
        extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = UploadAsyncTask.class.getSimpleName();

    Context context;

    public UploadAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String fileName = params[0];
        String uri = params[1];

//
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//
//        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//
//        multipartEntityBuilder.setCharset(Consts.ASCII);
//        multipartEntityBuilder.addBinaryBody("csv_file", new File(fileName), ContentType.DEFAULT_BINARY, "csv_file");
//        HttpPost httppost = new HttpPost(uri);
//
//        httppost.setEntity(multipartEntityBuilder.build());
//
//        try {
//            CloseableHttpResponse response = httpclient.execute(httppost);
//        } catch (IOException e) {
//            Log.d(TAG, e.getMessage());
//        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {


    }

    @Override
    protected void onPreExecute() {

    }
}