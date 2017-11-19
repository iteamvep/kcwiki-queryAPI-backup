package org.kcwiki.tools.wikitextparser;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Rikka on 2016/6/28.
 */
public class RetrofitAPI {

    public interface KcwikiService {
        @Headers({
                "Host: zh.kcwiki.org",
                "Referer: https://zh.kcwiki.org/wiki/",
                "User-Agent: Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36"
        })
        @GET("/index.php")
        Call<ResponseBody> getPage(@Query("title") String title, @Query("action") String action);
    }
    
    public interface MoeGirlService {
        @Headers({
                "Host: zh.moegirl.org",
                "Referer: https://zh.moegirl.org",
                "User-Agent: Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36"
        })
        @GET("/index.php")
        Call<ResponseBody> getPage(@Query("title") String title, @Query("action") String action);
    }

    public interface KancollewikiService {
        @GET("/index.php")
        Call<ResponseBody> getPage(@Query("title") String title, @Query("action") String action);
    }
}
