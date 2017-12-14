package bhw1899.paridae.model;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import bhw1899.paridae.R;
import bhw1899.paridae.constants.Constants;
import bhw1899.paridae.view.TextEditActivity;

public class FileListAdapter extends BaseAdapter {
    private ArrayList<String> fileNameList = new ArrayList<String>();
    private ArrayList<Boolean> selectionList = new ArrayList<Boolean>();
    private LayoutInflater inflater;
    private int positionInReading = -1;
    private OnCheckStatusChangeListener checkStatusListener = null;
    private Context context = null;

    public FileListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<String> list) {
        fileNameList.clear();
        fileNameList.addAll(list);

        selectionList.clear();
        for (String s : fileNameList) {
            selectionList.add(true);
        }
    }

    @Override
    public int getCount() {
        return fileNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private int getPosition(String fileName) {
        for (int i = 0; i < fileNameList.size(); i++) {
            if (fileNameList.get(i).equals(fileName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.file_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.filename);
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        String displayString = fileNameList.get(position);
        if (position == positionInReading) {
            displayString += Constants.PLAYING_STRING;
        }
        viewHolder.fileName.setText(displayString);
        viewHolder.checkBox.setChecked(selectionList.get(position));
        viewHolder.fileName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                View convertTmpView = (View) v.getParent();
                ViewHolder viewHolderTmp = (ViewHolder) convertTmpView.getTag();
                String fileName = (String) viewHolderTmp.fileName.getText();
                if (fileName.endsWith(Constants.PLAYING_STRING)) {
                    fileName = fileName.replace(Constants.PLAYING_STRING, "");
                }
                Intent intent = new Intent();
                intent.setClass(context, TextEditActivity.class);
                intent.putExtra(Constants.INTENT_KEY_FILE_NAME, fileName);
                context.startActivity(intent);
            }
        });
        viewHolder.checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                View convertTmpView = (View) v.getParent();
                ViewHolder viewHolderTmp = (ViewHolder) convertTmpView.getTag();
                selectionList.set(viewHolderTmp.position, viewHolderTmp.checkBox.isChecked());
                if (checkStatusListener != null) {
                    boolean allChecked = true;
                    for (Boolean checkStatus : selectionList) {
                        if (!checkStatus) {
                            allChecked = false;
                        }
                    }
                    checkStatusListener.onCheckStatusChanged(allChecked);
                }
            }
        });
        return convertView;
    }

    public void setSelectAll() {
        for (int i = 0; i < selectionList.size(); i++) {
            selectionList.set(i, true);
        }
    }

    public void setSelectNone() {
        for (int i = 0; i < selectionList.size(); i++) {
            selectionList.set(i, false);
        }
    }

    public boolean getItemCheckStatus(int position) {
        return selectionList.get(position);
    }

    public void setItemCheckStatus(int position, boolean status) {
        selectionList.set(position, status);
    }

    public void setReading(String fileName) {
        positionInReading = getPosition(fileName);
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedList() {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < fileNameList.size(); i++) {
            if (selectionList.get(i)) {
                result.add(fileNameList.get(i));
            }
        }
        return result;
    }

    public class ViewHolder {
        public CheckBox checkBox;
        public TextView fileName;
        public int position;
    }

    public interface OnCheckStatusChangeListener {
        public void onCheckStatusChanged(boolean selectAll);
    }

    public void setOnCheckStatusChangeListener(OnCheckStatusChangeListener l) {
        checkStatusListener = l;
    }

}
