package exiabots.framework;

import com.runemate.game.api.script.framework.LoopingScript;

/**
 * 
 * This is an extension of the LoopingScript in the same way that
 * TaskScript is. This class aims to provide a more robust implementation
 * than TaskScript by allowing Tasks to be executed on not only a 
 * successful validation, but also on a validation failure.
 * 
 * @author Exia
 *
 */
public abstract class TreeScript extends LoopingScript {
	TreeTask root;
	
	/**
	 * This is where the magic happens. Performs a sort of in-order
	 * traversal of the Task tree based on the validator. When a leaf
	 * node is reached, it will be executed instead of validated.
	 */
	@Override
	public final void onLoop() {
		if(root == null) throw new UnsupportedOperationException("Root of TaskTree was null.");
		
		TreeTask currentTask = root;
		
		// Traverse the tree starting at the root in tasks.
		while(!currentTask.isLeaf()){
			// If the task validates, move on to its success task.
			// Otherwise, move on to its failure task.
			if(currentTask.validate()){
				if(currentTask.successTask() == null) throw new UnsupportedOperationException("Branch had a null Success Task.");
				else currentTask = currentTask.successTask();
			} else {
				if(currentTask.failureTask() == null) throw new UnsupportedOperationException("Branch had a null Failure Task.");
				else currentTask = currentTask.failureTask();
			}
		}
		
		// When a leaf is reached, execute it.
		currentTask.execute();
	}
		
	/**
	 * Make this the only task tree in the queue.
	 * 
	 * @param task
	 */
	public void setRoot(TreeTask task){
		root = task;
	}
}