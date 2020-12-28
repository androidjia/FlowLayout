package com.jjs.zero.httplibrary.demo;

import com.jjs.zero.httplibrary.common.service.BaseResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <接口类>
 */
public interface RequestApi {
    /**
     * Retrofit的Url组合规则
     * BaseUrl	                                            和URL有关的注解中提供的值	 最后结果
     * http://localhost:8080/RetrofitService/NewsServlet	/post	                 http://localhost:8080/post
     * http://localhost:8080/RetrofitService/NewsServlet	post	                 http://localhost:8080/RetrofitService/NewsServlet/post
     * http://localhost:8080/RetrofitService/NewsServlet	http://www.jianshu.com/	 http://www.jianshu.com/
     */
    @GET("ghc-consumer-api/api/shop/manage/getProducts")
    Observable<BaseResult<List<Goods>>> getProductsAll();
}
