package com.mygdx.spacedistshooter;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyApi {
    @GET("/space.php")
    Call<List<DataFromBase>> send(@Query("name") String name, @Query("score") int score);
}
