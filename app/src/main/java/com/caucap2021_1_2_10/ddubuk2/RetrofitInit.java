package com.caucap2021_1_2_10.ddubuk2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInit{
    private static Retrofit retrofit;
    private static RetrofitAPI api;
    public static RetrofitAPI getRetrofit(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if(retrofit == null || api == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("SERVER_URL/") //나중에 편하게 사용하시려면 꼭 마지막에 '/'까지 입력!
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            api = retrofit.create(RetrofitAPI.class); // 아직 생성 안해서 빨간줄!
            return api;
        } return api;
    }
}