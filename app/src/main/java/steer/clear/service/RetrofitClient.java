package steer.clear.service;

import retrofit.RestAdapter;
import retrofit.client.Response;
import steer.clear.pojo.RidePost;
import steer.clear.pojo.RideResponse;

public class RetrofitClient {

    private final static String URL_BASE = "http://10.0.3.2:5000/rides";

    private RetrofitInterface apiService;

	public RetrofitClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL_BASE)
                .build();

        apiService = restAdapter.create(RetrofitInterface.class);
	}
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the ServiceHttpInterface onPostSuccess()
	 * If failure, calls through the ServiceHttpInterface onVolleyError()
	 */
	public RideResponse addRide(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
			final Double end_latitude, final Double end_longitude) {
        return apiService.addRide(new RidePost(num_passengers, start_latitude,
                start_longitude, end_latitude, end_longitude));
	}

	public Response cancelRide(int cancelId) {
        return apiService.deleteRide(cancelId);
	}
}
