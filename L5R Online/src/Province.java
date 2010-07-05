import java.util.ArrayList;

class Province
{
	private ArrayList<PlayableCard> attachments;
	private PlayableCard card;
	
	public Province()
	{
		attachments = new ArrayList<PlayableCard>();
	}
	
	public void setCard(PlayableCard card)
	{
		this.card = card;
	}
	
	public void setCard(StoredCard card)
	{
		this.card = new PlayableCard(card.getID());
	}
	
	public PlayableCard getCard()
	{
		return card;
	}
	
	public ArrayList<PlayableCard> getAttachments()
	{
		return attachments;
	}
	
	public void destroy()
	{
		//TODO: Discard attachments/contents
	}
}