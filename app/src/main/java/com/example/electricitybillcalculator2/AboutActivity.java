package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

public class AboutActivity extends AppCompatActivity {

    private Button btnBackToMain;
    private TextView txtWebsite, txtStudentName, txtStudentId, txtCourseCode, txtCourseName;
    private TextView txtCopyright, txtAppDescription, txtFeatures, txtAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        setTextsFromResources();
        setupButtonListeners();
        setupWebsiteLink();
    }

    private void initializeViews() {
        btnBackToMain = findViewById(R.id.btnBackToMain);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtStudentName = findViewById(R.id.txtStudentName);
        txtStudentId = findViewById(R.id.txtStudentId);
        txtCourseCode = findViewById(R.id.txtCourseCode);
        txtCourseName = findViewById(R.id.txtCourseName);
        txtCopyright = findViewById(R.id.txtCopyright);
        txtAppDescription = findViewById(R.id.txtAppDescription);
        txtFeatures = findViewById(R.id.txtFeatures);
        txtAppVersion = findViewById(R.id.txtAppVersion);
    }

    private void setTextsFromResources() {
        btnBackToMain.setText(getString(R.string.btn_back_to_main));

        // Student Information
        txtStudentName.setText(getString(R.string.student_name));
        txtStudentId.setText(getString(R.string.student_id));
        txtCourseCode.setText(getString(R.string.course_code));
        txtCourseName.setText(getString(R.string.course_name));

        // App Information
        txtAppVersion.setText(getString(R.string.app_version));
        txtAppDescription.setText(getString(R.string.app_description));
        txtFeatures.setText(getString(R.string.features));
        txtCopyright.setText(getString(R.string.copyright));
    }

    private void setupButtonListeners() {
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void setupWebsiteLink() {
        String url = getString(R.string.website_url);

        txtWebsite.setOnClickListener(v -> openWebsite(url));

        // Add underline to indicate it's clickable
        String websiteText = "<u>" + url + "</u>";
        txtWebsite.setText(HtmlCompat.fromHtml(websiteText, HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void openWebsite(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        } catch (Exception e) {
            Toast.makeText(this, "Cannot open website: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
