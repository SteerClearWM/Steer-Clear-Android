package steerclear.wm.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 4/4/16.
 */
public class OkHttpObservable<T> {

    private String url;
    private Class<T> clazz;
    private Gson gson;

    private OkHttpObservable(Builder builder) {
        this.url = builder.url;
        this.clazz = builder.clazz;
        this.gson = builder.gson;
    }

    public Observable<T> execute(OkHttpClient client) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onStart();
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();

                    Reader reader = response.body().charStream();
                    subscriber.onNext(gson.fromJson(reader, clazz));
                    subscriber.onCompleted();

                    reader.close();
                } catch (IOException e) {
                    Logg.log(e);
                    subscriber.onError(e);
                } catch (JsonSyntaxException e1) {
                    subscriber.onError(e1);
                    Logg.log(e1);
                }
            }});
    }

    public Observable<Response> getResponse(OkHttpClient client) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                subscriber.onStart();
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();

                    Reader reader = response.body().charStream();
                    subscriber.onNext(response);
                    subscriber.onCompleted();

                    reader.close();
                } catch (IOException e) {
                    Logg.log(e);
                    subscriber.onError(e);
                } catch (JsonSyntaxException e1) {
                    subscriber.onError(e1);
                    Logg.log(e1);
                }
            }
        });
    }

    public static class Builder<T> {

        public String url;
        public Class clazz;
        public Gson gson;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public OkHttpObservable<T> build() {
            return new OkHttpObservable<>(this);
        }
    }
}