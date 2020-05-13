package com.aryagami.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryagami.BuildConfig;
import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.UserRegistration;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.content.pm.PackageManager;

/**
 * Created by aryagami on 11/9/15.
 */
public class PictureUtility {
    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;
    public static final int FILE_MAX_VALUE = 1;
    static Uri imageUri;
    static Uri croppedUri;
    static boolean isEditImage;
    static ImageView editImageView;
    static Boolean isPassportImage = false;
    static Boolean isProfileImage = false;
    static Boolean isNinImage = false;
    static Boolean isRefugeeImage = false;
    static Boolean isActivationImage = false;
    static Boolean isNewVisaImage = false;
    static Boolean isVisaImage = false;
    public static String pictureFilePath;
    public static boolean imageByCamera = false;


    public static void selectImage(final Activity activity) {
        final MarshMallowPermission marshMallowPermission = new MarshMallowPermission(activity);
        final CharSequence[] items = {activity.getResources().getString(R.string.take_photo), activity.getResources().getString(R.string.from_gallery), activity.getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.picture_dialog);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(activity.getResources().getString(R.string.take_photo))) {


                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    }

                    imageByCamera = true;
                    if (!marshMallowPermission.checkPermissionForCamera()) {
                        marshMallowPermission.requestPermissionForCamera();
                    } else {
                        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                            marshMallowPermission.requestPermissionForExternalStorage();
                        } else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
                            if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                               // activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);

                                String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tangerine/Temp";
                                File folder = new File(folderPath);

                                if (!folder.exists()) {
                                    folder.mkdirs();
                                    MyToast.makeMyToast(activity, "Tangerine Directory Created", Toast.LENGTH_SHORT);
                                }

