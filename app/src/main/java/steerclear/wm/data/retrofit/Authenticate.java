package steerclear.wm.data.retrofit;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;
import steerclear.wm.data.model.LoginPost;
import steerclear.wm.data.model.RegisterPost;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public interface Authenticate {

    @POST("/login")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<Response> login(@Body LoginPost login);

    @POST("/register")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<Response> register(@Body RegisterPost login);

    @GET("/logout")
    Observable<Response> logout();
}
