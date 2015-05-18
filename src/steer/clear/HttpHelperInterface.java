package steer.clear;

import com.android.volley.VolleyError;

public interface HttpHelperInterface {
	
	public abstract void onPostSuccess(String string);
	
	public abstract void onGetSuccess(String string);
	
	public abstract void onVolleyError(VolleyError error);
	
	public abstract void onUserError(String string);
}
