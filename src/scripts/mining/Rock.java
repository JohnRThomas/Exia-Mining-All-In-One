package scripts.mining;

import java.awt.Color;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public enum Rock {

	CLAY				("Clay", new String[]{"Clay"},true, true, new Color(98,74,42), new Color(122,100,70)),
	COPPER				("Copper", new String[]{"Copper ore"},true, true, new Color(74,48,32), new Color(129,63,37)),
	TIN					("Tin", new String[]{"Tin ore"},true, true, new Color(106,106,106), new Color(114,114,114)),
	LIMESTONE			("Limestone", new String[]{"Limestone"},true, true, new Color(110,110,110)),
	BLUERITE			("Bluerite", new String[]{"Bluerite ore"},true, true, new Color(0,47,122)),
	IRON				("Iron", new String[]{"Iron ore"},true, true, new Color(32,17,14)),
	//ELEMENTAL			("Elemental", new String[]{"Elemental ore"}, true, true, new Color(0,0,0)),
	//DAEYALT			("Daeyalt", new String[]{"Daeyalt ore"}, true, false),
	SILVER				("Silver", new String[]{"Silver ore"},true, true, new Color(141,134,120), new Color(149,149,149)),
	ESSENCE				("Essence", new String[]{"Rune essence", "Pure essence"},true, true),
	COAL				("Coal", new String[]{"Coal"},true, true, new Color(24,24,17), new Color(0,0,0)),
	PAY_DIRT			("Ore vein", new String[]{"Pay-dirt"},false, false),
	SANDSTONE			("Sandstone", new String[]{"Sandstone (1kg)", "Sandstone (2kg)", "Sandstone (5kg)", "Sandstone (10kg)"},true, true, new Color(74,47,11)),
	GEMS				("Gems", new String[]{"Uncut opal", "Uncut jade", "Uncut red topaz", "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond"},true, true, new Color(66,0,63)),
	GOLD				("Gold", new String[]{"Gold ore"},true, true, new Color(129,86,0), new Color(106,88,30)),
	GRANITE				("Granite", new String[]{"Granite (500g)", "Granite (2kg)", "Granite (5kg)"},true, true, new Color(94,66,40)),
	MITHRIL				("Mithril", new String[]{"Mithril ore"},true, true, new Color(47,47,66)),
	ADAMANTITE			("Adamantite", new String[]{"Adamantite ore"}, true, true, new Color(52,60,52)),
	BANE				("Bane", new String[]{"Bane ore"}, true, false),
	LIVING_ROCK_REMAINS	("Living rock remains", new String[]{"Living minerals"}, true, false),
	CONCENTRATED_COAL	("Concentrated Coal", new String[]{"Coal"},	true, false),
	CONCENTRATED_GOLD	("Concentrated Gold", new String[]{"Gold ore"}, true, false),
	RUNITE				("Runite", new String[]{"Runite ore"}, true, true, new Color(73,98,102)),
	SEREN				("Seren", new String[]{"Corrupted ore"}, true, false),
	UNKNOWN				("Unknown", new String[]{}, false, false);

	public String name;
	public String[] oreNames;
	public boolean rs3;
	public boolean osrs;
	public Color[] colors;

	Rock(String name, String[] oreNames, boolean rs3, boolean osrs){
		this.name = name;
		this.oreNames = oreNames;
		this.rs3 = rs3;
		this.osrs = osrs;
		colors = new Color[0];
	}

	Rock(String name, String[] oreNames, boolean rs3, boolean osrs, Color... colors){
		this.name = name;
		this.oreNames = oreNames;

		this.rs3 = rs3;
		this.osrs = osrs;
		this.colors = colors;
	}

	public static ObservableList<String> getOres(boolean isRS3){
		ObservableList<String> ores = FXCollections.observableArrayList();
		for (int i = 0; i < Rock.values().length; i++) {
			if(isRS3 && Rock.values()[i].rs3)ores.add(Rock.values()[i].name);
			else if(!isRS3 && Rock.values()[i].osrs)ores.add(Rock.values()[i].name);
		}
		return ores;
	}

	public static Rock getByName(String name){
		return Rock.valueOf(name.toUpperCase().replace(" ", "_").replace("-", "_"));
	}
}
