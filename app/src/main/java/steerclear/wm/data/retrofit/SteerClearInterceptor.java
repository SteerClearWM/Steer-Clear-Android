package steerclear.wm.data.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import steerclear.wm.data.DataStore;
import steerclear.wm.util.Logg;

/**
 * Created by Miles Peele on 8/31/2015.
 */
class SteerClearInterceptor implements Interceptor {

    private DataStore store;

    public SteerClearInterceptor(DataStore store) {
        this.store = store;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (store.getCookie().isEmpty()) {
            return chain.proceed(chain.request());
        } else {
            Request newRequest = chain.request().newBuilder()
                    .header("Cookie", store.getCookie())
                    .build();
            return chain.proceed(newRequest);
        }
    }
}
