package com.example.salvatucomida.NetWork;

import com.example.salvatucomida.ModelsIA.IARequest;
import com.example.salvatucomida.ModelsIA.IAResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIServicioIA {
    @POST("openai/v1/chat/completions")
    Call<IAResponse> generarReceta(
            @Header("Authorization") String apiKey,
            @Body IARequest request
    );
}