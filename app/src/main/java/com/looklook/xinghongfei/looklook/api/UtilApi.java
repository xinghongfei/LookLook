package com.looklook.xinghongfei.looklook.api;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public interface UtilApi {

    @FormUrlEncoded
    @POST("http://www.weibovideo.com")
    Observable<ResponseBody> getVideoUrl(@Field("weibourl") String weibourl);
}
