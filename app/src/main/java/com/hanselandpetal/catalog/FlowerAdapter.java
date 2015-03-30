package com.hanselandpetal.catalog;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hanselandpetal.catalog.model.Flower;

public class FlowerAdapter extends ArrayAdapter<Flower> {

	private Context context;
	@SuppressWarnings("unused")
	private List<Flower> flowerList;

	public FlowerAdapter(Context context, int resource, List<Flower> objects) {
		super(context, resource, objects);
		this.context = context;
		this.flowerList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = 
				(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_flower, parent, false);

		//Display flower name in the TextView widget
		
		return view;
	}

}
