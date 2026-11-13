package com.example.salvatucomida.NetWork;

import com.example.salvatucomida.Models.TiendaAPI;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface APIServicioCliente {
    @GET("Tiendas")
    Call<List<TiendaAPI>> GetTiendas();
}