package com.jjrising.android.dozcalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mInputText, mResultText;
    private Expression mExpression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInputText = findViewById(R.id.text_input);
        mResultText = findViewById(R.id.text_result);
        mExpression = new Expression();
    }

    public void enterValue(View view) {
        String input = view.getTag().toString();
        if (input.equals("back")) {
            mExpression.back();
            mInputText.setText(mExpression.getText());
        } else {
            if (input.matches("^d/.*"))
                mExpression.add(Digit.fromTag(input));
            else if (input.matches("^o/.*"))
                mExpression.add(Operator.fromTag(input));
            else if (input.matches("^f/.*"))
                mExpression.add(Function.fromTag(input));
            else if (input.matches("^special/.*"))
                mExpression.add(Numeral.fromTag(input));
            else if (input.matches("^p/.*"))
                if (input.matches("^p/\\("))
                    mExpression.add(new OpenParen());
                else if (input.matches("^p/\\)"))
                    mExpression.add(new CloseParen());
            mInputText.setText(mExpression.getText());
        }
    }

    public void runCalculation(View view) {
        String resultText;
        try {
            resultText = mExpression.calc().toString();
        } catch (CalculationError e) {
            resultText = e.error;
        }
        mResultText.setText(resultText);
        mInputText.setText("");
        mExpression.clear();
    }
}

