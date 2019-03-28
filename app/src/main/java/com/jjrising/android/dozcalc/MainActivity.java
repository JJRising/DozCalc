package com.jjrising.android.dozcalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mInputText, mResultText;
    private InputString mInputString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInputText = findViewById(R.id.text_input);
        mResultText = findViewById(R.id.text_result);
        mInputString = new InputString();
    }

    public void enterValue(View view) {
        String input = view.getTag().toString();
        if (input.equals("back")) {
            mInputString.back();
            mInputText.setText(mInputString.getText());
        } else {
            mInputString.add(Integer.parseInt(input));
            mInputText.setText(mInputString.getText());
        }
    }

    public void runCalculation(View view) {
        Expression express = mInputString.createExpression();
        Numeral result = express.calc();
        mResultText.setText(result.toString());
        mInputText.setText("");
        mInputString.clear();
    }
}

