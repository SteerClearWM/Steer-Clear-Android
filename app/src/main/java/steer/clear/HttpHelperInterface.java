package steer.clear;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface HttpHelperInterface {
	
	void onPostSuccess(JSONObject object);
	
	void onGetSuccess(JSONObject object);
	
    void onVolleyError(VolleyError error);

	void onDeleteSuccess(String string);
}
