package com.fase.core.manager;

import com.fase.BuildConfig;
import com.fase.core.api.FaseApi;
import com.fase.core.serializers.DateTimeDeserializer;
import com.fase.core.serializers.DateTimeSerializer;
import com.fase.core.serializers.TupleDeserializer;
import com.fase.core.serializers.TupleSerializer;
import com.fase.model.element.Tuple;
import com.fase.model.service.Command;
import com.fase.model.service.Device;
import com.fase.model.service.ElementCallback;
import com.fase.model.service.Response;
import com.fase.model.service.ScreenUpdate;
import com.fase.model.service.Status;
import com.fase.util.DateTimeUtil;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class ApiManager {

    private static volatile ApiManager mSingleton;
    private FaseApi mApi;

    static synchronized ApiManager getInstance() {
        if (mSingleton == null) {
            synchronized (ApiManager.class) {
                if (mSingleton == null) {
                    mSingleton = new ApiManager();
                }
            }
        }
        return mSingleton;
    }

    private ApiManager() {
        mApi = createApi();
    }

    private Retrofit getRetrofit(GsonConverterFactory converter, OkHttpClient client, RxJava2CallAdapterFactory factory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_HOST)
                .client(client)
                .addCallAdapterFactory(factory)
                .addConverterFactory(converter)
                .build();
    }

    private Retrofit createRetrofit() {
        return getRetrofit(getConverter(), getHttpClient(), getCallAdapterFactory());
    }

    private RxJava2CallAdapterFactory getCallAdapterFactory() {
        return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    private GsonConverterFactory getConverter() {
        GsonBuilder builder = new GsonBuilder();

        // deserialize
        builder.registerTypeAdapter(Date.class, new DateTimeDeserializer(DateTimeUtil.SERVER_DATE_TIME));
        builder.registerTypeAdapter(Tuple.class, new TupleDeserializer());

        // serialize
        builder.registerTypeAdapter(Date.class, new DateTimeSerializer(DateTimeUtil.SERVER_DATE_TIME));
        builder.registerTypeAdapter(Tuple.class, new TupleSerializer());

        return GsonConverterFactory.create(builder.create());
    }

    private OkHttpClient getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(interceptor);
        }

        return builder.build();
    }

    private FaseApi createApi() {
        return createRetrofit().create(FaseApi.class);
    }

    /**
     * Api Methods
     */
    Single<Response> getService(Device device) {
        return mApi.getService(device);
    }

    Single<Command> sendInternalCommand() {
        return mApi.sendInternalCommand();
    }

    Single<Status> sendServiceCommand(Command command) {
        return mApi.sendServiceCommand(command);
    }

    Single<Response> getScreen(String sessionId, String version, Device device) {
        return mApi.getScreen(sessionId, version, device);
    }

    Single<Response> screenUpdate(String sessionId, String screenId, String version, ScreenUpdate screenUpdate) {
        return mApi.screenUpdate(sessionId, screenId, version, screenUpdate);
    }

    Single<Response> elementCallback(String sessionId, String screenId, String version, ElementCallback elementCallback) {
        return mApi.elementCallback(sessionId, screenId, version, elementCallback);
    }

    Single<ResponseBody> getResource(String fileName) {
        return mApi.getResource(fileName);
    }
}
