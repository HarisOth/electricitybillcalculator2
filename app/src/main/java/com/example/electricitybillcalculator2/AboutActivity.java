package com.example.electricitybillcalculator2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AboutActivity extends AppCompatActivity {

    private Button btnBackToMain;
    private TextView txtWebsite;
    private ImageView imgStudentPhoto;
    private CardView cardStudentInfo, cardAppDescription, cardFeatures, cardInstructions, cardWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        loadStudentPhoto();
        setupButtonListeners();
        setupWebsiteLink();
    }

    private void initializeViews() {
        btnBackToMain = findViewById(R.id.btnBackToMain);
        txtWebsite = findViewById(R.id.txtWebsite);
        imgStudentPhoto = findViewById(R.id.imgStudentPhoto);

        // Card views
        cardStudentInfo = findViewById(R.id.cardStudentInfo);
        cardAppDescription = findViewById(R.id.cardAppDescription);
        cardFeatures = findViewById(R.id.cardFeatures);
        cardInstructions = findViewById(R.id.cardInstructions);
        cardWebsite = findViewById(R.id.cardWebsite);
    }

    private void loadStudentPhoto() {
        try {
            // Try to load student photo from drawable
            imgStudentPhoto.setImageResource(R.drawable.profile);
        } catch (Resources.NotFoundException e) {
            // If profile image not found, use app logo as placeholder
            try {
                imgStudentPhoto.setImageResource(R.drawable.logo);
                Toast.makeText(this, "Using default profile image", Toast.LENGTH_SHORT).show();
            } catch (Resources.NotFoundException e2) {
                // If logo also not found, use a simple colored background
                imgStudentPhoto.setBackgroundColor(0xFFE3F2FD);
                imgStudentPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }

    private void setupButtonListeners() {
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void setupWebsiteLink() {
        String url = getString(R.string.website_url);

        txtWebsite.setOnClickListener(v -> openWebsite(url));

        // Make it look clickable
        txtWebsite.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
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