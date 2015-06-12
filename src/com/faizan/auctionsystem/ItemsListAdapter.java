/**
 * 
 */
package com.faizan.auctionsystem;

import java.util.ArrayList;

import com.datamodels.Item;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * item row = R.layout.row_item populate list of {@link ItemsFragment}
 * 
 * @author Faizan
 * 
 */
public class ItemsListAdapter extends BaseAdapter {

	ArrayList<Item> items;
	LayoutInflater inflater;
	Context context;

	public ItemsListAdapter(ArrayList<Item> items, Context context) {
		super();
		this.items = items;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Item getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_item, parent, false);
			holder = new ViewHolder();
			holder.itemName = (TextView) convertView.findViewById(R.id.tv_eventName);
			holder.time = (TextView) convertView.findViewById(R.id.tv_eventTime);
			holder.image = (ImageView) convertView.findViewById(R.id.iv_map);
			holder.amount = (TextView) convertView.findViewById(R.id.tv_likeCount);
			holder.sold = (ImageView) convertView.findViewById(R.id.iv_sold);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Item item = getItem(position);
		holder.amount.setText("$" + String.format("%.2f", item.lastBidPrice));
		holder.itemName.setText(item.name);
		if (item.imageUri == null) {
			if (position % 2 == 0) {
				holder.image.setImageResource(R.drawable.nexus_one);
			} else {
				holder.image.setImageResource(R.drawable.nexus_two);
			}
		} else {
			ImageLoader.getInstance().displayImage(item.imageUri, holder.image);
		}
		if (!item.isSold) {

		}
		if (item.isSold) {
			holder.sold.setVisibility(View.VISIBLE);
			holder.time.setText("Won by: " + item.lastBidUser);
		} else {
			holder.time.setText(parseTime(position, item.endTime - System.currentTimeMillis()));
			holder.sold.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * returns in format HH:mm:ss
	 * 
	 * @param position
	 * @param timeLeft
	 * @return
	 */
	private String parseTime(int position, long timeLeft) {
		if (timeLeft < 0) {
			items.get(position).isSold = true;
			notifyDataSetChanged();
			return "";
		}
		long total = timeLeft / 1000;
		int hours = (int) (total / (60 * 60));
		total = total - 3600 * hours;
		int min = (int) (total / 60);
		total = total - 60 * min;
		int seconds = (int) total;
		String result = "";
		result = hours <= 9 ? "0" + hours : hours + "";
		result = result + ":" + (min <= 9 ? "0" + min : "" + min);
		result = result + ":" + (seconds <= 9 ? "0" + seconds : "" + seconds);
		return result;
	}

	/**
	 * ViewHolder fragmemt to avoid unnecessary calls of findViewById()
	 * 
	 * @author Faizan
	 * 
	 */
	public static class ViewHolder {
		ImageView sold;
		TextView itemName;
		TextView time;
		TextView amount;
		ImageView image;

	}

}
