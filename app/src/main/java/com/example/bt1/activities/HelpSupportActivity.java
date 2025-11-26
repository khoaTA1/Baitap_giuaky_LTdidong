package com.example.bt1.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bt1.R;

public class HelpSupportActivity extends AppCompatActivity {

    private ImageView btnBack;
    private CardView cardFaq, cardContact, cardEmail, cardPhone, cardTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        cardFaq = findViewById(R.id.card_faq);
        cardContact = findViewById(R.id.card_contact);
        cardEmail = findViewById(R.id.card_email);
        cardPhone = findViewById(R.id.card_phone);
        cardTerms = findViewById(R.id.card_terms);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        cardFaq.setOnClickListener(v -> {
            Intent intent = new Intent(this, FaqActivity.class);
            startActivity(intent);
        });

        cardContact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
        });

        cardEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@pharmacystore.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu hỗ trợ từ PharmacyStore");
            
            try {
                startActivity(Intent.createChooser(emailIntent, "Gửi email qua..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
            }
        });

        cardPhone.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:0123456789"));
            startActivity(callIntent);
        });

        cardTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        });
    }
}
