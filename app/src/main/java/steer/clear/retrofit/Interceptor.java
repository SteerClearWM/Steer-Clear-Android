package steer.clear.retrofit;

import android.util.Log;

import retrofit.RequestInterceptor;
import steer.clear.util.Datastore;
import steer.clear.util.Logger;

/**
 * Created by Miles Peele on 8/31/2015.
 */
public class Interceptor implements RequestInterceptor {

    private Datastore store;

    public Interceptor(Datastore store) {
        this.store = store;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (!store.getCookie().isEmpty()) {
            request.addHeader("Cookie", store.getCookie());
        }
    }
}
