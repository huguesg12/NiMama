package com.dico.dicochinois;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ListeCarAdapter extends CursorAdapter {

	Context context;
	Cursor cursor;
	LayoutInflater layoutInflater;

	@SuppressLint("NewApi") 
	public ListeCarAdapter(Context context, Cursor cursor, int flags)
	{
		super(context, cursor, flags);
		this.context=context;
		this.cursor=cursor;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public void changeCursor (Cursor cursor) {
		super.changeCursor(cursor);
		this.cursor=cursor;
	}
	
	@Override
	public long getItemId(int position) {
		if (cursor.moveToPosition(position)) {
			return cursor.getLong(0);
		} else {
			return 0;
		}
	}

	static class ViewHolder {
		public TextView caractere;
		public TextView prononciation;
		public TextView traduction;
		public TextView score;
		public TextView nbI;
	}
/*
	@SuppressLint("InflateParams") @Override
	public View getView(int r, View convertView, ViewGroup parent) {
		cursor.moveToPosition(r);
		ViewHolder holder = null;
		// Si la vue n'est pas recyclée
		if(convertView == null) {
			// On récupère le layout
			convertView  = layoutInflater.inflate(R.layout.cursorlistecar, null);

			holder = new ViewHolder();
			// On place les widgets de notre layout dans le holder
			holder.caractere = (TextView) convertView.findViewById(R.id.display_caractere);
			holder.prononciation = (TextView) convertView.findViewById(R.id.display_pron);
			holder.traduction = (TextView) convertView.findViewById(R.id.display_traduction);
			holder.score = (TextView) convertView.findViewById(R.id.display_score);
			holder.nbI = (TextView) convertView.findViewById(R.id.display_nbI);

			// puis on insère le holder en tant que tag dans le layout
			convertView.setTag(holder);
		} else {
			// Si on recycle la vue, on récupère son holder en tag
			holder = (ViewHolder)convertView.getTag();
		}
		holder.caractere.setText(cursor.getString(1));
		holder.traduction.setText(cursor.getString(2));
		holder.prononciation.setText(cursor.getString(3));
		holder.score.setText(String.valueOf(cursor.getFloat(5)));
		holder.nbI.setText(String.valueOf(cursor.getInt(4)));
		return convertView;
	}
*/
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder)convertView.getTag();
		
		holder.caractere.setText(cursor.getString(1));
		holder.traduction.setText(cursor.getString(2));
		holder.prononciation.setText(cursor.getString(3));
		holder.score.setText(String.valueOf(cursor.getFloat(5)));
		holder.nbI.setText(String.valueOf(cursor.getInt(4)));
		convertView.setTag(holder);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View v = layoutInflater.inflate(R.layout.cursorlistecar, parent, false);

		
		ViewHolder holder = new ViewHolder();
		// On place les widgets de notre layout dans le holder
		holder.caractere = (TextView) v.findViewById(R.id.display_caractere);
		holder.prononciation = (TextView) v.findViewById(R.id.display_pron);
		holder.traduction = (TextView) v.findViewById(R.id.display_traduction);
		holder.score = (TextView) v.findViewById(R.id.display_score);
		holder.nbI = (TextView) v.findViewById(R.id.display_nbI);

		// puis on insère le holder en tant que tag dans le layout
		v.setTag(holder);
		return v;
	}

}
