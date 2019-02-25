package cs.com.webjsnative;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import static android.view.KeyEvent.KEYCODE_BACK;
public class MainActivity extends AppCompatActivity {

    private LinearLayout mLlSceneDetail;

    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private String url = "http://bimphone.ngrok.qimiaowa.com/open/offline#/model-show?model=http%3A%2F%2Fbim-model-dev.oss-cn-hangzhou.aliyuncs.com%2Fbim%2FdestModel%2F8d34fdc8f062000%2F8d34fdc8f062000.obj.svf&modelTitle=3ds&modelId=8d34fdc8f062000&navis=0";
    private String url1 = "https://www.baidu.com/";
    private WebView mWebView;
    private final int RESULT_CODE_STARTCAMERA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickCamera();
        initView();
    }

    private void initView() {
        mLlSceneDetail = (LinearLayout) findViewById(R.id.ll_scene_detail);
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
        // 支持缩放

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.loadUrl("file:///android_asset/index.html");
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

        // For Android < 3.0
        public void openFileChooser(ValueCallback valueCallback) {
            mUploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            mUploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback valueCallback, String acceptType, String capture) {
            mUploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            /*mUploadCallbackAboveL = filePathCallback;
            openImageChooserActivity();
             return true;*/

            mUploadCallbackAboveL = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILE_CHOOSER_RESULT_CODE);
            return true;


        }


    };

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || mUploadCallbackAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
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


}
