package steerclear.wm.data.retrofit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.internal.framed.Header;
import steerclear.wm.data.DataStore;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 4/4/16.
 */
class SteerClearCookieHandler implements CookieJar {

    private DataStore dataStore;

    public SteerClearCookieHandler(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//        for (Header header: response.getHeaders()) {
//            if (header.getName().contains("Set-Cookie")) {
//                store.putCookie(header.getValue());
//            }
//        }
        Logg.log("COOKIES: ", cookies.size());
        for (Cookie cookie: cookies) {
            if (cookie.name().contains("Set-Cookie")) {
                Logg.log("IS A SET COOKIE");
                dataStore.putCookie(cookie.value());
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
//        return Collections.singletonList(dataStore.getCookie());
        return null;
    }
}
