package TestResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.healthcare.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import Common.Common;

public class ReportPDFActivity extends AppCompatActivity {


    TextInputLayout checkedDate, reportType, note;

    WebView webView;
    Button btnDownload;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_p_d_f);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent i  = getIntent();
        String type = i.getStringExtra("type");
        String url = i.getStringExtra("fileUrl");
        String note = i.getStringExtra("note");
        String checkedDate = i.getStringExtra("checkedDate");
        initialize();

        pd = new ProgressDialog(this);
        pd.setTitle("File");
        pd.setMessage("Opening....");
        pd.show();
        loadData(type,note,checkedDate);
        loadPDF(url);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
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
    private void loadPDF(String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
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
                                 }
        );
        String report  = "";
        try {
            report  = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+ report);

    }


    /*private void loadPDF() {
        progressBar.setVisibility(View.VISIBLE);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                pdfView.fromUri(uri)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .onDraw(new OnDrawListener() {
                            @Override
                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                            }
                        }).onDrawAll(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                    }
                }).onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {

                    }
                }).onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        return true;
                    }
                }).onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth();
                    }
                }).enableAnnotationRendering(true)
                        .invalidPageColor(Color.WHITE)
                        .load();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
*/

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