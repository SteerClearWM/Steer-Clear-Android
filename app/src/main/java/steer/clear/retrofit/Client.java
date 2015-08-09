package steer.clear.retrofit;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import steer.clear.pojo.LoginPost;
import steer.clear.pojo.RegisterPost;
import steer.clear.pojo.RideObject;
import steer.clear.pojo.RidePost;

public class Client {

    private final static String URL_BASE = "http://10.0.0.223:5000/api";
    private final static String URL_LOGIN = "http://10.0.0.223:5000/";

    private ApiInterface apiInterface;
    public LoginInterface userInterface;

	public Client(Application application) {
        OkHttpClient okHttpClient = new OkHttpClient();
        CookieManager cookieHandler = new CookieManager();
        cookieHandler.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okHttpClient.setFollowRedirects(true);
        okHttpClient.setCookieHandler(cookieHandler);
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
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

    public Observable<Response> register(String username, String password, String phone) {
        return userInterface.register(new RegisterPost(username, password, phone));
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
