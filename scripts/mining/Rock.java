package scripts.mining;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public enum Rock {

	CLAY("Clay", 5.0, true, true, new int[]{7481, 7483, 13456, 13457, 13458}),
	COPPER("Copper", 17.5, true, true, new int[]{7478, 7479, 7480, 13450, 13451, 13452, 13708, 14884, 14885, 1488}),
	TIN("Tin", 17.5, true, true, new int[]{7484, 7486, 13447, 13448, 13449, 14883, 14864, 14863}),
	LIMESTONE("Limestone", 5.0, true, true),
	BLUERITE("Bluerite", 0.0, true, true),
	IRON("Iron", 35.0, true, true, new int[]{7487, 7488, 7489, 13444, 13445, 13446, 13710, 13711}),
	ELEMENTAL("Elemental", 0.0, true, true),
	DAEYALT("Daeyalt", 0.0, true, false),
	SILVER("Silver", 40.0, true, true, new int[]{13716, 13717, 13438, 13439, 13440}),
	ESSENCE("Essence", 0.0, true, true),
	COAL("Coal", 50.0, true, true, new int[]{13706, 13714, 14860, 14861, 14862}),
	PAY_DIRT("Pay-Dirt", 0.0, false, true),
	SANDSTONE("Sandstone", 0.0, true, true),
	GEMS("Gems", 65.0, true, true),
	GOLD("Gold", 65.0, true, true, new int[]{7490, 7492, 13707, 13715}),
	GRANITE("Granite", 0.0, true, true),
	MITHRIL("Mithril", 80.0, true, true, new int[]{13718, 13719, 14890, 14948, 14949}),
	ADAMANTITE("Adamantite", 95.0, true, true, new int[]{14168, 13720, 14887, 14889}),
	BANE("Bane", 0.0, true, false),
	LIVING_MINERALS("Living Minerals", 0.0, true, false),
	CONCENTRATED_COAL("Concentrated Coal", 0.0, true, false),
	CONCENTRATED_GOLD("Concentrated Gold", 0.0, true, false),
	RUNITE("Runite", 125.0, true, true),
	SEREN("Seren", 0.0, true, false),
	UNKNOWN("Unknown", 0.0, false, false);
	
	public String name;
	public double exp;
	public boolean rs3;
	public boolean osrs;
	public int[] ids;

	Rock(String name, double exp, boolean rs3, boolean osrs){
		this.name = name;
		this.exp = exp;
		this.rs3 = rs3;
		this.osrs = osrs;
		ids = new int[0];
	}
	
	Rock(String name, double exp, boolean rs3, boolean osrs, int[] ids){
		this.name = name;
		this.exp = exp;
		this.rs3 = rs3;
		this.osrs = osrs;
		this.ids = ids;
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
