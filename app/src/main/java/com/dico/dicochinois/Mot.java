package com.dico.dicochinois;

public class Mot {

	private long idcar;
	private long iddico;
	private long idcc;
	private String caractere;
	private String prononciation;
	private String trad;
	private int frequence;
	private int nbInterrogations;
	private float poids;
	//private String exemple;
	private float score;
	
	public Mot(long id, long iddico, String caractere, String prononciation, String trad, int frequence) {
		super();
		this.idcar=id;
		this.setIddico(iddico);
		this.caractere=caractere;
		this.prononciation = prononciation;
		this.trad=trad;
		this.frequence=frequence;
		this.nbInterrogations=0;
		this.score=0;
		this.poids=calculPoids(1,frequence, 1, 1);
		//this.exemple=exemple;
		//this.score=score;
	}
	
	public static float calculPoids(float score, int frequence, int nbi, int maxNbi)
	{
		return (float) (Math.max(0.1, 1-score)*Math.max(0.1, 1-nbi/(float)maxNbi)*(frequence+1)/6);
	}
	/*
	public Mot(String caractere, String prononciation, String trad, int frequence) {
		super();
		this.id=0;
		this.caractere=caractere;
		this.prononciation = prononciation;
		this.trad=trad;
		this.frequence=frequence;
		//this.exemple=exemple;
		//this.score=0.0f;
	}
	*/
	public long getIdcar()
	{
		return idcar;
	}
	
	public String getCaractere()
	{
		return caractere;
	}
	
	public String getPrononciation()
	{
		return prononciation;
	}
	
	public String getTrad()
	{
		return trad;
	}
	
	public int getFrequence()
	{
		return frequence;
	}
	/*
	public String getExemple()
	{
		return exemple;
	}
	*/
	public float getScore()
	{
		return score;
	}
	
	public void setIdcar(long idcar)
	{
		this.idcar=idcar;
	}
	
	public void setCaractere(String caractere)
	{
		this.caractere=caractere;
	}
	
	public void setPrononciation(String prononciation)
	{
		this.prononciation=prononciation;
	}
	
	public void setTrad(String trad)
	{
		this.trad=trad;
	}
	
	public void setFrequence(int frequence)
	{
		this.frequence=frequence;
	}
	/*
	public void setExemple(String exemple)
	{
		this.exemple=exemple;
	}
	*/
	public void setScore(float score)
	{
		this.score=score;
	}

	public long getIddico() {
		return iddico;
	}
	public void setIddico(long iddico) {
		this.iddico = iddico;
	}
	public long getIdcc() {
		return idcc;
	}
	public void setIdcc(long idcc) {
		this.idcc = idcc;
	}
	public int getNbInterrogations() {
		return nbInterrogations;
	}
	public void setNbInterrogations(int nbInterrogations) {
		this.nbInterrogations = nbInterrogations;
	}
	public float getPoids() {
		return poids;
	}
	public void setPoids(float poids) {
		this.poids = poids;
	}
}
