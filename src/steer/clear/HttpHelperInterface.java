package steer.clear;

import com.android.volley.VolleyError;

/**
 * Helper interface that allows the asynchronous methods in HttpHelper to call through to your activity or fragment.
 * @author Miles Peele
 *
 */
public interface HttpHelperInterface {
	
	public abstract void onPostSuccess(String string);
	
	public abstract void onGetSuccess(String string);
	
	public abstract void onVolleyError(VolleyError error);
	
	public abstract void onUserError(String string);
}
