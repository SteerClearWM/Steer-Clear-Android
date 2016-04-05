package steerclear.wm.data.retrofit;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import steerclear.wm.data.model.LoginPost;
import steerclear.wm.data.model.RegisterPost;

/**
 * Created by Miles Peele on 7/25/2015.
 */
interface ISteerClearAuth {

    @POST("login")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<ResponseBody> login(@Body LoginPost loginPost);

    @POST("register")
    @Headers({"contentType: application/x-www-form-urlencoded"})
    Observable<ResponseBody> register(@Body RegisterPost registerPost);

    @GET("logout")
    Observable<ResponseBody> logout();
}
