package com.freedom.augmentedreality.detect;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;

import com.freedom.augmentedreality.R;
import com.freedom.augmentedreality.helper.SQLiteHandler;

import java.util.HashMap;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener{
    private static Context context;
    private static final String TAG = "CameraActivity";

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("nftSimpleNative");
    }

    public static native boolean nativeCreate(Context ctx);
    public static native boolean nativeStart();
    public static native boolean nativeStop();
    public static native boolean nativeDestroy();
    // Camera functions.
    public static native boolean nativeVideoInit(int w, int h, int cameraIndex, boolean cameraIsFrontFacing);
    public static native void nativeVideoFrame(byte[] image);
    // OpenGL functions.
    public static native void nativeSurfaceCreated();
    public static native void nativeSurfaceChanged(int w, int h);
    public static native void nativeDrawFrame();
    // Other functions.
    public static native void nativeDisplayParametersChanged(int orientation, int w, int h, int dpi); // 0 = portrait, 1 = landscape (device rotated 90 degrees ccw), 2 = portrait upside down, 3 = landscape reverse (device rotated 90 degrees cw).
    public static native void nativeSetInternetState(int state);

    private GLSurfaceView glView;
    private CameraSurface camSurface;

    private FrameLayout mainLayout;
    static SQLiteHandler db;
    static HashMap<String, String> markers;
    static private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean needActionBar = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (!ViewConfiguration.get(this).hasPermanentMenuKey()) needActionBar = true;
            } else {
                needActionBar = true;
            }
        }
        if (needActionBar) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Force landscape-only.
        updateNativeDisplayParameters();

        setContentView(R.layout.activity_camera);

        CameraActivity.nativeCreate(this);
        CameraActivity.context = getApplicationContext();
        tts = new TextToSpeech(CameraActivity.context, this);
    }

    private void updateNativeDisplayParameters()
    {
        Display d = getWindowManager().getDefaultDisplay();
        int orientation = d.getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        int dpi = dm.densityDpi;
        nativeDisplayParametersChanged(orientation, w, h, dpi);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mainLayout = (FrameLayout)this.findViewById(R.id.mainLayoutDetect);

        CameraActivity.nativeStart();
    }

    @SuppressWarnings("deprecation") // FILL_PARENT still required for API level 7 (Android 2.1)
    @Override
    public void onResume() {
        super.onResume();

        // Update info on whether we have an Internet connection.
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        nativeSetInternetState(isConnected ? 1 : 0);

        // In order to ensure that the GL surface covers the camera preview each time onStart
        // is called, remove and add both back into the FrameLayout.
        // Removing GLSurfaceView also appears to cause the GL surface to be disposed of.
        // To work around this, we also recreate GLSurfaceView. This is not a lot of extra
        // work, since Android has already destroyed the OpenGL context too, requiring us to
        // recreate that and reload textures etc.

        // Create the camera view.
        camSurface = new CameraSurface(this);

        // Create/recreate the GL view.
        glView = new GLSurfaceView(this);
        //glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Do we actually need a transparent surface? I think not, (default is RGB888 with depth=16) and anyway, Android 2.2 barfs on this.
        glView.setRenderer(new Renderer());
//        glView.setZOrderMediaOverlay(true); // Request that GL view's SurfaceView be on top of other SurfaceViews (including CameraPreview's SurfaceView).

        mainLayout.addView(camSurface, new LayoutParams(128, 128));
        mainLayout.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        if (glView != null) glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glView != null) glView.onPause();

        mainLayout.removeView(glView);
        mainLayout.removeView(camSurface);
    }

    @Override
    public void onStop() {
        super.onStop();

        CameraActivity.nativeStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        CameraActivity.nativeDestroy();
    }

    static int i = 0;
    static int pageNo = 0;

    public void messageMe(int message) {
        if(this != null ) {
            db = new SQLiteHandler(CameraActivity.context);
            markers = db.getAllContentMarkers();

            if(message < 100) {
                if(pageNo != message) {
                    speakOut(String.valueOf(markers.get(String.valueOf(message))));
                    pageNo = message;
                    i = 0;
                } else {
                    i++;
                    if(i%40 == 0) {
                        if(i > 2000) i = 0;
                        speakOut(String.valueOf(markers.get(String.valueOf(message))));
                    }
                }

            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.8f);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String text) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
