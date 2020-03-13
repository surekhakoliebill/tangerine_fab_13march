package com.aryagami.data;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.aryagami.util.MyToast;
import com.aryagami.util.UserSession;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PdfOpenHelper {

    public static void openPdfFromUrl(final String pdfUrl, final Activity activity) {
        Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                try {
                    URL url = new URL(pdfUrl);
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("SESSIONID", UserSession.getSessionKey());
                    connection.connect();

                    // download the file
                    InputStream input = new BufferedInputStream(connection.getInputStream());
                    File dir = new File(Environment.getExternalStorageDirectory(), "documents");
                    dir.mkdir();
                    ///data/data/com.tangerine.android/files/shared_pdf
                    File file = new File(dir, "temp.pdf");
                    OutputStream output = new FileOutputStream(file);

                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {

                    @Override
                    public void accept(File file) throws Exception {
                        String authority = "com.tangerine.android.provider";
                        Uri uriToFile = FileProvider.getUriForFile(activity, authority, file);

                        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                        shareIntent.setDataAndType(uriToFile, "application/pdf");
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(shareIntent);
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        MyToast.makeMyToast(activity,"File Not Found.", Toast.LENGTH_SHORT);

                    }
                });
    }
}
