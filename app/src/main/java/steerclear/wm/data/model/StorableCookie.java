package steerclear.wm.data.model;

import java.net.CookieHandler;

import okhttp3.Cookie;

/**
 * Created by mbpeele on 4/4/16.
 */
public class StorableCookie {

    private final String name;
    private final String value;
    private final long expiresAt;
    private final String domain;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    private final boolean persistent; // True if 'expires' or 'max-age' is present.
    private final boolean hostOnly; // True unless 'domain' is present.

    public StorableCookie(Cookie cookie) {
        name = cookie.name();
        value = cookie.value();
        expiresAt = cookie.expiresAt();
        domain = cookie.domain();
        path = cookie.path();
        secure = cookie.secure();
        httpOnly = cookie.httpOnly();

        persistent = cookie.persistent();
        hostOnly = cookie.hostOnly();
    }

    public static Cookie toCookie(StorableCookie storableCookie) {
        return new Cookie.Builder()
                .name(storableCookie.name)
                .value(storableCookie.value)
                .domain(storableCookie.domain)
                .expiresAt(storableCookie.expiresAt)
                .hostOnlyDomain(storableCookie.domain)
                .path(storableCookie.path)
                .build();
    }
}
