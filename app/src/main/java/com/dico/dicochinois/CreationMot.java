package com.dico.dicochinois;

//import java.util.ArrayList;

//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.KeyListener;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ListView;
import android.widget.TextView;

public class CreationMot extends Activity{

	TextView texteCaractere = null;
	EditText caractere = null;
	KeyListener caractereListener = null;
	TextView textePrononciation = null;
	EditText prononciation = null;
	KeyListener prononciationListener = null;
	TextView texteTraduction = null;
	EditText traduction= null;
	KeyListener traductionListener = null;
	TextView texteFrequence = null;
	EditText frequence = null;
	KeyListener frequenceListener = null;
	//EditText exemple = null;
	//ListView connu = null;
	Button valider = null;
	Button suivant = null;
	Button precedent = null;
	Button fermer = null;
	ArrayList<Mot> definitions = null;
	boolean discovered=false;
	int choix=0;
	DicoDAO dicoDao = null;
	Mot newMot = null;
	int user=-1;

	private OnClickListener clickListenerValider = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!discovered){
				envoiCaractere();
			} else {
				envoiDef();
			}
			//Intent intent = new Intent(MenuPrin.this, CreationMot.class);
			//startActivity(intent);
		}
	};

	private OnClickListener clickListenerSuivant = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			choix=choix+1;
			pullDef();
			//Intent intent = new Intent(MenuPrin.this, CreationMot.class);
			//startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerPrecedent = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			choix=choix-1;
			pullDef();
			//Intent intent = new Intent(MenuPrin.this, CreationMot.class);
			//startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerFermer = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = getIntent().getIntExtra("userid",-1);
		setContentView(R.layout.creationmot);

		texteCaractere = (TextView) findViewById(R.id.caractere);
		caractere = (EditText) findViewById(R.id.input_caractere);
		caractereListener = caractere.getKeyListener();
		textePrononciation = (TextView) findViewById(R.id.prononciation);
		prononciation = (EditText) findViewById(R.id.input_prononciation);
		prononciationListener = prononciation.getKeyListener();
		texteTraduction = (TextView) findViewById(R.id.traduction);
		traduction = (EditText) findViewById(R.id.input_traduction);
		traductionListener = traduction.getKeyListener();
		texteFrequence = (TextView) findViewById(R.id.frequence);
		frequence = (EditText) findViewById(R.id.input_frequence);
		frequenceListener = frequence.getKeyListener();
		//exemple = (EditText) findViewById(R.id.input_exemple);
		//connu = (ListView) findViewById(R.id.input_connu);
		valider = (Button) findViewById(R.id.valider_caractere);
		precedent = (Button) findViewById(R.id.precedent);
		suivant = (Button) findViewById(R.id.suivant);
		fermer = (Button) findViewById(R.id.close_caractere);
		//String[] yesNo = {getString(R.string.yes), getString(R.string.no)};
		//String[] yesNo = {"oui", "non"};
		//On ajoute un adaptateur qui affiche des boutons radio (c'est l'affichage � consid�rer quand on ne peut
		//s�lectionner qu'un �l�ment d'une liste)
		//connu.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, yesNo));
		//On d�clare qu'on s�lectionne de base le premier �l�ment (Oui)
		//connu.setItemChecked(0, true);

		valider.setOnClickListener(clickListenerValider);
		suivant.setOnClickListener(clickListenerSuivant);
		precedent.setOnClickListener(clickListenerPrecedent);
		fermer.setOnClickListener(clickListenerFermer);
		
	}

	public void envoiCaractere()
	{
		// Appeler la m�thode pour r�cup�rer les donn�es JSON
		//getServerData(strURL);
		dicoDao = new DicoDAO(this);
		//Mot newMot = new Mot(caractere.getText().toString(), prononciation.getText().toString(), 
		//		traduction.getText().toString(), Integer.parseInt(frequence.getText().toString()));
		definitions = dicoDao.checkMot(caractere.getText().toString());
		caractere.setKeyListener(null);
		discoverDef();
		pullDef();
	}

	public void envoiDef()
	{
		if(choix==definitions.size()-1)
		{
			definitions.get(choix).setPrononciation(prononciation.getText().toString());
			definitions.get(choix).setTrad(traduction.getText().toString());
			int freq = 3;
			if(!frequence.getText().toString().matches(""))
			{
				freq = Integer.parseInt(frequence.getText().toString());
			}
			definitions.get(choix).setFrequence(freq);
		}
		dicoDao.open();
		newMot = dicoDao.addMot(definitions.get(choix), user);
		reset();
	}
	
	public void reset() {
		definitions = null;
		hideDef();
		choix=0;
		dicoDao = null;
		newMot = null;
		traduction.setText("");
		frequence.setText("");
		prononciation.setText("");
		caractere.setText("");
		caractere.setKeyListener(caractereListener);
	}

	public void hideDef()
	{
		textePrononciation.setVisibility(View.GONE);
		prononciation.setVisibility(View.GONE);
		texteTraduction.setVisibility(View.GONE);
		traduction.setVisibility(View.GONE);
		texteFrequence.setVisibility(View.GONE);
		frequence.setVisibility(View.GONE);
		precedent.setVisibility(View.GONE);
		suivant.setVisibility(View.GONE);
		discovered=false;
	}
	
	public void discoverDef()
	{
		textePrononciation.setVisibility(View.VISIBLE);
		prononciation.setVisibility(View.VISIBLE);
		texteTraduction.setVisibility(View.VISIBLE);
		traduction.setVisibility(View.VISIBLE);
		texteFrequence.setVisibility(View.VISIBLE);
		frequence.setVisibility(View.VISIBLE);
		discovered=true;
	}
	
	public void pullDef()
	{
		
		if(definitions.size()>0)
		{
			if(choix<definitions.size()-1)
			{
				Mot courant = definitions.get(choix);
				prononciation.setText(courant.getPrononciation());
				prononciation.setKeyListener(null);
				traduction.setText(courant.getTrad());
				traduction.setKeyListener(null);
				frequence.setText(String.valueOf(courant.getFrequence()));
				frequence.setKeyListener(null);
				suivant.setVisibility(View.VISIBLE);
			} else {
				prononciation.setKeyListener(prononciationListener);
				prononciation.setText("");
				traduction.setKeyListener(traductionListener);
				traduction.setText("");
				frequence.setKeyListener(frequenceListener);
				suivant.setVisibility(View.GONE);
			}
			if(choix==0) {
				precedent.setVisibility(View.GONE);
			} else {
				precedent.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
}
