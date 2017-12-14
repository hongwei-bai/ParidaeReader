package bhw1899.paridae.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import bhw1899.paridae.R;
import bhw1899.paridae.constants.Constants;
import bhw1899.paridae.model.utils.TextFileReader;

public class TextEditActivity extends Activity {
    private TextView titleTextView = null;
    private EditText contentEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_edit_activity);
        titleTextView = (TextView) findViewById(R.id.text_view);
        contentEditText = (EditText) findViewById(R.id.edit_text);

        Intent intent = getIntent();
        if (null == intent) {
            finish();
        }
        String fileName = intent.getStringExtra(Constants.INTENT_KEY_FILE_NAME);
        if (null == fileName) {
            finish();
        }
        titleTextView.setText(fileName);

        String str = TextFileReader.read(Constants.DEFAULT_TXT_PATH, fileName);
        if (str != null) {
            contentEditText.setText(str);
        }
    }
}
