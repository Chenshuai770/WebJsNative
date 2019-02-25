package cs.com.webjsnative;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.KeyEvent.KEYCODE_BACK;
import static cs.com.webjsnative.SmartWebView.ASWV_F_TYPE;

public class TestActivity extends AppCompatActivity {


    private String url = "file:///android_asset/index.html";
    private WebView mWebView;
    private final int RESULT_CODE_STARTCAMERA = 101;

    private final static int asw_file_req = 1;
    private ValueCallback<Uri> asw_file_message;
    private ValueCallback<Uri[]> asw_file_path;
    private String asw_cam_message;
    private final static int file_perm = 2;

    static boolean ASWP_JSCRIPT = SmartWebView.ASWP_JSCRIPT;
    static boolean ASWP_FUPLOAD = SmartWebView.ASWP_FUPLOAD;
    static boolean ASWP_CAMUPLOAD = SmartWebView.ASWP_CAMUPLOAD;
    static boolean ASWP_ONLYCAM = SmartWebView.ASWP_ONLYCAM;
    static boolean ASWP_MULFILE = SmartWebView.ASWP_MULFILE;
    static boolean ASWP_LOCATION = SmartWebView.ASWP_LOCATION;
    static boolean ASWP_RATINGS = SmartWebView.ASWP_RATINGS;
    static boolean ASWP_PBAR = SmartWebView.ASWP_PBAR;
    static boolean ASWP_ZOOM = SmartWebView.ASWP_ZOOM;
    static boolean ASWP_SFORM = SmartWebView.ASWP_SFORM;
    static boolean ASWP_OFFLINE = SmartWebView.ASWP_OFFLINE;
    static boolean ASWP_EXTURL = SmartWebView.ASWP_EXTURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickCamera();
        initView();
    }

    private void initView() {
        //Prevent the app from being started again when it is still alive in the background
        if (!isTaskRoot()) {
            finish();
            return;
        }

        mWebView = (WebView) findViewById(R.id.mWebView);

        WebSettings webSettings = mWebView.getSettings();
//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
// 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
// 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可


//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setBuiltInZoomControls(true); // 缩放至屏幕的大小


//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDomStorageEnabled(true);

        webSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.setAllowFileAccessFromFileURLs(true);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //开启JavaScript支持
        webSettings.setJavaScriptEnabled(true);


        if (!ASWP_OFFLINE) {
            webSettings.setJavaScriptEnabled(ASWP_JSCRIPT);
        }
        webSettings.setSaveFormData(ASWP_SFORM);
        webSettings.setSupportZoom(ASWP_ZOOM);
        webSettings.setGeolocationEnabled(ASWP_LOCATION);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        // 支持缩放

        mWebView.setHapticFeedbackEnabled(false);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        mWebView.setVerticalScrollBarEnabled(false);

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.loadUrl(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            //  Log.i("Info","BaseWebActivity onPageStarted");
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            if (ASWP_FUPLOAD) {
                asw_file_message = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType(ASWV_F_TYPE);
                if (ASWP_MULFILE) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, getString(R.string.fl_chooser)), asw_file_req);
            }
        }

        //Handling input[type="file"] requests for android API 21+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (check_permission(2) && check_permission(3)) {
                if (ASWP_FUPLOAD) {
                    if (asw_file_path != null) {
                        asw_file_path.onReceiveValue(null);
                    }
                    asw_file_path = filePathCallback;
                    Intent takePictureIntent = null;
                    if (ASWP_CAMUPLOAD) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(TestActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = create_image();
                                takePictureIntent.putExtra("PhotoPath", asw_cam_message);
                            } catch (IOException ex) {
                                Log.e("TAG", "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                asw_cam_message = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    if (!ASWP_ONLYCAM) {
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType(ASWV_F_TYPE);
                        if (ASWP_MULFILE) {
                            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        }
                    }
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.fl_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, asw_file_req);
                }
                return true;
            } else {
                get_file();
                return false;
            }
        }
    };


    //Checking if particular permission is given or not
    public boolean check_permission(int permission) {
        switch (permission) {
            case 1:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            case 2:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            case 3:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        }
        return false;
    }

    //Creating image file for upload
    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name = "file_" + file_name + "_";
        File sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".jpg", sd_directory);
    }

    //Checking permission for storage and camera for writing and uploading images
    public void get_file() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        //Checking for storage permission to write images for upload
        if (ASWP_FUPLOAD && ASWP_CAMUPLOAD && !check_permission(2) && !check_permission(3)) {
            ActivityCompat.requestPermissions(TestActivity.this, perms, file_perm);

            //Checking for WRITE_EXTERNAL_STORAGE permission
        } else if (ASWP_FUPLOAD && !check_permission(2)) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, file_perm);

            //Checking for CAMERA permissions
        } else if (ASWP_CAMUPLOAD && !check_permission(3)) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.CAMERA}, file_perm);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == asw_file_req) {
                    if (null == asw_file_path) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        if (asw_cam_message != null) {
                            results = new Uri[]{Uri.parse(asw_cam_message)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            if (ASWP_MULFILE) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    results = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        results[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            asw_file_path.onReceiveValue(results);
            asw_file_path = null;
        } else {
            if (requestCode == asw_file_req) {
                if (null == asw_file_message) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                asw_file_message.onReceiveValue(result);
                asw_file_message = null;
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }


    public void clickCamera() {
        Log.d("roamer", "clickCamera");

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)) {
            //mWebView.loadUrl("file:///android_asset/index.html");
        } else {
            //提示用户开户权限   拍照和读写sd卡权限
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            ActivityCompat.requestPermissions(this, perms, RESULT_CODE_STARTCAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RESULT_CODE_STARTCAMERA: {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //mWebView.loadUrl("file:///android_asset/index.html");
                } else {
                    //用户授权拒绝之后，友情提示一下就可以了
                }
            }
            break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }


}
