package br.com.hackathon.hackathon1746;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IconedTextViewAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	Context mContext;
	String[] mItemArray;
	TypedArray mIconArray;

	public IconedTextViewAdapter(Context context, String[] itemArray,
			TypedArray iconArray) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mItemArray = itemArray;
		mIconArray = iconArray;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItemArray.length;
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
	public View getView(int arg0, View convertView, ViewGroup arg2) {

		if (convertView == null) {

			convertView = mInflater
					.inflate(R.layout.item_textview, arg2, false);

		}

		TextView tx = (TextView) convertView;

		tx.setText(mItemArray[arg0]);

		tx.setCompoundDrawablesWithIntrinsicBounds(
				mIconArray.getDrawable(arg0), null, null, null);

		return convertView;
	}

}
