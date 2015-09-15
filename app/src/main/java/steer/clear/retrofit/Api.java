package steer.clear.retrofit;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import steer.clear.pojo.RidePost;
import steer.clear.pojo.RideObject;

/**
 * Created by milespeele on 7/2/15.
 */
public interface Api {

    @POST("/rides")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<RideObject> addRide(@Body RidePost ridePost);

    @DELETE("/rides/{rideId}")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<Response> deleteRide(@Path("rideId") int cancelId);

    @GET("/rides/{rideId}")
    Observable<Response> checkRideStatus(@Path("rideId") int cancelId);
}
