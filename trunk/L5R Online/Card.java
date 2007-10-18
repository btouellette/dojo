// Card.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.util.ArrayList;

class Card
{
	//Info
	String id, type;
	String name;
	ArrayList<String> imageLocation, imageEdition;
	ArrayList<String> legal;
	String edition, text, cost, focus;
	String clan, province_strength, gold_production, starting_honor;
	String force, chi, personal_honor, honor_req;

    public Card(String id)
    {
		this.id = id;
		imageLocation = new ArrayList<String>();
		imageEdition = new ArrayList<String>();
		legal = new ArrayList<String>();
    	//Wait for the card to be filled with info from the parser
    }

    public void setID(String id)
    {
		this.id = id;
	}

    public void setType(String type)
    {
		this.type = type;
	}

    public void setName(String name)
    {
		this.name = name;
	}

    public void setImageLocation(String imageLocation)
    {
		this.imageLocation.add(imageLocation);
	}

    public void setImageEdition(String imageEdition)
    {
		this.imageEdition.add(imageEdition);
	}

	public void setEdition(String edition)
	{
		this.edition = edition;
	}

    public void setLegal(String legal)
    {
		this.legal.add(legal);
	}

    public void setText(String text)
    {
		this.text = text;
	}

    public void setCost(String cost)
    {
		this.cost = cost;
	}

    public void setFocus(String focus)
    {
		this.focus = focus;
	}

    public void setClan(String clan)
    {
		this.clan = clan;
	}

    public void setProvinceStrength(String province_strength)
    {
		this.province_strength = province_strength;
	}

    public void setGoldProduction(String gold_production)
    {
		this.gold_production = gold_production;
	}

    public void setStartingHonor(String starting_honor)
    {
		this.starting_honor = starting_honor;
	}

    public void setForce(String force)
    {
		this.force = force;
	}

    public void setChi(String chi)
    {
		this.chi = chi;
	}

    public void setPersonalHonor(String personal_honor)
    {
		this.personal_honor = personal_honor;
	}

	public void setHonorReq(String honor_req)
    {
		this.honor_req = honor_req;
	}

	public String getID()
	{
		return id;
	}

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getEdition()
	{
		return edition;
	}

	public String getText()
	{
		return text;
	}

	public String getCost()
	{
		return cost;
	}

	public String getFocus()
	{
		return focus;
	}

	public String getClan()
	{
		return clan;
	}

	public String getProvinceStrength()
	{
		return province_strength;
	}

	public String getGoldProduction()
	{
		return gold_production;
	}

	public String getStartingHonor()
	{
		return starting_honor;
	}

	public String getForce()
	{
		return force;
	}

	public String getChi()
	{
		return chi;
	}

	public String getPersonalHonor()
	{
		return personal_honor;
	}

	public String getHonorReq()
	{
		return honor_req;
	}
}