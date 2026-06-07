package com.esys.agenticpos.networking;


import android.content.Context;
import android.content.SharedPreferences;

import com.esys.agenticpos.Constant;
import com.esys.agenticpos.utils.MultiLanguageApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String BASE_URL = Constant.BASE_URL;
    private static Retrofit retrofit = null;


    /**
     * Attaches the per-tenant API token (saved at login) to every request as the
     * {@code X-Tenant-Token} header. The multi-tenant backend resolves the caller's
     * tenant from this token; requests without it are rejected with HTTP 401.
     * The token is read fresh from SharedPreferences on each request so it survives
     * process restarts and login changes.
     */
    private static class TenantTokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            String token = "";
            Context ctx = MultiLanguageApp.getAppContext();
            if (ctx != null) {
                SharedPreferences sp = ctx.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                token = sp.getString(Constant.SP_API_TOKEN, "");
            }

            if (token == null || token.isEmpty()) {
                // No token yet (e.g. the login call itself) — send the request unchanged.
                return chain.proceed(original);
            }

            Request withToken = original.newBuilder()
                    .header(Constant.HEADER_TENANT_TOKEN, token)
                    .build();
            return chain.proceed(withToken);
        }
    }


    public static Retrofit getApiClient() {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(new TenantTokenInterceptor())
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;

    }

}
