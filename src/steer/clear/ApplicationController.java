package steer.clear;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApplicationController extends Application {
	 
    public static final String TAG = ApplicationController.class.getSimpleName();
 
    private RequestQueue mRequestQueue; 
 
    private static ApplicationController mInstance; // Singleton construct
 
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    
    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    /**
     * Gets the Singleton request queue.
     * @return RequeuestQueue queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
 
    /**
     * Adds a request to the queue with a Tag.
     * If you supply a tag, adds it with that tag.
     * If otherwise, adds it with the Tag specified as AppController.class.getSimpleName()
     * @param request
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    /**
     * Adds a request to the queue with a Tag specified as AppController.class.getSimpleName()
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    /**
     * Cancels all requests with a given Tag
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}