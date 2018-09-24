package com.example.montest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Person person = (Person) this.getIntent().getParcelableExtra("Person");
        final TextView firstName = (TextView) findViewById(R.id.firstName2);
        final TextView lastName = (TextView) findViewById(R.id.lastName2);
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        Button button = (Button) findViewById(R.id.buttonTest2);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Main2Activity.this.setResult(007, Main2Activity.this.getIntent().putExtra("Key2", lastName.getText().toString()));
                Main2Activity.this.finish();
            }
        });
    }
}
