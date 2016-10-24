package com.dico.dicochinois;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class DicoDAO {

	protected final static int VERSION = 11;
	// Le nom du fichier qui représente ma base
	protected final static String NOM = "database.db";
	//public final static String SCORE = "Score";

	protected SQLiteDatabase mDb = null;
	protected DatabaseHandler mHandler = null;
	protected Mot mot = null;
	protected int user = -1;
	protected String caractere = null;
	protected ArrayList<Mot> mots = null;

	public static final String VERIF_MOT = "SELECT " + DatabaseHandler.DICO_KEY + " FROM " + 
			DatabaseHandler.DICO_TABLE_NAME + " WHERE " + DatabaseHandler.DICO_CARACTERE + " = ? AND " +
			DatabaseHandler.DICO_TRAD + " = ?";

	public static final String SELECT_CARS = "SELECT " + DatabaseHandler.DICO_KEY + ", " + 
			DatabaseHandler.DICO_CARACTERE + ", " + DatabaseHandler.DICO_TRAD + ", " +
			DatabaseHandler.DICO_PRON + ", " + DatabaseHandler.DICO_NBI + ", " +
			DatabaseHandler.DICO_SCORE + ", " + DatabaseHandler.DICO_FREQUENCE + ", " +
			DatabaseHandler.DICO_POIDS + ", " + DatabaseHandler.DICO_IDCC + ", " +
			DatabaseHandler.DICO_TEMP + " FROM " +	
			DatabaseHandler.DICO_TABLE_NAME +
			" ORDER BY " + DatabaseHandler.DICO_POIDS + " DESC";

	public static final String SELECT_UNSYNC = "SELECT " + DatabaseHandler.DICO_KEY + ", " + 
			DatabaseHandler.DICO_CARACTERE + ", " + DatabaseHandler.DICO_TRAD + ", " +
			DatabaseHandler.DICO_PRON + ", " + DatabaseHandler.DICO_NBI + ", " +
			DatabaseHandler.DICO_SCORE + ", " + DatabaseHandler.DICO_FREQUENCE + ", " +
			DatabaseHandler.DICO_IDCC + ", " + DatabaseHandler.DICO_IDCAR + ", " +
			DatabaseHandler.DICO_IDDICO + " FROM " +	DatabaseHandler.DICO_TABLE_NAME +
			" WHERE " + DatabaseHandler.DICO_TEMP + " >0";

	public DicoDAO(Context pContext) {
		this.mHandler = new DatabaseHandler(pContext, NOM, null, VERSION);
	}

	public SQLiteDatabase open() {
		mDb = mHandler.getWritableDatabase();
		return mDb;
	}

	public void close() {
		mDb.close();
	}

	public SQLiteDatabase getDb() {
		return mDb;
	}

	public static final String preAjoutURL = "http://www.huguesg.fr/chinois/PreAjoutCaractere.php";
	public static final String ajoutURL = "http://www.huguesg.fr/chinois/ajoutCaractere.php";
	public static final String synchro = "http://www.huguesg.fr/chinois/synchro.php";

	class AddMotTask extends AsyncTask<Mot, Integer, Mot> {

		@Override
		protected Mot doInBackground(Mot... params) {
			addMotOnline();
			return null;
		}

		private void addMotOnline() {

			String returnString="";
			InputStream is = null;

			String result=null;

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("idDico",String.valueOf(mot.getIddico())));
			nameValuePairs.add(new BasicNameValuePair("idCar",String.valueOf(mot.getIdcar())));
			nameValuePairs.add(new BasicNameValuePair("idUser",String.valueOf(user)));
			nameValuePairs.add(new BasicNameValuePair("caractere",mot.getCaractere()));
			nameValuePairs.add(new BasicNameValuePair("prononciation",mot.getPrononciation()));
			nameValuePairs.add(new BasicNameValuePair("trad",mot.getTrad()));
			nameValuePairs.add(new BasicNameValuePair("frequence",String.valueOf(mot.getFrequence())));

			try{

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(ajoutURL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			}catch(Exception e){
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			// Conversion de la requête en string
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
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
				for(int i=0;i<jArray.length();i++){
					// Résultats de la requête
					JSONObject jobj = jArray.getJSONObject(i);
					returnString += "\n\t" + jobj.toString();
					System.out.println(returnString);
					mot.setIdcc(jobj.getLong("idcc"));
					mot.setIddico(jobj.getLong("iddico"));
				}
			}catch(Exception e){
				Log.e("log_tag", "Error parsing data " + e.toString());
			}

		}
	}


	public Mot addMot(Mot mot, int user) {

		this.mot = mot;
		this.user=user;
		// Envoi de la commande http
		AddMotTask amt = new AddMotTask();
		amt.execute();
		try {
			amt.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(mot);

		return mot;
	}

	public void add(Mot mot) {
		Cursor select = mDb.rawQuery(VERIF_MOT, new String[]{mot.getCaractere(), mot.getTrad()});

		if(!select.moveToFirst())
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseHandler.DICO_CARACTERE, mot.getCaractere());
			values.put(DatabaseHandler.DICO_FREQUENCE, mot.getFrequence());
			values.put(DatabaseHandler.DICO_IDCAR, mot.getIdcar());
			values.put(DatabaseHandler.DICO_IDCC, mot.getIdcc());
			values.put(DatabaseHandler.DICO_IDDICO, mot.getIddico());
			values.put(DatabaseHandler.DICO_PRON, mot.getPrononciation());
			values.put(DatabaseHandler.DICO_TRAD, mot.getTrad());
			values.put(DatabaseHandler.DICO_TEMP, mot.getIdcc()==0||mot.getIddico()==0);
			values.put(DatabaseHandler.DICO_POIDS, mot.getPoids());
			mDb.insert(DatabaseHandler.DICO_TABLE_NAME, null, values);

			//SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//SharedPreferences.Editor editor = preferences.edit();
			//int score = preferences.getInt(SCORE, 0);
			//editor.putInt(SCORE, score+1);
			//editor.commit();
			//System.out.println("score " + score);
		} 
	}

	class CheckMotTask extends AsyncTask<Mot, Integer, Mot> {

		@Override
		protected Mot doInBackground(Mot... params) {
			checkMotOnline();
			return null;
		}

		private void checkMotOnline()
		{
			String returnString="";
			InputStream is = null;

			mots = new ArrayList<Mot>();
			String result=null;

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("caractere",caractere));

			// Envoi de la commande http
			try{

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(preAjoutURL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			}catch(Exception e){
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			// Conversion de la requête en string
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
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
			long idcar =0;
			try{
				JSONArray jArray = new JSONArray(result);
				JSONObject jobj = jArray.getJSONObject(0);
				idcar = jobj.getLong("id");
				for(int i=0;i<jArray.length();i++){
					// Résultats de la requête
					jobj = jArray.getJSONObject(i);
					returnString += "\n\t" + jobj.toString();
					System.out.println(returnString);
					long iddico=-1;
					int frequence=0;
					String caractereLocal=caractere;
					String prononciation = "";
					String traduction = "";
					if(!jobj.isNull("iddico")) {
						iddico = jobj.getLong("iddico");
						frequence = jobj.getInt("frequence");
						caractereLocal=jobj.getString("caractere");
						prononciation = jobj.getString("prononciation");
						traduction = jobj.getString("traduction");
						Mot newMot = new Mot(idcar, iddico, caractereLocal, prononciation, traduction, 
								frequence);
						mots.add(newMot);
					}

				}
			}catch(Exception e){
				Log.e("log_tag", "Error parsing data " + e.toString());
			}
			Mot newMot = new Mot(idcar, -1, caractere, "", "", 0);
			mots.add(newMot);
		}
	}

	public ArrayList<Mot> checkMot(String mot) {

		caractere=mot;

		CheckMotTask cmt = new CheckMotTask();
		cmt.execute();
		try {
			cmt.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return mots;
	}

	public Cursor pullCars() {
		Cursor c = mDb.rawQuery(SELECT_CARS, null);
		return c;
	}

	public void repondre(int reponse, int id, float score, int nbi, int frequence, 
			int maxNbi, int iduser, int idcc, int temp) {
		ContentValues value = new ContentValues();
		float newScore = (score*nbi+reponse)/(nbi+1);
		value.put(DatabaseHandler.DICO_SCORE, newScore);
		value.put(DatabaseHandler.DICO_NBI, nbi+1);
		value.put(DatabaseHandler.DICO_POIDS, Math.max(0.1, 1-newScore)*(frequence+1)/6*Math.max(0.1, 1-(nbi+1)/maxNbi));
		mDb.update(DatabaseHandler.DICO_TABLE_NAME, value, DatabaseHandler.DICO_KEY+ " = ?", 
				new String[] {String.valueOf(id)});
		System.out.println("temp " + temp);
		if(temp==0)
		{
			RepondreTask rt = new RepondreTask();
			
			rt.execute(idcc, iduser, newScore, nbi+1, id);
			try {
				rt.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class RepondreTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			repond((Integer) params[0], (Integer) params[1], (Float) params[2], 
					(Integer) params[3], (Integer) params[4]);
			return null;
		}

		private void repond(int idcc, int iduser, float score, int nbi, int id) {
			JSONObject json = new JSONObject();
			InputStream is=null;
			String result=null;
			
			try {
				json = new JSONObject();
				json.put("idcc", idcc);
				json.put("iduser", iduser);
				json.put("score", score);
				json.put("nbi", nbi);
				System.out.println("idcc " + idcc);
				System.out.println("iduser" + iduser);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpClient client = new DefaultHttpClient(httpParams);

				String url = "http://www.huguesg.fr/chinois/repondre.php";

				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes(
						"UTF8")));
				request.setHeader("json", json.toString());
				//client.execute(request);
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch(Exception e){
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			
			// Conversion de la requête en string
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				StringBuilder sb = new StringBuilder();
				String line = null; 
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				is.close();
				result=sb.toString();

				System.err.println("result " + result);
			}catch(Exception e){
				Log.e("log_tag", "Error converting result " + e.toString());
			}
			/*
			// Parse les données JSON
			try{
				JSONArray jArrayr = new JSONArray(result);
				JSONObject jobj = null;
				// Résultats de la requête
				jobj = jArrayr.getJSONObject(0);
				int idccol = jobj.optInt("idcc", -1);
				int idcarol = jobj.optInt("idcar", -1);
				int iddicool = jobj.optInt("iddico", -1);
				boolean doit=false;
				ContentValues values = new ContentValues();
				if(idccol>-1) {
					values.put(DatabaseHandler.DICO_IDCC, idccol);
					doit=true;
				}
				if(idcarol>-1) {
					values.put(DatabaseHandler.DICO_IDCAR, idcarol);
					doit=true;
				}
				if(iddicool>-1) {
					values.put(DatabaseHandler.DICO_IDDICO, iddicool);
					doit=true;
				}
				if(doit) {
					boolean ok = (idcc+idccol>-2)&&(iddico+iddicool>-2)&&(idcar+idcarol>-2);
					values.put(DatabaseHandler.DICO_TEMP, ok);
					mDb.update(DatabaseHandler.DICO_TABLE_NAME, values, 
						DatabaseHandler.DICO_KEY+ " = ?", new String[] {String.valueOf(id)});
				}
			}catch(Exception e){
				Log.e("log_tag", "Error parsing data " + e.toString());
			}
			*/
		}
	}


	class SynchTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			synch();
			return null;
		}

		private void synch() {

			InputStream is=null;
			String result=null;
			JSONObject json = new JSONObject();
			JSONArray jarray = new JSONArray();
			Cursor c = mDb.rawQuery(SELECT_UNSYNC, null);
			try {
				if(c.moveToFirst())
				{
					do {
						json = new JSONObject();
						json.put("idcc", c.getInt(7));
						json.put("idcar", c.getInt(8));
						json.put("iddico", c.getInt(9));
						json.put("car", c.getString(1));
						json.put("trad", c.getString(2));
						json.put("pron", c.getString(3));
						json.put("nbi", c.getInt(4));
						json.put("score", c.getFloat(5));
						json.put("freq", c.getInt(6));
						json.put("iduser", user);
						jarray.put(json);
					}
					while(c.moveToNext());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			c.close();

			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpClient client = new DefaultHttpClient(httpParams);

				String url = "http://www.huguesg.fr/chinois/synchro.php";

				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(jarray.toString().getBytes(
						"UTF8")));
				request.setHeader("json", jarray.toString());
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			}catch(Exception e){
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			// Conversion de la requête en string
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
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
				mDb.execSQL("delete from " + DatabaseHandler.DICO_TABLE_NAME);
				JSONArray jArrayr = new JSONArray(result);
				System.out.println(jArrayr);
				JSONObject jobj = null;
				for(int i=0;i<jArrayr.length();i++){
					// Résultats de la requête
					jobj = jArrayr.getJSONObject(i);

					ContentValues values = new ContentValues();
					int frequence = jobj.getInt("freq");
					int nbi = jobj.getInt("nbi");
					float score = (float) jobj.getDouble("score");
					int maxNbi = jobj.getInt("maxnbi");
					values.put(DatabaseHandler.DICO_IDCAR, jobj.getLong("idcar"));
					values.put(DatabaseHandler.DICO_IDCC, jobj.getLong("idcc"));
					values.put(DatabaseHandler.DICO_IDDICO, jobj.getLong("iddico"));
					values.put(DatabaseHandler.DICO_CARACTERE, jobj.getString("car"));
					values.put(DatabaseHandler.DICO_FREQUENCE, frequence);
					values.put(DatabaseHandler.DICO_PRON, jobj.getString("pron"));
					values.put(DatabaseHandler.DICO_TRAD, jobj.getString("trad"));
					values.put(DatabaseHandler.DICO_POIDS, Mot.calculPoids(score, frequence, nbi, maxNbi));
					values.put(DatabaseHandler.DICO_NBI, nbi);
					values.put(DatabaseHandler.DICO_SCORE, score);
					mDb.insert(DatabaseHandler.DICO_TABLE_NAME, null, values);					
				}
			}catch(Exception e){
				Log.e("log_tag", "Error parsing data " + e.toString());
			}
		}
	}

	public void synch(int user) {
		this.user=user;
		SynchTask st = new SynchTask();
		st.execute();
		try {
			st.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
