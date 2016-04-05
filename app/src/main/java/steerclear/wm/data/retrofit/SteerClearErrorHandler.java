package steerclear.wm.data.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 4/4/16.
 */
public class SteerClearErrorHandler implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        switch (response.code()) {

        }
        return response;
    }
}
