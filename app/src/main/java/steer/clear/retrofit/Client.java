package steer.clear.retrofit;

import android.app.Application;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import steer.clear.pojo.LoginPost;
import steer.clear.pojo.RidePost;
import steer.clear.pojo.RideObject;

public class Client {

    private final static String URL_BASE = "http://10.0.3.2:5000/api";
    private final static String URL_LOGIN = "http://10.0.3.2:5000/";

    private ApiInterface apiInterface;
    public LoginInterface userInterface;

	public Client(Application application) {
        OkHttpClient okHttpClient = new OkHttpClient();
        CookieManager cookieHandler = new CookieManager();
        cookieHandler.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okHttpClient.setFollowRedirects(true);
        okHttpClient.setCookieHandler(cookieHandler);
        OkClient okClient = new OkClient(okHttpClient);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL_BASE)
                .setClient(okClient)
                .build();
        apiInterface = restAdapter.create(ApiInterface.class);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(URL_LOGIN)
                .setClient(okClient)
                .build();
        userInterface = adapter.create(LoginInterface.class);
	}

    public Observable<Response> login(String username, String password) {
        return userInterface.login(new LoginPost(username, password));
    }

    public Observable<Response> register(String username, String password) {
        return userInterface.register(new LoginPost(username, password));
    }
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the ServiceHttpInterface onPostSuccess()
	 * If failure, calls through the ServiceHttpInterface onVolleyError()
	 */
	public Observable<RideObject> addRide(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
			final Double end_latitude, final Double end_longitude) {
        return apiInterface.addRide(new RidePost(num_passengers, start_latitude,
                start_longitude, end_latitude, end_longitude));
	}

	public Observable<Response> cancelRide(int cancelId) {
        return apiInterface.deleteRide(cancelId);
	}
}
