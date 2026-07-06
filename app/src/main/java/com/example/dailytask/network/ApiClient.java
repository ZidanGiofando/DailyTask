package com.example.dailytask.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    /*
     * PENTING - Sesuaikan BASE_URL dengan lokasi backend PHP Anda:
     *  - Emulator Android Studio mengakses localhost komputer lewat 10.0.2.2
     *    contoh: "http://10.0.2.2/dailytask_api/"
     *  - HP fisik (satu jaringan WiFi dengan komputer/XAMPP) pakai IP lokal komputer
     *    contoh: "http://192.168.1.5/dailytask_api/"
     *  - Jika sudah hosting online, ganti dengan domain, contoh:
     *    "https://namadomainanda.com/dailytask_api/"
     *
     * Folder backend_php/api pada proyek ini harus diletakkan di htdocs/dailytask_api
     * (XAMPP) atau di public_html (hosting) sehingga URL di atas mengarah ke folder
     * yang berisi auth/ dan tasks/.
     */
    private static final String BASE_URL = "http://192.168.100.13/dailytask_api/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
