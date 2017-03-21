package com.oldbook.android.entity;


public class FriendEntity
{
	private int avatar;
	private int id;
	private String petName;
	private String evaluation;
	

	public FriendEntity(int id,String petName)
	{
		//this.avatar=avatar;
		this.id=id;
		this.petName=petName;

	}

	public int getAvatar() 
	{
		return avatar;
	}

	public void setAvatar(int avatar)
	{
		this.avatar = avatar;
	}

	public int getId()
	{
		return id;
	}

	public void setAccount(int id)
	{
		this.id = id;
	}

	public String getPetName()
	{
		return petName;
	}

	public void setPetName(String petName)
	{
		this.petName = petName;
	}
	public String getEvaluation()
	{
		return evaluation;
	}

	public void setEvaluation(String evaluation)
	{
		this.evaluation = evaluation;
	}
	
}