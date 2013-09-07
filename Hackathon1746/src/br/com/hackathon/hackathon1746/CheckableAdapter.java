package br.com.hackathon.hackathon1746;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class CheckableAdapter extends BaseAdapter implements
		OnItemClickListener {

	Context mContext;
	LayoutInflater mInflater;
	String[] mItems;
	int posChecked = -1;

	public CheckableAdapter(Context context, String[] items) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mItems = items;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup container) {

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.item_radiobutton,
					container, false);

		}

		RadioButton tx = (RadioButton) convertView;

		tx.setText(mItems[pos]);

		// tx.setCompoundDrawablesWithIntrinsicBounds(
		// android.R.drawable.btn_radio, 0, 0, 0);

		if (posChecked == pos) {

			tx.setChecked(true);

		} else {

			tx.setChecked(false);

		}

		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		RadioButton radioButton = (RadioButton) arg1;
		radioButton.setChecked(true);
		notifyDataSetChanged();
		posChecked = arg2;

	}

	public int  getSelectedItem() {
		
		return posChecked;

	}

}
