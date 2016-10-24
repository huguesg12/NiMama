package com.dico.dicochinois;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MenuPrin extends Activity {
	public static final int RESULT_Login = 1;
	Button nouveau_mot = null;
	Button voir_liste = null;
	Button interrogation = null;
	Button aPropos = null;
	Button deconnexion = null;
	TextView texteAPropos = null;
	boolean isOpenAbout = false;
	final static int SPEED = 300;
	int user;

	//deroulage A Propos
	public boolean toggleAbout() {
		//Animation de transition.
		TranslateAnimation animation = null;

		// On passe de ouvert � ferm� (ou vice versa)
		isOpenAbout = !isOpenAbout;

		// Si le menu est d�j� ouvert
		if (isOpenAbout) 
		{
			// Animation de translation du bas vers le haut
			animation = new TranslateAnimation(0.0f, 0.0f, -texteAPropos.getHeight(), 0.0f);
			animation.setAnimationListener(openAboutListener);
		} else
		{
			// Sinon, animation de translation du haut vers le bas
			animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -texteAPropos.getHeight());
			animation.setAnimationListener(closeAboutListener);
		}

		// On d�termine la dur�e de l'animation
		animation.setDuration(SPEED);
		// On ajoute un effet d'acc�l�ration
		animation.setInterpolator(new AccelerateInterpolator());
		// Enfin, on lance l'animation
		texteAPropos.startAnimation(animation);

		return isOpenAbout;
	}

	Animation.AnimationListener closeAboutListener = new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			//On dissimule l'a propos
			texteAPropos.setVisibility(View.GONE);
		}

		public void onAnimationRepeat(Animation animation) {

		}

		public void onAnimationStart(Animation animation) {

		}
	};

	/* Listener pour l'animation d'ouverture de l'a propos */
	Animation.AnimationListener openAboutListener = new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationStart(Animation animation) {
			//On affiche l'a propos
			texteAPropos.setVisibility(View.VISIBLE);
		}
	};

	private OnClickListener clickListenerNouveauMot = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent intent = new Intent(MenuPrin.this, CreationMot.class);
			intent.putExtra("userid", user);
			startActivity(intent);
		}
	};

	private OnClickListener clickListenerListe = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent intent = new Intent(MenuPrin.this, ListeCar.class);
			intent.putExtra("userid", user);
			startActivity(intent);
		}
	};

	private OnClickListener clickListenerInterrogation = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MenuPrin.this, Interrogation.class);
			intent.putExtra("userid", user);
			startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerDeconnexion = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("userid", -1);
			editor.commit();
			startActivityForResult(new Intent(MenuPrin.this, Login.class), RESULT_Login);
		}
		
	};

	/*
	private OnClickListener clickListenerConstruction = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			Toast.makeText(v.getContext(), "Pas encore disponible", Toast.LENGTH_SHORT).show();

		}
	};
	 */

	private OnClickListener clickListenerAPropos= new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// � pour afficher ou cacher l'a propos
			if(toggleAbout())
			{
				// Si le Slider est ouvert�
				// � on change le texte en "Cacher"
				aPropos.setText(R.string.hide);
			}else
			{
				// Sinon on met "Afficher"
				aPropos.setText(R.string.about);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		user = preferences.getInt("userid", -1);
		if(user==-1) {
			startActivityForResult(new Intent(MenuPrin.this, Login.class), RESULT_Login);
		}
		
		setContentView(R.layout.activity_menu_prin);

		nouveau_mot = (Button) findViewById(R.id.nouveau_mot);
		voir_liste = (Button) findViewById(R.id.voir_liste);
		interrogation = (Button) findViewById(R.id.interrogation);
		aPropos = (Button) findViewById(R.id.about);
		deconnexion = (Button) findViewById(R.id.deconnexion);

		nouveau_mot.setOnClickListener(clickListenerNouveauMot);
		voir_liste.setOnClickListener(clickListenerListe);
		interrogation.setOnClickListener(clickListenerInterrogation);
		aPropos.setOnClickListener(clickListenerAPropos);
		deconnexion.setOnClickListener(clickListenerDeconnexion);


		// On r�cup�re le menu
		texteAPropos = (TextView) findViewById(R.id.about_content);

	}

	private void startup(Intent i) 
	{       
		user = i.getIntExtra("userid",-1);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("userid", user);
		editor.commit();
		DicoDAO ddao = new DicoDAO(this);
		ddao.open();
		ddao.synch(user);
		ddao.close();
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{ 
		if(requestCode == RESULT_Login && resultCode == RESULT_CANCELED)  
			finish(); 
		else 
			startup(data);
	}

}