                                File pictureFile = null;
                                pictureFile = getPictureFile(folder);
                                if (pictureFile != null) {

                                    imageUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", pictureFile);
                                    //   imageUri = Uri.fromFile(lastDocument);
                                    String provider = "com.android.providers.media.MediaProvider";

                                    activity.grantUriPermission(provider, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    activity.grantUriPermission(provider, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);

                                }else{
                                    BugReport.postBugReport(activity, Constants.emailId, " "+pictureFile,"TAKE PHOTO");
                                }
                            }
                        }

                    /*
                    imageByCamera = true;
                    if (!marshMallowPermission.checkPermissionForCamera()) {
                        marshMallowPermission.requestPermissionForCamera();
                    } else {
                        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                            marshMallowPermission.requestPermissionForExternalStorage();
                        } else {

                            // IMAGES WILL SAVE IN TANGERINE/TEMP DIRECTORY

                            String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tangerine/Temp";
                            File folder = new File(folderPath);
                            if (!folder.exists()) {
                                folder.mkdirs();
                                MyToast.makeMyToast(activity,"Tangerine Directory Created", Toast.LENGTH_SHORT);
                            }


                            File pictureFile = null;
                            pictureFile = getPictureFile(folder);
                            //  File lastDocument = new File(folderPath,"lastDocument");

                            if (pictureFile != null) {

                                imageUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", pictureFile);
                                //   imageUri = Uri.fromFile(lastDocument);
                                String provider = "com.android.providers.media.MediaProvider";

                                activity.grantUriPermission(provider, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                activity.grantUriPermission(provider, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                activity.startActivityForResult(intent, REQUEST_CAMERA);

                            }


                        }
                    }*/
                    }

                } else if (items[item].equals(activity.getResources().getString(R.string.from_gallery))) {

                    if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    } else {

                        imageByCamera = false;
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        activity.startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                    }


                } else if (items[item].equals(activity.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private static File getPictureFile(File folder) {
        // String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        double n = Math.random();
        long randomNumber = Math.round(Math.random() * 1000);

        String pictureFile = "Lyca_" + randomNumber;
        File storageDir = folder;
        File image = null;
        try {
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
            pictureFilePath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private static void addThumbNail(final Activity activity, View view, Uri imageUri,final List<View> imageList, String title, ViewGroup root){
        if (root == null) {
            if(isProfileImage){
                root = (ViewGroup) view.findViewById(R.id.profile_picture_thumbnails);
                isProfileImage =false;
            }else if(isPassportImage){
                root = (ViewGroup) view.findViewById(R.id.passport_picture_thumbnails);
                isPassportImage = false;
            }else if(isNinImage){
                root = (ViewGroup) view.findViewById(R.id.nin_picture_thumbnails);
                isNinImage=false;
            }else if(isRefugeeImage){
                root = (ViewGroup) view.findViewById(R.id.refugee_picture_thumbnails);
                isRefugeeImage=false;
            }else if(isActivationImage){
                root = (ViewGroup) view.findViewById(R.id.activation_form_picture_thumbnails);
                isActivationImage=false;
            }else if(isVisaImage){
                root = (ViewGroup) view.findViewById(R.id.visa_picture_thumbnails);
                isVisaImage=false;
            }else if(isNewVisaImage){
                root = (ViewGroup) view.findViewById(R.id.visa_new_picture_thumbnails);
                isNewVisaImage=false;
            }
        }

        View imgView = null;
        if (title == null) {
            imgView = activity.getLayoutInflater().inflate(R.layout.item_user_thumbnail, root, false);
        } else {
            //  imgView = activity.getLayoutInflater().inflate(R.layout.item_doc_thumbnail, root, false);
        }

        final ImageView thumbNailImg = isEditImage ? editImageView :
                (title != null ? (ImageView)imgView.findViewById(R.id.image) : (ImageView)imgView);
        final ViewGroup froot = root;
        thumbNailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage(activity, thumbNailImg, imageList, froot);
            }
        });

        if (!isEditImage) {
            imageList.add(imgView);
        }

        String selectedImagePath = null;

        if (imageUri.getScheme().equals("file")) {
            selectedImagePath = imageUri.getPath();
        } else {

            if(!imageByCamera) {
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = activity.managedQuery(imageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(column_index);
            }else{
                selectedImagePath = pictureFilePath;
            }
        }
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 500;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        Bitmap rotatedBitmap = ExifUtils.rotateBitmap(selectedImagePath, bm);
        // Bitmap waterMarkBitmap = addWaterMark(activity,rotatedBitmap);
        thumbNailImg.setImageBitmap(rotatedBitmap);

        if (!isEditImage) {


            root.addView(imgView);
        }
    }

    static public ImageView loadPicturePreview (final Activity activity, ViewGroup root, final List<View> imageList, String imageUrl) {
        if (root == null) {
            // root = (LinearLayout) activity.findViewById(R.id.profile_image);
        }
        final ImageView thumbNailImg = (ImageView) activity.getLayoutInflater().inflate(R.layout.activity_user_profile, root, false);
        final ViewGroup lroot = root;
        thumbNailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage(activity, thumbNailImg, imageList, lroot);
            }
        });
        imageList.add(thumbNailImg);
        loadImageNoCache(activity, imageUrl, thumbNailImg, true);

        root.addView(thumbNailImg);
        return thumbNailImg;
    }

    static public void processPictureRequest (final Activity activity, View view, int requestCode, Intent data, final List<View> imageList) {
        doProcessPictureRequest(activity, view, requestCode, data, imageList, false, null, null);
    }

    static public void processPictureRequestWithEdit (final Activity activity, View view, int requestCode, Intent data, final List<View> imageList) {
        doProcessPictureRequest(activity, view, requestCode, data, imageList, true, null, null);
    }

    public static void processPassportPictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean isPImage) {
        isPassportImage = isPImage;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processProfilePictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isProfileImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processRefugeePictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isRefugeeImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processVisaPictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isVisaImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processActivationFormPictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isActivationImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processNewVisaPictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isNewVisaImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    public static void processNinPictureRequestWithEdit(Activity activity, View passportPictureContainer, int requestCode, Intent data, List<View> passportImages, Boolean profile) {
        isNinImage = profile ;
        doProcessPictureRequest(activity, passportPictureContainer, requestCode, data, passportImages, false, null, null);
    }

    static public void processPictureRequestWithTitle (final Activity activity, View view, int requestCode, Intent data, final List<View> imageList, String title) {
        doProcessPictureRequest(activity, view, requestCode, data, imageList, true, title, null);
    }

    static public void processPictureRequestWithRoot (final Activity activity, View view, int requestCode, Intent data, final List<View> imageList, ViewGroup root) {
        doProcessPictureRequest(activity, view, requestCode, data, imageList, true, null, root);
    }

    static public void doProcessPictureRequest(final Activity activity, View view, int requestCode, Intent data, final List<View> imageList, boolean enableEdit, String title, ViewGroup root) {
        if (requestCode == REQUEST_CAMERA) {
            if (enableEdit) {
                int width = DeviceImageMetrics.getDeviceWidth(activity);
                croppedUri = Uri.fromFile(new File(activity.getCacheDir(), "cropped"));
                Crop.of(imageUri, croppedUri).asSquare().withMaxSize(width, width).start(activity);
            } else {
                addThumbNail(activity, view, imageUri, imageList,title, root);
            }
        } else if (requestCode == SELECT_FILE) {
            imageUri = data.getData();
            if (enableEdit) {
                int width = DeviceImageMetrics.getDeviceWidth(activity);
                croppedUri = Uri.fromFile(new File(activity.getCacheDir(), "cropped"));
                Crop.of(imageUri, croppedUri).asSquare().withMaxSize(width, width).start(activity);
            } else {
                addThumbNail(activity, view, imageUri, imageList,title, root);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            addThumbNail(activity,view,Crop.getOutput(data), imageList,title, root);
            editImageDone();
        }
    }


    public static void editImageDone() {
        isEditImage = false;
    }

    private static void setImageUri(Activity activity, ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
        String result = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmapDrawable.getBitmap(), "", "");
        imageUri = Uri.parse(result);
    }

    public static void editImage(final Activity activity, final ImageView thumbNailImg, final List imageList, final ViewGroup root) {
        final CharSequence[] items = {activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.move_right), activity.getResources().getString(R.string.move_left), activity.getResources().getString(R.string.delete), activity.getResources().getString(R.string.cancel)};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getResources().getString(R.string.edit_picture));
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(activity.getResources().getString(R.string.edit))) {
                    editImageView = thumbNailImg;
                    int width = DeviceImageMetrics.getDeviceWidth(activity);
                    setImageUri(activity, thumbNailImg);
                    croppedUri = Uri.fromFile(new File(activity.getCacheDir(), "cropped"));
                    Crop.of(imageUri, croppedUri).asSquare().withMaxSize(width, width).start(activity);
                    isEditImage = true;
                } else if (items[item].equals(activity.getResources().getString(R.string.move_right))) {
                    int position = root.indexOfChild(thumbNailImg);
                    if ( position+ 1 == root.getChildCount()) {
                        MyToast.makeMyToast(activity, activity.getResources().getString(R.string.cant_move_right), Toast.LENGTH_LONG);
                    } else {
                        root.removeView(thumbNailImg);
                        imageList.remove(thumbNailImg);
                        root.addView(thumbNailImg, position + 1);
                        imageList.add(position + 1, thumbNailImg);
                    }
                } else if (items[item].equals(activity.getResources().getString(R.string.move_left))) {
                    int position = root.indexOfChild(thumbNailImg);
                    if (position == 0) {
                        MyToast.makeMyToast(activity, activity.getResources().getString(R.string.cant_move_left), Toast.LENGTH_LONG);
                    } else {
                        root.removeView(thumbNailImg);
                        imageList.remove(thumbNailImg);
                        root.addView(thumbNailImg, position - 1);
                        imageList.add(position - 1, thumbNailImg);
                    }
                } else if (items[item].equals(activity.getResources().getString(R.string.delete))) {
                    PictureUtility.deletePicture(activity, root, thumbNailImg);
                    if (imageList != null) {
                        imageList.remove(thumbNailImg);
                    }
                } else if (items[item].equals(activity.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }

            }
        });
        alertDialog.show();
    }

    private static Bitmap addWaterMark(Activity activity, final Bitmap src) {

        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src,0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(activity.getResources(), R.drawable.watermark);
        Paint p = new Paint();
        p.setAlpha(70);

        int width = src.getWidth();
        int height = src.getHeight();
        float centerX = (width  - waterMark.getWidth()) * 0.5f;
        float centerY = (height- waterMark.getHeight()) * 0.5f;

        canvas.drawBitmap(waterMark, centerX, centerY, p);
        return result;
    }

    public static void deletePicture(Activity activity, ImageView thumbNailImg) {
        ViewGroup root = (LinearLayout) activity.findViewById(R.id.product_picture_thumbnails);
        root.removeView(thumbNailImg);
    }

    public static void deletePicture(final Activity activity, final ViewGroup root, ImageView thumbNailImg) {
        root.removeView(thumbNailImg);
    }

    public static String encodeFileToBase64Binary(String fileName) throws IOException {
        String encodedString = "";
        File file = new File(fileName);
        byte[] bytes = loadFile(file);
       /* if(bytes.length/1024 > 1){
            encodedString = "File size is too Large";
        }else{*/
        byte[] encoded = Base64.encodeBase64(bytes);
        encodedString = new String(encoded);


        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    public static String encodePicutreBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if(bitmap != null){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            String file = android.util.Base64.encodeToString(data, 0);

            return file;
        }else{
            return "";
        }

    }

    public static String encodeWSQBitmap(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        String file = android.util.Base64.encodeToString(data, 0);

        return file;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



    static public class PicassoTrustAll {
        private static Picasso mInstance = null;

        private PicassoTrustAll(Context context) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] x509Certificates,
                            String s) throws java.security.cert.CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] x509Certificates,
                            String s) throws java.security.cert.CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }};

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }})
                        .sslSocketFactory(sc.getSocketFactory())
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                final Request original = chain.request();

                                final Request authorized = original.newBuilder()
                                        .addHeader("SESSIONID", UserSession.getSessionKey())
                                        .build();

                                return chain.proceed(authorized);
                            }
                        }).build();

                mInstance = new Picasso.Builder(context)
                        .downloader(new OkHttp3Downloader(client))
                        .listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            }
                        }).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static Picasso getInstance(Context context) {
            if (mInstance == null) {
                new PicassoTrustAll(context);
            }
            return mInstance;
        }
    }


   /* public static void displayFullImage(Activity activity, String[] imageUrls, int position) {
        Intent intent = new Intent(activity, FullImageActivity.class);
        intent.putExtra("ImageUrls", imageUrls);
        intent.putExtra("position", position);
        activity.startActivity(intent);
    }*/

    public static void loadImage(Context context, String imageUrl, ImageView imageView, boolean isThumbnail) {
        if (isThumbnail) {
            String urls[] = imageUrl.split(".jpeg");
            imageUrl = urls[0] + "_thumbnail.jpeg";
        }
        PicassoTrustAll.getInstance(context).load(imageUrl).placeholder(R.drawable.image_default).error(R.drawable.image_error).into(imageView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView, boolean isThumbnail, Drawable.Callback callback) {
        if (isThumbnail) {
            String urls[] = imageUrl.split(".jpeg");
            imageUrl = urls[0] + "_thumbnail.jpeg";
        }
        PicassoTrustAll.getInstance(context).load(imageUrl).placeholder(R.drawable.image_default).error(R.drawable.image_error).into(imageView, (Callback) callback);
    }

    public static void loadImageNoCache(Context context, String imageUrl, ImageView imageView, boolean isThumbnail) {
        if (isThumbnail) {
            String urls[] = imageUrl.split(".jpeg");
            imageUrl = urls[0] + "_thumbnail.jpeg";
        }
        PicassoTrustAll.getInstance(context).load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).
                placeholder(R.drawable.image_default).error(R.drawable.image_error).into(imageView);
    }

    public static void moveImageLeft() {

    }
}
