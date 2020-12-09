package TestResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;


import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


import com.example.healthcare.R;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;


public class ReportIMGActivity extends AppCompatActivity {

    TextInputLayout checkedDate, reportType, note;
    WebView webView;
    Button btnDownload;
    ProgressDialog pd;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_i_m_g);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String type = i.getStringExtra("type");
        String url = i.getStringExtra("fileUrl");
        String note = i.getStringExtra("note");
        String checkedDate = i.getStringExtra("checkedDate");
        initialize();

        pd = new ProgressDialog(this);
        pd.setTitle("File");
        pd.setMessage("Opening....");
        pd.show();
        loadData(type, note, checkedDate);
        loadIMG(url);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void loadIMG(String urli) {

        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view.getContentHeight() == 0)
                    view.reload();

                pd.dismiss();
            }
        });
        webView.loadUrl(urli);
    }


    private void loadData(String type, String reportNote, String date) {
        checkedDate.getEditText().setText(date);
        reportType.getEditText().setText(type);
        note.getEditText().setText(reportNote);
    }

    private void initialize() {
        webView = findViewById(R.id.webView);
        checkedDate = findViewById(R.id.txtDate);
        reportType = findViewById(R.id.txtType);
        note = findViewById(R.id.txtNote);
        btnDownload = findViewById(R.id.btn_download);

    }

}