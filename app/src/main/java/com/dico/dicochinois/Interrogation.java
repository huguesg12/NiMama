package com.dico.dicochinois;

/*
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Interrogation extends Activity{

	TextView question = null;
	EditText reponse = null;
	Button valider = null;
	Button fermer = null;
	TextView correction = null;
	String vraieReponse;
	DicoDAO dicoDao = null;
	Cursor listeCar = null;
	ArrayList<Deuplet> listeAct = null;
	float sommePoids = 0;
	int maxNbi=0;
	int positionAct=0;
	int iduser;
	
	@Override
	protected void onStop() {
		super.onStop();
		listeCar.close();
		if(dicoDao!=null)
			dicoDao.close();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		iduser = getIntent().getIntExtra("userid",-1);

		setContentView(R.layout.interrogation);
		question = (TextView) findViewById(R.id.question);
		reponse = (EditText) findViewById(R.id.reponse);
		valider = (Button) findViewById(R.id.valider_reponse);
		fermer = (Button) findViewById(R.id.close_interro);
		correction = (TextView) findViewById(R.id.correction);
		
		valider.setOnClickListener(clickListenerValider);
		fermer.setOnClickListener(clickListenerFermer);
		dicoDao = new DicoDAO(this);
		dicoDao.open();
		listeCar=dicoDao.pullCars();
		sommePoids();
		pullQuestion();
	}

	private void sommePoids() {
		listeAct = new ArrayList<Deuplet>();
		int position=0;
		if(listeCar.moveToFirst())
		{
			do
			{
				maxNbi = Math.max(maxNbi, listeCar.getInt(4));
				sommePoids+=listeCar.getFloat(7);
				listeAct.add(new Deuplet(position, listeCar.getFloat(7), listeCar.getInt(4),
						listeCar.getFloat(5)));
				System.out.println(position + " " + listeCar.getFloat(7));
				position++;
			}
			while(listeCar.moveToNext());
			Collections.sort(listeAct);
			
		}
	}

	/*
	private void calculPoids() {
		
		poids=new ArrayList<Deuplet>();
		int maxNbi = 0;
		int nbi=0;
		int position=0;
		if(listeCar.moveToFirst()) {
			do {
				nbi = listeCar.getInt(4);
				poids.add(new Deuplet(position, nbi, listeCar.getFloat(5), listeCar.getInt(6)));
				maxNbi=Math.max(nbi, maxNbi);
				position++;
			}
			while(listeCar.moveToNext());
			for (int i=0; i<poids.size(); i++)
			{
				poids.get(i).normalize(maxNbi);
				sommeScore+=poids.get(i).getValeur();
			}
			Collections.sort(poids);
		}
	}
	*/
	
	private OnClickListener clickListenerFermer = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private OnClickListener clickListenerValider = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String rep;
			if(reponse.getText().toString().equals(vraieReponse))
			{
				repondre(1);
				rep=getResources().getString(R.string.good_rep);
			}
			else
			{
				repondre(0);
				rep=getResources().getString(R.string.bad_rep);
			}
			reponse.setText("");
			correction.setText(rep);
			pullQuestion();
		}
	};
	
	public void repondre(int i) {
		int nbiAct = listeAct.get(positionAct).getNbi();
		maxNbi=Math.max(maxNbi, nbiAct+1);
		int freq = listeCar.getInt(6);
		
		dicoDao.repondre(i, listeCar.getInt(0), listeAct.get(positionAct).getScore(), 
				nbiAct, freq, maxNbi, iduser, listeCar.getInt(8), listeCar.getInt(9));
		
		listeAct.get(positionAct).setNbi(nbiAct+1);
		//System.out.println("avt" + listeAct.get(positionAct).getScore());
		float newScore = (listeAct.get(positionAct).getScore()*nbiAct+i)/(nbiAct+1);
		listeAct.get(positionAct).setScore(newScore);
		//System.out.println("apr" + listeAct.get(positionAct).getScore());
		float newPoids = (float) (Math.max(0.1, 1-newScore)*(freq+1)/6*Math.max(0.1, 1-((nbiAct+1)/(float)maxNbi)));
		listeAct.get(positionAct).setPoids(newPoids);
		/*System.out.println((nbiAct+1)/(float)maxNbi);
		System.out.println(newPoids);
		System.out.println(newScore);
		System.out.println(nbiAct);*/
	}
	
	public void pullQuestion() {
		if(listeCar.moveToFirst())
		{
			Random rand = new Random();
			System.out.println(sommePoids);
			double d=rand.nextDouble()*sommePoids;
			System.out.println("d " + d);
			double cumul=0;
			positionAct=0;
			do
			{
				listeCar.moveToPosition(listeAct.get(positionAct).getPosition());
				cumul+=listeCar.getFloat(7);
				System.out.println("cumul " + cumul);
				if(d>cumul) {
					listeCar.moveToNext();
					positionAct++;
				}
				System.out.println("PA" + positionAct);
				System.out.println(listeAct.get(positionAct).getPosition());
			}
			while(d>cumul);
			listeCar.moveToPosition(listeAct.get(positionAct).getPosition());
			question.setText(listeCar.getString(2));
			vraieReponse = listeCar.getString(1);
		} else {
			correction.setText(getResources().getString(R.string.dico_vide));
			reponse.setVisibility(View.GONE);
			valider.setVisibility(View.GONE);
		}
	}
	
	/*
	public void pullQuestion() {
		if(poids.size()>0)
		{
			Random rand = new Random();
			listeCar.moveToPosition(findPosition((float) rand.nextDouble()*sommeScore));
			question.setText(listeCar.getString(2));
			vraieReponse = listeCar.getString(1);
		} else {
			correction.setText(getResources().getString(R.string.dico_vide));
		}
	}
	*/

	/*
	public int findPosition(float d) {
		int curseur=-1;
		double cumul=0;
		do
		{
			curseur++;
			cumul+=poids.get(curseur).getValeur();
		}
		while(d>cumul);
		return curseur;
	}
	*/
	
	/*
	private String getServerData(String returnString) {

		InputStream is = null;

		String result = "";

		// Envoyer la requête au script PHP.
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(strURL);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// Conversion de la requête en string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result=sb.toString();

		}catch(Exception e){

			Log.e("log_tag", "Error converting result " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = new JSONArray(result);
			Random randomGenerator = new Random();
			int nbQ = randomGenerator.nextInt(jArray.length());
			*/
			/*
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				// Affichage ID_ville et Nom_ville dans le LogCat
				Log.i("log_tag","caractere"+json_data.getString("caractere")+
						",prononciation: "+json_data.getString("prononciation")
						);
				// Résultats de la requête
				returnString += "\n\t" + jArray.getJSONObject(i);
			}
			*/
			/*
			returnString = jArray.getJSONObject(nbQ).getString("caractere");
			vraieReponse = jArray.getJSONObject(nbQ).getString("prononciation");
			
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return returnString;
	}*/

}
