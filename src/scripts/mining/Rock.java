package scripts.mining;

import java.awt.Color;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public enum Rock {

	CLAY			("Clay", new String[]{"Clay"}, new double[]{5.0},
			true, true, new Color(98,74,42), new Color(122,100,70)),
	COPPER			("Copper", new String[]{"Copper ore"}, new double[]{17.5},
			true, true, new Color(74,48,32)),
	TIN				("Tin", new String[]{"Tin ore"}, new double[]{17.5},
			true, true, new Color(106,106,106)),
	LIMESTONE		("Limestone", new String[]{"Limestone"}, new double[]{26.5},
			true, true, new Color(110,110,110)),
	BLUERITE		("Bluerite", new String[]{"Bluerite ore"}, new double[]{17.5},
			true, true, new Color(0,47,122)),
	IRON			("Iron", new String[]{"Iron ore"}, new double[]{35.0},
			true, true, new Color(32,17,14)),
	//ELEMENTAL		("Elemental", new String[]{"Elemental ore"}, new double[]{0.0},
	//		true, true, new Color(0,0,0)),
	//DAEYALT			("Daeyalt", new String[]{"Daeyalt ore"}, new double[]{17.5},
	//		true, false),
	SILVER			("Silver", new String[]{"Silver ore"}, new double[]{40.0},
			true, true, new Color(141,134,120), new Color(149,149,149)),
	ESSENCE			("Essence", new String[]{"Rune essence", "Pure essence"}, new double[]{5.0, 5.0},
			true, true),
	COAL			("Coal", new String[]{"Coal"}, new double[]{50.0},
			true, true, new Color(24,24,17)),
	//PAY_DIRT		("Pay-Dirt", new String[]{"Pay-dirt"}, new double[]{60.0},
	//		false, true, new Color(0,0,0)),
	SANDSTONE		("Sandstone", new String[]{"Sandstone (1kg)", "Sandstone (2kg)", "Sandstone (5kg)", "Sandstone (10kg)"}, new double[]{30.0, 40.0, 50.0, 60.0},
			true, true, new Color(74,47,11)),
	GEMS			("Gems", new String[]{"Uncut opal", "Uncut jade", "Uncut red topaz", "Uncut sapphire", "Uncut emerald", "Uncut diamond"}, new double[]{65.0, 65.0, 65.0, 65.0, 65.0, 65.0},
			true, true, new Color(0,0,0)),
	GOLD			("Gold", new String[]{"Gold ore"}, new double[]{65.0},
			true, true, new Color(0,0,0)),
	GRANITE			("Granite", new String[]{"Granite (500g)", "Granite (2kg)", "Granite (5kg)"}, new double[]{50.0, 60.0, 75.0},
			true, true, new Color(94,66,40)),
	MITHRIL			("Mithril", new String[]{}, new double[]{80.0},
			true, true, new Color(47,47,66)),
	ADAMANTITE		("Adamantite", new String[]{"Adamantite ore"}, new double[]{95.0},
			true, true, new Color(52,60,52)),
	BANE			("Bane", new String[]{"Bane ore"}, new double[]{90.0},
			true, false),
	LIVING_MINERALS	("Living rock remains", new String[]{"Living minerals"}, new double[]{25.0},
			true, false),
	CONCEN_COAL		("Concentrated Coal", new String[]{"Coal"}, new double[]{50.0},
			true, false),
	CONCEN_GOLD		("Concentrated Gold", new String[]{"Gold ore"}, new double[]{65.0},
			true, false),
	RUNITE			("Runite", new String[]{"Runite ore"}, new double[]{125.0},
			true, true, new Color(0,0,0)),
	SEREN			("Seren", new String[]{"Corrupted ore"}, new double[]{296.7},
			true, false),
	UNKNOWN			("Unknown", new String[]{}, new double[]{0.0}, false, false);

	public String name;
	public HashMap<String, Double> exps = new HashMap<String, Double>();
	public boolean rs3;
	public boolean osrs;
	public Color[] colors;

	Rock(String name, String[] oreNames, double[] exp, boolean rs3, boolean osrs){
		this.name = name;
		for(int i = 0; i < oreNames.length; i++){
			this.exps.put(oreNames[i], exp[i]);
		}
		this.rs3 = rs3;
		this.osrs = osrs;
		colors = new Color[0];
	}

	Rock(String name, String[] oreNames, double[] exp, boolean rs3, boolean osrs, Color... colors){
		this.name = name;
		for(int i = 0; i < oreNames.length; i++){
			this.exps.put(oreNames[i], exp[i]);
		}
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
