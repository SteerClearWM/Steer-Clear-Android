package steer.clear;

import org.json.JSONObject;

import com.android.volley.VolleyError;

/**
 * Helper interface that allows the asynchronous methods in HttpHelper to call through to your activity or fragment.
 * @author Miles Peele
 *
 */
public interface HttpHelperInterface {
	
	public abstract void onPostSuccess(JSONObject object);
	
	public abstract void onGetSuccess(JSONObject object);
	
	public abstract void onVolleyError(VolleyError error);
	
	public abstract void onUserError(String string);
}
