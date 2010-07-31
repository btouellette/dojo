package dojo;

public abstract class Card
{
	protected final String id;
	protected boolean isDynasty;
	
	public Card(String id)
	{
		this.id = id;
	}
	
	public String getID()
	{
		return id;
	}
	
	public boolean isDynasty()
	{
		return isDynasty;
	}
	
	public boolean isFate()
	{
		return !isDynasty;
	}
}
