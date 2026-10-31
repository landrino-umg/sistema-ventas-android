package gt.edu.umg.sistema.ventas.movil.network;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {

    private static final String PREF_NAME = "sistema_ventas_config";
    private static final String KEY_BASE_URL = "base_url";

    // Por defecto 10.0.2.2:8080 para emulador Android Studio, o la URL de ngrok que el usuario configure
    public static final String DEFAULT_URL = "http://10.0.2.2:8080/api";

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String url = prefs.getString(KEY_BASE_URL, DEFAULT_URL);
        if (url == null || url.trim().isEmpty()) {
            return DEFAULT_URL;
        }
        url = url.trim();
        // Quitar pleca final si la tiene
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static void setBaseUrl(Context context, String url) {
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_URL;
        }
        url = url.trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BASE_URL, url).apply();
    }
}
