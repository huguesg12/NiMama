package com.dico.dicochinois;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
//import android.widget.Toast;


public class ListeCar extends Activity{
	ListView vue;
	Button synchro = null;
	ListeCarAdapter adapter = null;
	int user=-1;

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = getIntent().getIntExtra("userid",-1);

		setContentView(R.layout.listecar);

		vue = (ListView) findViewById(R.id.listCar);
		synchro = (Button) findViewById(R.id.synchro);

		synchro.setOnClickListener(clickListenerSync);
		DicoDAO dicoDao = new DicoDAO(this);
		dicoDao.open();
		
		/*
		SimpleCursorAdapter adapter = new SimpleCursorAdapter (this, R.layout.cursorlistecar, 
				listeCar, new String[]{DatabaseHandler.DICO_CARACTERE, DatabaseHandler.DICO_PRON, 
				DatabaseHandler.DICO_TRAD, DatabaseHandler.DICO_SCORE, DatabaseHandler.DICO_NBI}, 
				new int[]{R.id.display_caractere, R.id.display_pron, R.id.display_traduction, 
				R.id.display_score, R.id.display_nbI}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		 */
		adapter = new ListeCarAdapter(this, dicoDao.pullCars(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		vue.setAdapter(adapter);
		dicoDao.close();
	}

	private OnClickListener clickListenerSync = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DicoDAO ddao = new DicoDAO(v.getContext());
			ddao.open();
			ddao.synch(user);
			adapter.changeCursor(ddao.pullCars());
			adapter.notifyDataSetChanged();
			ddao.close();
		}
	};

/*	private OnClickListener clickListenerConstruction = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(v.getContext(), "Pas encore disponible", Toast.LENGTH_SHORT).show();

		}
	};
*/
	
}
