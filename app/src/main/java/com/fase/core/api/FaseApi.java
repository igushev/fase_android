package com.fase.core.api;

import com.fase.model.service.Command;
import com.fase.model.service.Device;
import com.fase.model.service.ElementCallback;
import com.fase.model.service.Response;
import com.fase.model.service.ScreenUpdate;
import com.fase.model.service.Status;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface FaseApi {

    @POST("/getservice")
    Single<Response> getService(@Body Device device);

    @POST("/sendinternalcommand")
    Single<Command> sendInternalCommand();

    @POST("/sendservicecommand")
    Single<Status> sendServiceCommand(@Body Command command);

    @POST("/getscreen")
    Single<Response> getScreen(@Header("session_id") String sessionId, @Body Device device);

    @POST("/screenupdate")
    Single<Response> screenUpdate(@Header("session_id") String sessionId, @Header("screen_id") String screenId, @Body ScreenUpdate screenUpdate);

    @POST("/elementcallback")
    Single<Response> screenUpdate(@Header("session_id") String sessionId, @Header("screen_id") String screenId, @Body ElementCallback elementCallback);

    @Streaming
    @GET("/getresource/{filename}/")
    Single<ResponseBody> getResource(@Path("filename") String fileName);
}
