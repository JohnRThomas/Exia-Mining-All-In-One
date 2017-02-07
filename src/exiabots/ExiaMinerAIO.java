package exiabots;

import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

import exiabots.newmining.GUI;
import exiabots.newmining.GUI.GUIState;
import exiabots.newmining.GUITask;
import exiabots.newmining.SwapTask;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

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

	@Override
	public void onStop() {
		if(gui != null && gui.getState() != GUIState.PAINT) javafx.application.Platform.runLater(() -> { gui.setValue(new BorderPane()); });
		if(gui != null && gui.miner != null) gui.miner.onStop();
		if(gui != null && gui.paint != null) gui.paint.stop = true;
		
	}

}
