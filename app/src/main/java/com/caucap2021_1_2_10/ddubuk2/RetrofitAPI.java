package com.caucap2021_1_2_10.ddubuk2;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI{
    @GET("checkExist.php") //통신할 페이지 이름을 정확히 입력합니다.

    Call<JsonObject> checkExist(@Query("_id") String _id);
    //Call<JsonObejct> : JsonObejct 타입으로 결과값을 return 받기 위해
    //checkExist : 안드로이드에서 사용될 함수
    //@Query("_id") : checkExist.php파일에서 입력받을 변수 이름 ***중요. 꼭 맞춰줘야함.
    //String _id : 안드로이드에서 넘겨줄 변수
}