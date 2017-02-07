package exiabots;

import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

import exiabots.newmining.GUI;
import exiabots.newmining.GUITask;
import exiabots.newmining.SwapTask;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public class ExiaMinerAIO extends TreeBot implements EmbeddableUI {
	
	private GUI gui = new GUI();
	
	public ExiaMinerAIO(){
		setEmbeddableUI(this);
	}
	
	@Override
	public void onStart(String... args){
		setLoopDelay(0);
	}
	
	@Override
	public TreeTask createRootTask() {
		// Set up a a swappable root so that the GUITask
		// can be removed and replaced by a MiningTask
		// when the script is started. This is to avoid
		// continuously calling an IsGUIOpenTask that will
		// ALWAYS return false. This also allows the Mining
		// root task to be created at a later time when the
		// GUI has received all of the necessary info from
		// the user.
		SwapTask rootTask = new SwapTask();
		rootTask.setTask(new GUITask(this, rootTask, gui));
		
		// Return the the root task.
		return rootTask;
	}
	
	@Override
	public ObjectProperty<? extends Node> botInterfaceProperty() {
		return gui;
	}


}
/*public class ExiaMinerAIO extends LoopingScript implements EmbeddableUI{
	public static MiningStyle miner;
	public static String version = "";
	public static String name = "";
	public static ExiaMinerAIO instance;
	public static boolean isPaid = false;
	private AIOMinerGUI gui = new AIOMinerGUI();
	private Paint paint;
	private boolean catchErrors = true;
	public static boolean isRS3;
	
	@Override
	public ObjectProperty<Node> botInterfaceProperty() {
		return gui;
	}
	
	@Override
	public void onStart(String... args){
		setEmbeddableUI(this);
		
		setLoopDelay(0);
		isRS3 = Environment.isRS3();
		version = getMetaData().getVersion();
		name = getMetaData().getName();
		isPaid = getMetaData().getHourlyPrice().compareTo(BigDecimal.ZERO) > 0;
		instance = this;

		while(gui == null || gui.dispose == 0)Execution.delay(500);
		if(gui.dispose == 2){
			stop();
			return;
		}

		paint = gui.paint;
		CustomPlayerSense.intialize();

		miner = gui.miner;
		catchErrors = !Environment.isSDK() && gui.catchErrors;
		
		miner.onStart(args);
	}

	@Override
	public void onLoop() {

		try{
			miner.loop();
		}catch(Exception e){
			if(catchErrors){
				ErrorHandler.add(e);
			}else{
				throw e;
			}
		}
	}

	@Override
	public void onStop() {
		if(miner != null)miner.onStop();
		if(paint != null)paint.stop = true;
		
		System.gc();
		if(ErrorHandler.hasErrors()){
			ErrorHandler.throwAll(miner);
		}
	}
}*/