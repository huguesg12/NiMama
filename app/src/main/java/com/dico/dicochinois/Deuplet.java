package com.dico.dicochinois;

public class Deuplet implements Comparable<Deuplet>{

	int position;
	float poids;
	int nbi;
	float score;
	
	public Deuplet(int position, float poids, int nbi, float score)
	{
		this.position=position;
		this.poids=poids;
		this.nbi=nbi;
		this.score=score;
	}

	@Override
	public int compareTo(Deuplet another) {
		return Float.valueOf(poids).compareTo(Float.valueOf(another.getPoids()));
	}

	public float getPoids() {
		return poids;
	}

	public int getPosition()
	{
		return position;
	}
	
	public int getNbi()
	{
		return nbi;
	}
	
	public float getScore()
	{
		return score;
	}
	
	public void setScore(float score)
	{
		this.score=score;
	}
	
	public void setNbi(int nbi)
	{
		this.nbi=nbi;
	}
	
	public void setPoids(float poids)
	{
		this.poids=poids;
	}
}
