package com.neotrino;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;

import java.io.IOException;

public class VrVideoActivity extends Activity {
  private static final String TAG = VrVideoActivity.class.getSimpleName();

  public static final int LOAD_VIDEO_STATUS_UNKNOWN = 0;
  public static final int LOAD_VIDEO_STATUS_SUCCESS = 1;
  public static final int LOAD_VIDEO_STATUS_ERROR = 2;

  private int loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;

  /** Tracks the file to be loaded across the lifetime of this app. **/
  private Uri fileUri;
  private Uri fallbackVideoUri;

  /** Configuration information for the video. **/
  private Options videoOptions = new Options();

  private VideoLoaderTask backgroundVideoLoaderTask;
  private Boolean fallbackVideoLoaded = false;
  /**
   * The video view and its custom UI elements.
   */
  protected VrVideoView videoWidgetView;
  Activity activity = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    String package_name = getApplication().getPackageName();
    Resources resources = getApplication().getResources();
    
    setContentView(resources.getIdentifier("main_layout", "layout", package_name));

    loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;

    // Bind input and output objects for the view.
    videoWidgetView = (VrVideoView) findViewById(resources.getIdentifier("video_view", "id", package_name));
    videoWidgetView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);
    videoWidgetView.setEventListener(new ActivityEventListener());
    videoWidgetView.setVisibility(View.INVISIBLE);

    activity = this;
    // Initial launch of the app or an Activity recreation due to rotation.
    handleIntent(getIntent());
  }

  /**
   * Called when the Activity is already running and it's given a new intent.
   */
  @Override
  protected void onNewIntent(Intent intent) {
    Log.i(TAG, this.hashCode() + ".onNewIntent()");
    // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during
    // future invocations.
    setIntent(intent);
    // Load the new image.
    handleIntent(intent);
  }

  public int getLoadVideoStatus() {
    return loadVideoStatus;
  }


  private void launchVideoLoader(Uri fileUri) {
    // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
    // take 100s of milliseconds.
    if (backgroundVideoLoaderTask != null) {
      // Cancel any task from a previous intent sent to this activity.
      backgroundVideoLoaderTask.cancel(true);
    }
    backgroundVideoLoaderTask = new VideoLoaderTask();
    backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
  }
  /**
   * Load custom videos based on the Intent or load the default video. See the Javadoc for this
   * class for information on generating a custom intent via adb.
   */
  private void handleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      fileUri = Uri.parse(extras.getString("videoUrl"));
      String fallbackVideo = extras.getString("fallbackVideoUrl");
      if (fallbackVideo != null)
        fallbackVideoUri = Uri.parse(fallbackVideo);
    } else {
      fileUri = null;
    }

    launchVideoLoader(fileUri);

  }

  @Override
  protected void onPause() {
    super.onPause();
    // Prevent the view from rendering continuously when in the background.
    videoWidgetView.pauseRendering();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Resume the 3D rendering.
    videoWidgetView.resumeRendering();
  }

  @Override
  protected void onDestroy() {
    // Destroy the widget and free memory.
    videoWidgetView.shutdown();
    super.onDestroy();
  }


  /**
   * Listen to the important events from widget.
   */
  private class ActivityEventListener extends VrVideoEventListener  {
    /**
     * Called by video widget on the UI thread when it's done loading the video.
     */
    @Override
    public void onLoadSuccess() {
      Log.i(TAG, "Sucessfully loaded video " + videoWidgetView.getDuration());
      loadVideoStatus = LOAD_VIDEO_STATUS_SUCCESS;
    }

    @Override
    public void onDisplayModeChanged(int newDisplayMode) {
      if (newDisplayMode != VrWidgetView.DisplayMode.FULLSCREEN_STEREO && newDisplayMode !=  VrWidgetView.DisplayMode.FULLSCREEN_MONO){
        activity.finish();
      }
    }
    /**
     * Called by video widget on the UI thread on any asynchronous error.
     */
    @Override
    public void onLoadError(String errorMessage) {
      // An error here is normally due to being unable to decode the video format.
      loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
      //Attempt to load fallback video
      if (!fallbackVideoLoaded){
        fallbackVideoLoaded = true;
        launchVideoLoader(fallbackVideoUri);
      }else {
        Toast.makeText(
                VrVideoActivity.this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                .show();
      }
      Log.e(TAG, "Error loading video: " + errorMessage);
    }


    /**
     * Update the UI every frame.
     */
    @Override
    public void onNewFrame() {
    }

    /**
     * Make the video play in a loop. This method could also be used to move to the next video in
     * a playlist.
     */
    @Override
    public void onCompletion() {
      videoWidgetView.seekTo(0);
    }
  }

  /**
   * Helper class to manage threading.
   */
  class VideoLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {
    Uri fileUri = null;


    @Override
    protected Boolean doInBackground(Pair<Uri, Options>... pairs) {
      fileUri = pairs[0].first;
      return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
      try {
        Options options = new Options();
        //options.inputType = Options.TYPE_MONO;
        videoWidgetView.loadVideo(fileUri, options);
      } catch (IOException e) {
        // An error here is normally due to being unable to locate the file.
        loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
        // Since this is a background thread, we need to switch to the main thread to show a toast.
        videoWidgetView.post(new Runnable() {
          @Override
          public void run() {
            Toast
                    .makeText(VrVideoActivity.this, "Error opening file. ", Toast.LENGTH_LONG)
                    .show();
          }
        });
        Log.e(TAG, "Could not open video: " + e);
      }
      super.onPostExecute(aBoolean);
    }
  }
}
