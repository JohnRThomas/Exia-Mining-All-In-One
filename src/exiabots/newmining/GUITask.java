package exiabots.newmining;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

import exiabots.mining.CustomPlayerSense;
import exiabots.newmining.GUI.GUIState;

public class GUITask extends BranchTask {
	private TreeTask success = new OpenGUITask();
	private TreeTask failure = new IsGUIOpenBranch();

	private SwapTask root;
	private GUI gui;
	TreeBot bot;

	public GUITask(TreeBot bot, SwapTask root, GUI gui) {
		this.root = root;
		this.bot = bot;
		this.gui = gui;
	}
	
	@Override
	public boolean validate() {
		return gui.getState() == GUIState.WAITING;
	}
	
	@Override
	public TreeTask successTask() {
		return success;
	}
	
	@Override
	public TreeTask failureTask() {
		return failure;
	}
	
	private class OpenGUITask extends LeafTask {

		@Override
		public void execute() {
			gui.load(bot.getMetaData().getName(), bot.getMetaData().getVersion(), Environment.isRS3(), bot.getMetaData().getHourlyPrice().compareTo(BigDecimal.ZERO) > 0);
		}
	}
	
	private class IsGUIOpenBranch extends BranchTask {
		
		private TreeTask success = new WaitTask();
		private TreeTask failure = new StartOrStopBranch();

		@Override
		public boolean validate() {
			return gui.getState() == GUI.GUIState.OPEN;
		}
		
		@Override
		public TreeTask successTask() {
			return success;
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}
	}
	
	private class StartOrStopBranch extends BranchTask {
		private TreeTask success = new StartMinerTask();
		private TreeTask failure = new StopMinerTask();

		@Override
		public boolean validate() {
			return gui.getState() == GUI.GUIState.PAINT;
		}
		
		@Override
		public TreeTask successTask() {
			return success;
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}

	}
	
	private class StartMinerTask extends LeafTask {
		
		@Override
		public void execute() {
			// Setup the paint so it can update on it's own.
			setupTimers(gui.paint);

			// Get the tree from the 
			TreeTask newTree = gui.miner.createRootTask();
			
			// Add a reflex agent updater to the front of the tree.
			if(gui.paint.showGraph) {
				newTree = new ReflexUpdateBranch(newTree, gui.paint);
			}

			CustomPlayerSense.intialize();
			
			// Swap out the GUI tree for the new tree.
			root.setTask(newTree);
		}		
		
		private void setupTimers(Paint paint) {
			// Schedule this to update the paint every second.
			new Timer("Paint Updater").schedule(new TimerTask(){ 
				public void run(){
					javafx.application.Platform.runLater(() -> {
						paint.update();
						if(paint.stop){
							cancel();
						}
					});
				}
			}, 0, 900);
			
			// Schedule this to update exp amounts every second
			new Timer("EXP Updater").schedule(new TimerTask(){ 
				public void run(){
					try {
						bot.getPlatform().invokeAndWait(() -> {
							if(paint.startEXP == -1)paint.startEXP = Skill.MINING.getExperience();
							paint.currentEXP = Skill.MINING.getExperience();
							paint.nextLevelEXP = Skill.MINING.getExperienceToNextLevel();
							paint.currentLevel = Skill.MINING.getCurrentLevel();
							paint.percentage = Skill.MINING.getExperienceAsPercent();			

							if(paint.stop){
								cancel();
							}
						});
					} catch (ExecutionException | InterruptedException e) {}
				}
			}, 0, 900);
			
		}

		private class ReflexUpdateBranch extends BranchTask {
			
			TreeTask failure;
			TreeTask success;
			Paint paint;
			
			public ReflexUpdateBranch(TreeTask nextTask, Paint paint){
				this.paint = paint;
				this.failure = nextTask;
				this.success = new LeafTask(){
					@Override
					public void execute() {
						Paint.status = "Regenerating reflex delay";
						ReflexAgent.reinitialize(ReflexAgent.getReactionTime());
						javafx.application.Platform.runLater(() -> {
							paint.updateGraph();
						});					
					}
				};
			}

			@Override
			public boolean validate() {			
				//At ~8 hours we need to generate a new line
				return System.currentTimeMillis() - paint.startTime >=  3600000 * 7.875 * (1 + ReflexAgent.resets);
			}
			
			@Override
			public TreeTask successTask() {
				return success;
			}
			
			@Override
			public TreeTask failureTask() {
				return failure;
			}
		}
	}
	
	private class StopMinerTask extends LeafTask {
		@Override
		public void execute() {
			if(bot != null)bot.stop();
		}
	}
}
