package scripts.mining;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public enum Rock {

	CLAY("Clay", new String[]{"Clay"}, new double[]{5.0}, true, true, new int[]{7481, 7483, 13456, 13457, 13458}),
	COPPER("Copper", new String[]{"Copper ore"}, new double[]{17.5}, true, true, new int[]{7478, 7479, 7480, 13450, 13451, 13452, 13708, 14884, 14885, 1488}),
	TIN("Tin", new String[]{"Tin ore"}, new double[]{17.5}, true, true, new int[]{7484, 7486, 13447, 13448, 13449, 14883, 14864, 14863}),
	LIMESTONE("Limestone", new String[]{"Limestone"}, new double[]{26.5}, true, true),
	BLUERITE("Bluerite", new String[]{"Bluerite ore"}, new double[]{17.5}, true, true),
	IRON("Iron", new String[]{"Iron ore"}, new double[]{35.0}, true, true, new int[]{7487, 7488, 7489, 13444, 13445, 13446, 13710, 13711}),
	ELEMENTAL("Elemental", new String[]{"Elemental ore"}, new double[]{0.0}, true, true),
	DAEYALT("Daeyalt", new String[]{"Daeyalt ore"}, new double[]{17.5}, true, false),
	SILVER("Silver", new String[]{"Silver ore"}, new double[]{40.0}, true, true, new int[]{13716, 13717, 13438, 13439, 13440}),
	ESSENCE("Essence", new String[]{"Rune essence", "Pure essence"}, new double[]{5.0, 5.0}, true, true),
	COAL("Coal", new String[]{"Coal"}, new double[]{50.0}, true, true, new int[]{13706, 13714, 14860, 14861, 14862}),
	PAY_DIRT("Pay-Dirt", new String[]{"Pay-dirt"}, new double[]{60.0}, false, true),
	SANDSTONE("Sandstone", new String[]{"Sandstone (1kg)", "Sandstone (2kg)", "Sandstone (5kg)", "Sandstone (10kg)"}, new double[]{30.0, 40.0, 50.0, 60.0}, true, true),
	GEMS("Gems", new String[]{"Uncut opal", "Uncut jade", "Uncut red topaz", "Uncut sapphire", "Uncut emerald", "Uncut diamond"}, new double[]{65.0, 65.0, 65.0, 65.0, 65.0, 65.0}, true, true),
	GOLD("Gold", new String[]{"Gold ore"}, new double[]{65.0}, true, true, new int[]{7490, 7492, 13707, 13715}),
	GRANITE("Granite", new String[]{"Granite (500g)", "Granite (2kg)", "Granite (5kg)"}, new double[]{50.0, 60.0, 75.0}, true, true),
	MITHRIL("Mithril", new String[]{}, new double[]{80.0}, true, true, new int[]{13718, 13719, 14890, 14948, 14949}),
	ADAMANTITE("Adamantite", new String[]{"Adamantite ore"}, new double[]{95.0}, true, true, new int[]{14168, 13720, 14887, 14889}),
	BANE("Bane", new String[]{"Bane ore"}, new double[]{90.0}, true, false),
	LIVING_MINERALS("Living rock remains", new String[]{"Living minerals"}, new double[]{25.0}, true, false),
	CONCENTRATED_COAL("Concentrated Coal", new String[]{"Coal"}, new double[]{50.0}, true, false),
	CONCENTRATED_GOLD("Concentrated Gold", new String[]{"Gold ore"}, new double[]{65.0}, true, false),
	RUNITE("Runite", new String[]{"Runite ore"}, new double[]{125.0}, true, true),
	SEREN("Seren", new String[]{"Corrupted ore"}, new double[]{296.7}, true, false),
	UNKNOWN("Unknown", new String[]{}, new double[]{0.0}, false, false);

	public String name;
	public HashMap<String, Double> exps = new HashMap<String, Double>();
	public boolean rs3;
	public boolean osrs;
	public int[] ids;

	Rock(String name, String[] oreNames, double[] exp, boolean rs3, boolean osrs){
		this.name = name;
		for(int i = 0; i < oreNames.length; i++){
			this.exps.put(oreNames[i], exp[i]);
		}
		this.rs3 = rs3;
		this.osrs = osrs;
		ids = new int[0];
		System.out.println(name);
	}
	
	Rock(String name, String[] oreNames, double[] exp, boolean rs3, boolean osrs, int[] ids){
		this.name = name;
		for(int i = 0; i < oreNames.length; i++){
			this.exps.put(oreNames[i], exp[i]);
		}
		this.rs3 = rs3;
		this.osrs = osrs;
		this.ids = ids;
		System.out.println(name);
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
