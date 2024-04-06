package com.ph40510.asm_ph40510_and103.Services;

import com.ph40510.asm_ph40510_and103.Model.CarModel;
import com.ph40510.asm_ph40510_and103.Model.Page;
import com.ph40510.asm_ph40510_and103.Model.Response;
import com.ph40510.asm_ph40510_and103.Model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    public static String BASE_URL = "http://192.168.1.9:3000/api/";

    @Multipart
    @POST("register-send-email")
    Call<Response<User>> register(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part avartar

    );

    @POST("login")
    Call<Response<User>> login (@Body User user);

    @GET("get-list-car")
    Call<Response<ArrayList<CarModel>>> getListCar(@Header("Authorization")String token);
    @GET("get-page-car")
    Call<Response<Page<ArrayList<CarModel>>>> getPageCar(@QueryMap Map<String, String> stringMap);

    @Multipart
    @POST("add-fruit-with-file-image")
    Call<Response<CarModel>> addCarWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                @Part ArrayList<MultipartBody.Part> ds_hinh
    );

    @Multipart
    @PUT("update-fruit-by-id/{id}")
    Call<Response<CarModel>> updateCarWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                   @Path("id") String id,
                                                   @Part ArrayList<MultipartBody.Part> ds_hinh
    );

    @DELETE("destroy-fruit-by-id/{id}")
    Call<Response<CarModel>> deleteCar(@Path("id") String id);

    @GET("get-fruit-by-id/{id}")
    Call<Response<CarModel>> getCarById (@Path("id") String id);

}

