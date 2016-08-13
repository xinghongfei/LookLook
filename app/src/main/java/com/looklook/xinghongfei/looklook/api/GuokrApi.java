package com.looklook.xinghongfei.looklook.api;


import com.looklook.xinghongfei.looklook.bean.guokr.GuokrArticle;
import com.looklook.xinghongfei.looklook.bean.guokr.GuokrHot;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/7 0007.
 */
public interface GuokrApi {

    @GET("http://apis.guokr.com/minisite/article.json?retrieve_type=by_minisite")
    Observable<GuokrHot> getGuokrHot(@Query("offset") int offset);

    @GET("http://apis.guokr.com/minisite/article/{id}.json")
    Observable<GuokrArticle> getGuokrArticle(@Path("id") String id);
}
