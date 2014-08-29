package com.murrayc.galaxyzoo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.murrayc.galaxyzoo.app.provider.Config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link com.murrayc.galaxyzoo.app.ExampleViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ExampleViewerFragment extends Fragment {
    public static final int MARGIN_SMALL_DP = 4;
    public static final String ARG_EXAMPLE_URL = "example-url";
    private Singleton mSingleton;

    //See http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
    private static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String strUri = null;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            strUri = params[0];

            try {
                final URL url = new URL(strUri);
                final URLConnection connection = url.openConnection();
                final InputStream stream = connection.getInputStream();
                return BitmapFactory.decodeStream(stream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        // This avoids calling the ImageView methods in the non-main thread.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /** We need to load the bitmap for the imageview in an async task.
     * This is tedious. It would be far easier if ImageView had a setFromUrl(url) method that did
     * the work asynchronously itself.
     * 
     * @param strUri
     * @param imageView
     */
    private void loadBitmap(final String strUri, ImageView imageView) {
        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(strUri);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionId The question ID
     * @return A new instance of fragment HelpDialogFragment.
     */
    public static ExampleViewerFragment newInstance(final String questionId) {
        ExampleViewerFragment fragment = new ExampleViewerFragment();
        Bundle args = new Bundle();
        args.putString(QuestionFragment.ARG_QUESTION_ID, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public ExampleViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        String uriStr = null;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            uriStr = bundle.getString(ARG_EXAMPLE_URL);
        }

        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        View mRootView = inflater.inflate(R.layout.fragment_example_viewer, null);

        final ImageView imageView = (ImageView) mRootView.findViewById(R.id.imageView);
        if (imageView != null) {
            loadBitmap(uriStr, imageView);
        }

        return mRootView;
    }


}