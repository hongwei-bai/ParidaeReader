package bhw1899.paridae.view;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ToggleButton;
import bhw1899.paridae.R;
import bhw1899.paridae.constants.Constants;
import bhw1899.paridae.model.FileListAdapter;
import bhw1899.paridae.model.FileListAdapter.OnCheckStatusChangeListener;
import bhw1899.paridae.model.FileScanner;
import bhw1899.paridae.model.tts.SpeakWord;
import bhw1899.paridae.model.tts.SpeakWord.SpeakCompleteListener;
import bhw1899.paridae.model.utils.TextFileReader;

public class ReaderActivity extends Activity {
    private ListView listView;
    private FileListAdapter adapter;
    private CheckBox selectAllCheckBox;
    private ToggleButton readToggleButton;
    private ToggleButton scanToggleButton;
    private ToggleButton loopToggleButton;
    private Boolean banSelectNone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);
        SpeakWord.getInstance().init(this, true);
        selectAllCheckBox = (CheckBox) findViewById(R.id.check_box);
        readToggleButton = (ToggleButton) findViewById(R.id.toggle_button_read);
        scanToggleButton = (ToggleButton) findViewById(R.id.toggle_button_scan);
        loopToggleButton = (ToggleButton) findViewById(R.id.toggle_button_loop);
        SpeakWord.getInstance().init(this, true);

        adapter = new FileListAdapter(this);
        adapter.setOnCheckStatusChangeListener(new OnCheckStatusChangeListener() {

            @Override
            public void onCheckStatusChanged(boolean selectAll) {
                synchronized (banSelectNone) {
                    if (!selectAll) {
                        banSelectNone = true;
                    }
                }
                selectAllCheckBox.setChecked(selectAll);
            }
        });
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        selectAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.setSelectAll();
                } else {
                    synchronized (banSelectNone) {
                        if (!banSelectNone) {
                            adapter.setSelectNone();
                        }
                    }
                }
                banSelectNone = false;
                adapter.notifyDataSetChanged();
            }
        });
        scanToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (scanToggleButton.isChecked()) {
                    scan();
                    scanToggleButton.setChecked(false);
                }
            }
        });
        readToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (readToggleButton.isChecked()) {
                    playFirstFileOfList();
                } else {
                    SpeakWord.getInstance().stop();
                }
            }
        });
        scan();
    }

    private void playFirstFileOfList() {
        ArrayList<String> fileNameList = adapter.getSelectedList();
        if (null == fileNameList) {
            return;
        }
        if (fileNameList != null && fileNameList.size() > 0) {
            String str = TextFileReader.read(Constants.DEFAULT_TXT_PATH, fileNameList.get(0));
            if (str != null) {
                String linesArray[] = str.split("\n");
                ArrayList<String> lines = new ArrayList<String>();
                for (String s : linesArray) {
                    lines.add(s);
                }
                SpeakWord.getInstance().speakEnglish(fileNameList.get(0), lines, 1, speakListener);
                sendMessageToUpdateListView(fileNameList.get(0));
            }
        }
    }

    final private static int MSG_ID_UPDATE_LISTVIEW = 0x98;
    final private static int MSG_ID_RESET_READ_BUTTON = 0x99;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (MSG_ID_UPDATE_LISTVIEW == msg.what) {
                String str = (String) msg.obj;
                adapter.setReading(str);
            } else if (MSG_ID_RESET_READ_BUTTON == msg.what) {
                readToggleButton.setChecked(false);
            }
        };
    };

    private void sendMessageToUpdateListView(String fileName) {
        Message msg = new Message();
        msg.what = MSG_ID_UPDATE_LISTVIEW;
        msg.obj = fileName;
        handler.sendMessage(msg);
    }

    private SpeakCompleteListener speakListener = new SpeakCompleteListener() {

        @Override
        public void onComplete(String tag) {
            sendMessageToUpdateListView(null);
            if (!readToggleButton.isChecked()) {
                // already stopped
                return;
            }
            while (true) {
                String nextFileName = getNextFileName(tag);
                if (null == nextFileName) {
                    // completed playing the list
                    if (loopToggleButton.isChecked()) {
                        playFirstFileOfList();
                        return;
                    } else {
                        handler.sendEmptyMessage(MSG_ID_RESET_READ_BUTTON);
                        return;
                    }
                }
                String str = TextFileReader.read(Constants.DEFAULT_TXT_PATH, nextFileName);
                if (str != null) {
                    String linesArray[] = str.split("\n");
                    ArrayList<String> lines = new ArrayList<String>();
                    for (String s : linesArray) {
                        lines.add(s);
                    }
                    SpeakWord.getInstance().speakEnglish(nextFileName, lines, 1, speakListener);
                    sendMessageToUpdateListView(nextFileName);
                    return;
                }
            }
        }
    };

    private String getNextFileName(String thisFileName) {
        ArrayList<String> fileNameList = adapter.getSelectedList();
        for (int i = 0; i < fileNameList.size(); i++) {
            if (fileNameList.get(i).equals(thisFileName)) {
                if (i + 1 < fileNameList.size()) {
                    return fileNameList.get(i + 1);
                }
            }
        }
        return null;
    }

    private void scan() {
        adapter.setData(FileScanner.scan());
        adapter.notifyDataSetChanged();
        selectAllCheckBox.setChecked(true);
    }

    @Override
    protected void onDestroy() {
        SpeakWord.getInstance().stop();
        SpeakWord.getInstance().shutdown();
        super.onDestroy();
    }
}
