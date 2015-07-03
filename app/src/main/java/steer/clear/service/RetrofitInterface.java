package steer.clear.service;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import steer.clear.pojo.RidePost;
import steer.clear.pojo.RideResponse;

/**
 * Created by milespeele on 7/2/15.
 */
public interface RetrofitInterface {

    @POST("/rides")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    RideResponse addRide(RidePost ridePost);

    @DELETE("/rides/{rideId}")
    Response deleteRide(@Path("rideId") int cancelId);
}
