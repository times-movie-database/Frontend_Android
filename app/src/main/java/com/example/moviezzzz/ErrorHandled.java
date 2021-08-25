package com.example.moviezzzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ERROR HANDLING ACTIVITY ----> For Handling errors
 */
public class ErrorHandled extends AppCompatActivity {
    TextView code;
    Button btn;
    String activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_handled);
        code=findViewById(R.id.error_txt_cause);
        btn=findViewById(R.id.error_btn_retry);
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");
        code.setText(intent.getStringExtra("cd"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("homeact"))
                {
                    Intent obj=new Intent(ErrorHandled.this, HomeActivity.class);
                    startActivity(obj);
                }
                if(activity.equals("addm"))
                {
                    Intent obj=new Intent(ErrorHandled.this,HomeActivity.class);
                    startActivity(obj);
                }
                if(activity.equals("Srcact"))
                {
                    Intent obj=new Intent(ErrorHandled.this, SearchActivity.class);
                    startActivity(obj);
                }
                if(activity.equals("Mvd"))
                {
                    Intent intent = getIntent();
                    Intent obj=new Intent(ErrorHandled.this, Movie_Details.class);
                    obj.putExtra("titlee",intent.getStringExtra("titlee"));
                    obj.putExtra("id",intent.getStringExtra("id"));
                    startActivity(obj);
                }
            }
        });
    }
}