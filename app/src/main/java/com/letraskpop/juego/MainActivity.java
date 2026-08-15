package com.letraskpop.juego;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private WebView webView;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();
        tts = new TextToSpeech(this, this);
        webView = new WebView(this); setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false); s.setSupportZoom(false);
        webView.setWebViewClient(new WebViewClient()); webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new TTSBridge(), "AndroidTTS");
        webView.setOnLongClickListener(v -> true); webView.setHapticFeedbackEnabled(false);
        webView.loadUrl("file:///android_asset/index.html");
    }
    @Override public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("es", "MX"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) tts.setLanguage(new Locale("es", "ES"));
            tts.setSpeechRate(0.90f); tts.setPitch(1.12f); ttsReady = true;
        }
    }
    public class TTSBridge { @JavascriptInterface public void speak(final String text) { runOnUiThread(() -> { if (ttsReady && text != null && !text.trim().isEmpty()) { tts.stop(); tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "felicitacion"); } }); } }
    private void hideSystemUi() { getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); }
    @Override public void onWindowFocusChanged(boolean hasFocus) { super.onWindowFocusChanged(hasFocus); if (hasFocus) hideSystemUi(); }
    @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else moveTaskToBack(true); }
    @Override protected void onDestroy() { if (tts != null) { tts.stop(); tts.shutdown(); } if (webView != null) webView.destroy(); super.onDestroy(); }
}
