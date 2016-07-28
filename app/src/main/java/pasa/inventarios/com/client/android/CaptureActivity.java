/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pasa.inventarios.com.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;

import pasa.inventarios.com.Activity_AddData;
import pasa.inventarios.com.R;
import pasa.inventarios.com.client.android.camera.CameraManager;
import pasa.inventarios.com.client.android.clipboard.ClipboardInterface;
import pasa.inventarios.com.client.android.history.HistoryActivity;
import pasa.inventarios.com.client.android.history.HistoryItem;
import pasa.inventarios.com.client.android.history.HistoryManager;
import pasa.inventarios.com.client.android.result.ResultButtonListener;
import pasa.inventarios.com.client.android.result.ResultHandler;
import pasa.inventarios.com.client.android.result.ResultHandlerFactory;

public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private View resultView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
    private IntentSource source;
    private String sourceUrl;
    private ScanFromWebPageManager scanFromWebPageManager;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    public CaptureActivity() {

        Log.i("CaptureActivity --- ", "CaptureActivity()");

    }

    ViewfinderView getViewfinderView() {

        Log.i("CaptureActivity --- ", "getViewfinderView()");

        return viewfinderView;
    }

    public Handler getHandler() {

        Log.i("CaptureActivity --- ", "getHandler()");

        return handler;
    }

    CameraManager getCameraManager() {

        Log.i("CaptureActivity --- ", "getCameraManager()");

        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {

        Log.i("CaptureActivity --- ", "onCreate()");


        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        //PreferenceManager.setDefaultValues(this, R.xml.preferenc, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.i("CaptureActivity --- ", "onConfigurationChanged()");

        super.onConfigurationChanged(newConfig);
/*
        Toast.makeText(this, "Orientación", Toast.LENGTH_LONG).show();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_LONG).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_LONG).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
*/
    }

    @Override
    protected void onResume() {

        Log.i("CaptureActivity --- ", "onResume()");

        super.onResume();

        // historyManager must be initialized here to update the history preference
        historyManager = new HistoryManager(this);
        historyManager.trimHistory();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);

        handler = null;
        lastResult = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(PreferencesActivity.KEY_DISABLE_AUTO_ORIENTATION, true)) {

            Log.i("CaptureActivity --- ", "()");

            //setRequestedOrientation(getCurrentOrientation());
            //Toast.makeText(getApplicationContext(), "Orientacion actual:" + ActivityInfo.SCREEN_ORIENTATION_SENSOR, Toast.LENGTH_LONG).show();
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //getCurrentOrientation();
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {

            Log.i("CaptureActivity --- ", "()");

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //getCurrentOrientation();
        }

        resetStatusView();

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        Intent intent = getIntent();

        copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
                && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));

        source = IntentSource.NONE;
        sourceUrl = null;
        scanFromWebPageManager = null;
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            Log.i("CaptureActivity --- ", "()");


            String action = intent.getAction();
            String dataString = intent.getDataString();

            if (Intents.Scan.ACTION.equals(action)) {

                Log.i("CaptureActivity --- ", "()");

                // Scan the formats the intent requested, and return the result to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {

                    Log.i("CaptureActivity --- ", "()");

                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {

                        Log.i("CaptureActivity --- ", "()");

                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {

                    Log.i("CaptureActivity --- ", "()");

                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {

                        Log.i("CaptureActivity --- ", "()");

                        cameraManager.setManualCameraId(cameraId);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {

                    Log.i("CaptureActivity --- ", "()");

                    statusView.setText(customPromptMessage);
                }

            } else if (dataString != null &&
                    dataString.contains("http://www.google") &&
                    dataString.contains("/m/products/scan")) {

                Log.i("CaptureActivity --- ", "()");


                // Scan only products and send the result to mobile Product Search.
                source = IntentSource.PRODUCT_SEARCH_LINK;
                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

            } else if (isZXingURL(dataString)) {

                Log.i("CaptureActivity --- ", "()");


                // Scan formats requested in query string (all formats if none specified).
                // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
                source = IntentSource.ZXING_LINK;
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(dataString);
                scanFromWebPageManager = new ScanFromWebPageManager(inputUri);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
                // Allow a sub-set of the hints to be specified by the caller.
                decodeHints = DecodeHintManager.parseDecodeHints(inputUri);

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

        }

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {

            Log.i("CaptureActivity --- ", "()");

            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {

            Log.i("CaptureActivity --- ", "()");

            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    private int getCurrentOrientation() {

        Log.i("CaptureActivity --- ", "getCurrentOrientation()");

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (rotation) {
                case Surface.ROTATION_0:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_0 =====>>>>> SCREEN_ORIENTATION_LANDSCAPE");
                case Surface.ROTATION_90:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_90 =====>>>>> SCREEN_ORIENTATION_LANDSCAPE");
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                default:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_0 =====>>>>> SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        } else {

            Log.i("CaptureActivity --- ", "()");

            switch (rotation) {
                case Surface.ROTATION_0:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_0 =====>>>>> SCREEN_ORIENTATION_PORTRAIT");
                case Surface.ROTATION_270:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_270 =====>>>>> SCREEN_ORIENTATION_PORTRAIT");
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                default:

                    Log.i("CaptureActivity --- ", "()");

                    Log.e("*********", " ROTATION_0 =====>>>>> SCREEN_ORIENTATION_REVERSE_PORTRAIT");
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
    }

    private static boolean isZXingURL(String dataString) {

        Log.i("CaptureActivity --- ", "isZXingURL()");

        if (dataString == null) {

            Log.i("CaptureActivity --- ", "()");

            return false;
        }
        for (String url : ZXING_URLS) {

            Log.i("CaptureActivity --- ", "()");

            if (dataString.startsWith(url)) {

                Log.i("CaptureActivity --- ", "()");

                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {

        Log.i("CaptureActivity --- ", "onPause()");

        if (handler != null) {

            Log.i("CaptureActivity --- ", "()");

            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {

            Log.i("CaptureActivity --- ", "()");

            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        Log.i("CaptureActivity --- ", "onDestroy()");

        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.i("CaptureActivity --- ", "onKeyDown()");

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                Log.i("CaptureActivity --- ", "()");

                if (source == IntentSource.NATIVE_APP_INTENT) {

                    Log.i("CaptureActivity --- ", "()");

                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {

                    Log.i("CaptureActivity --- ", "()");

                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_FOCUS:

                Log.i("CaptureActivity --- ", "()");

            case KeyEvent.KEYCODE_CAMERA:

                Log.i("CaptureActivity --- ", "()");

                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                Log.i("CaptureActivity --- ", "()");

                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:

                Log.i("CaptureActivity --- ", "()");

                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.i("CaptureActivity --- ", "onCreateOptionsMenu()");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.capture, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i("CaptureActivity --- ", "onOptionsItemSelected()");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        switch (item.getItemId()) {

            case R.id.menu_history:

                Log.i("CaptureActivity --- ", "()");

                intent.setClassName(this, HistoryActivity.class.getName());
                startActivityForResult(intent, HISTORY_REQUEST_CODE);
                break;
            case R.id.menu_settings:

                Log.i("CaptureActivity --- ", "()");

                intent.setClassName(this, PreferencesActivity.class.getName());
                startActivity(intent);
                break;
            default:

                Log.i("CaptureActivity --- ", "()");

                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i("CaptureActivity --- ", "onActivityResult()");

        if (resultCode == RESULT_OK && requestCode == HISTORY_REQUEST_CODE && historyManager != null) {

            Log.i("CaptureActivity --- ", "()");

            int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
            if (itemNumber >= 0) {

                Log.i("CaptureActivity --- ", "()");

                HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);
                decodeOrStoreSavedBitmap(null, historyItem.getResult());
            }
        }
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {

            Log.i("CaptureActivity --- ", "decodeOrStoreSavedBitmap()");

            savedResultToShow = result;
        } else {

            Log.i("CaptureActivity --- ", "()");

            if (result != null) {

                Log.i("CaptureActivity --- ", "()");

                savedResultToShow = result;
            }
            if (savedResultToShow != null) {

                Log.i("CaptureActivity --- ", "()");

                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.i("CaptureActivity --- ", "surfaceCreated()");

        if (holder == null) {

            Log.i("CaptureActivity --- ", "()");

            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {

            Log.i("CaptureActivity --- ", "()");

            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.i("CaptureActivity --- ", "surfaceDestroyed()");

        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.i("CaptureActivity --- ", "surfaceChanged()");


    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        Log.i("CaptureActivity --- ", "handleDecode()");

        inactivityTimer.onActivity();
        lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {

            Log.i("CaptureActivity --- ", "() --- if (fromLiveScan)");

            historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, scaleFactor, rawResult);
        }

        switch (source) {
            case NATIVE_APP_INTENT:

                Log.i("CaptureActivity --- ", "() --- switch (source) --- case NATIVE_APP_INTENT");

            case PRODUCT_SEARCH_LINK:

                Log.i("CaptureActivity --- ", "() --- switch (source) --- case PRODUCT_SEARCH_LINK");

                handleDecodeExternally(rawResult, resultHandler, barcode);
                break;
            case ZXING_LINK:

                Log.i("CaptureActivity --- ", "() --- switch (source) --- case ZXING_LINK");

                if (scanFromWebPageManager == null || !scanFromWebPageManager.isScanFromWebPage()) {

                    Log.i("CaptureActivity --- ", "() --- switch (source) --- case ZXING_LINK --- if");

                    handleDecodeInternally(rawResult, resultHandler, barcode);
                } else {

                    Log.i("CaptureActivity --- ", "() --- switch (source) --- case ZXING_LINK --- else");

                    handleDecodeExternally(rawResult, resultHandler, barcode);
                }
                break;
            case NONE:

                Log.i("CaptureActivity --- ", "() --- switch (source) --- case NONE");

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {

                    Log.i("CaptureActivity --- ", "() --- switch (source) --- case NONE --- if");

                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
                            Toast.LENGTH_SHORT).show();
                    // Wait a moment or else it will scan the same barcode continuously about 3 times
                    restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
                } else {

                    Log.i("CaptureActivity --- ", "() --- switch (source) --- case NONE --- else");

                    handleDecodeInternally(rawResult, resultHandler, barcode);
                }
                break;
        }
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     *
     * @param barcode     A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult   The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {

        Log.i("CaptureActivity --- ", "drawResultPoints()");

        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {

            Log.i("CaptureActivity --- ", "()");

            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {

                Log.i("CaptureActivity --- ", "()");

                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {

                Log.i("CaptureActivity --- ", "()");

                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {

                Log.i("CaptureActivity --- ", "()");

                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {

                    Log.i("CaptureActivity --- ", "()");

                    if (point != null) {

                        Log.i("CaptureActivity --- ", "()");

                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {

        Log.i("CaptureActivity --- ", "drawLine()");

        if (a != null && b != null) {

            Log.i("CaptureActivity --- ", "()");

            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }

    // Put up our own UI for how to handle the decoded contents.
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        Log.i("CaptureActivity --- ", "handleDecodeInternally()");

        CharSequence displayContents = resultHandler.getDisplayContents();

        if (copyToClipboard && !resultHandler.areContentsSecure()) {

            Log.i("CaptureActivity --- ", "()");

            ClipboardInterface.setText(displayContents, this);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (resultHandler.getDefaultButtonID() != null && prefs.getBoolean(PreferencesActivity.KEY_AUTO_OPEN_WEB, false)) {

            Log.i("CaptureActivity --- ", "()");

            resultHandler.handleButtonPress(resultHandler.getDefaultButtonID());
            return;
        }

        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);

        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
        if (barcode == null) {

            Log.i("CaptureActivity --- ", "()");

            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.logo_login));
        } else {

            Log.i("CaptureActivity --- ", "()");

            barcodeImageView.setImageBitmap(barcode);
        }

        TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
        formatTextView.setText(rawResult.getBarcodeFormat().toString());

        TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
        typeTextView.setText(resultHandler.getType().toString());

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
        timeTextView.setText(formatter.format(new Date(rawResult.getTimestamp())));


        TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
        contentsTextView.setText(displayContents);
        int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
        contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);


        TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
        supplementTextView.setText("");
        supplementTextView.setOnClickListener(null);

        int buttonCount = resultHandler.getButtonCount();
        ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
        buttonView.requestFocus();
        for (int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {

            Log.i("CaptureActivity --- ", "()");

            TextView button = (TextView) buttonView.getChildAt(x);
            if (x < buttonCount) {

                Log.i("CaptureActivity --- ", "()");

                button.setVisibility(View.VISIBLE);
                button.setText(resultHandler.getButtonText(x));
                button.setOnClickListener(new ResultButtonListener(resultHandler, x));
            } else {

                Log.i("CaptureActivity --- ", "()");

                button.setVisibility(View.GONE);
            }
        }
        Log.d("Msj", " - " + contentsTextView.getText().toString());
    /*Intent mIntent = new Intent(this, Activity_AddData.class);
    Bundle mBundle = new Bundle();
    mBundle.putString("id_codigo", contentsTextView.getText().toString());
    mIntent.putExtras(mBundle);
    finish();*/

        Intent intent = new Intent();
        intent.putExtra("id_codigo", contentsTextView.getText().toString());
        setResult(RESULT_OK, intent);
        finish();


    }

    // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        Log.i("CaptureActivity --- ", "handleDecodeExternally()");

        if (barcode != null) {

            Log.i("CaptureActivity --- ", "()");

            viewfinderView.drawResultBitmap(barcode);
        }

        long resultDurationMS;
        if (getIntent() == null) {

            Log.i("CaptureActivity --- ", "()");

            resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        } else {

            Log.i("CaptureActivity --- ", "()");

            resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
                    DEFAULT_INTENT_RESULT_DURATION_MS);
        }

        if (resultDurationMS > 0) {

            Log.i("CaptureActivity --- ", "()");

            String rawResultString = String.valueOf(rawResult);
            if (rawResultString.length() > 32) {

                Log.i("CaptureActivity --- ", "()");

                rawResultString = rawResultString.substring(0, 32) + " ...";
            }
            statusView.setText(getString(resultHandler.getDisplayTitle()) + " : " + rawResultString);
        }

        if (copyToClipboard && !resultHandler.areContentsSecure()) {

            Log.i("CaptureActivity --- ", "()");

            CharSequence text = resultHandler.getDisplayContents();
            ClipboardInterface.setText(text, this);
        }

        if (source == IntentSource.NATIVE_APP_INTENT) {

            Log.i("CaptureActivity --- ", "()");

            // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
            // the deprecated intent is retired.
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null && rawBytes.length > 0) {

                Log.i("CaptureActivity --- ", "()");

                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {

                Log.i("CaptureActivity --- ", "()");

                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {

                    Log.i("CaptureActivity --- ", "()");

                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                            metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {

                    Log.i("CaptureActivity --- ", "()");

                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {

                    Log.i("CaptureActivity --- ", "()");

                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                @SuppressWarnings("unchecked")
                Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {

                    Log.i("CaptureActivity --- ", "()");

                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {

                        Log.i("CaptureActivity --- ", "()");

                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

        } else if (source == IntentSource.PRODUCT_SEARCH_LINK) {

            Log.i("CaptureActivity --- ", "()");

            // Reformulate the URL which triggered us into a query, so that the request goes to the same
            // TLD as the scan URL.
            int end = sourceUrl.lastIndexOf("/scan");
            String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";
            sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);

        } else if (source == IntentSource.ZXING_LINK) {

            Log.i("CaptureActivity --- ", "()");

            if (scanFromWebPageManager != null && scanFromWebPageManager.isScanFromWebPage()) {

                Log.i("CaptureActivity --- ", "()");

                String replyURL = scanFromWebPageManager.buildReplyURL(rawResult, resultHandler);
                scanFromWebPageManager = null;
                sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);
            }

        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {

        Log.i("CaptureActivity --- ", "sendReplyMessage()");

        if (handler != null) {

            Log.i("CaptureActivity --- ", "()");

            Message message = Message.obtain(handler, id, arg);
            if (delayMS > 0L) {

                Log.i("CaptureActivity --- ", "()");

                handler.sendMessageDelayed(message, delayMS);
            } else {

                Log.i("CaptureActivity --- ", "()");

                handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {

        Log.i("CaptureActivity --- ", "initCamera()");

        if (surfaceHolder == null) {

            Log.i("CaptureActivity --- ", "()");

            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {

            Log.i("CaptureActivity --- ", "()");

            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {

            Log.i("CaptureActivity --- ", "()");

            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {

                Log.i("CaptureActivity --- ", "()");

                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {

            Log.i("CaptureActivity --- ", "()");

            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {

            Log.i("CaptureActivity --- ", "()");

            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {

        Log.i("CaptureActivity --- ", "displayFrameworkBugMessageAndExit()");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {

        Log.i("CaptureActivity --- ", "restartPreviewAfterDelay()");

        if (handler != null) {

            Log.i("CaptureActivity --- ", "()");

            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {

        Log.i("CaptureActivity --- ", "resetStatusView()");

        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {

        Log.i("CaptureActivity --- ", "drawViewfinder()");

        viewfinderView.drawViewfinder();
    }
}
