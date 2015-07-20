package steer.clear.service;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import steer.clear.pojo.RidePost;
import steer.clear.pojo.RideResponse;

/**
 * Created by milespeele on 7/2/15.
 */
public interface RetrofitInterface {

    @POST("/rides")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<RideResponse> addRide(@Body RidePost ridePost);

    @DELETE("/rides/{rideId}")
    Observable<Response> deleteRide(@Path("rideId") int cancelId);
}
