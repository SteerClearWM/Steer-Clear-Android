package steer.clear.service;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;
import java.io.IOException;

public interface ServiceHttpInterface {
	
	void onPostSuccess(Response response);

	void onFailure(Request request, IOException exception);

	void onDeleteSuccess(Response response);
}
