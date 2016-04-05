package steerclear.wm.data.retrofit;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import steerclear.wm.data.model.RidePost;
import steerclear.wm.data.model.RideObject;

/**
 * Created by milespeele on 7/2/15.
 */
interface ISteerClearApi {

    @POST("rides")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<RideObject> addRide(@Body RidePost ridePost);

    @DELETE("rides/{rideId}")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<ResponseBody> deleteRide(@Path("rideId") int cancelId);

    @GET("rides/{rideId}")
    Observable<ResponseBody> checkRideStatus(@Path("rideId") int cancelId);

    @GET("index")
    Observable<ResponseBody> checkCookie();
}
