package steer.clear;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface HttpHelperInterface {
	
	public abstract void onPostSuccess(JSONObject object);
	
	public abstract void onGetSuccess(JSONObject object);
	
	public abstract void onVolleyError(VolleyError error);
}
