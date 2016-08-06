package scripts.framework;

import java.util.LinkedList;

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
	private LinkedList<TreeTask> tasks = new LinkedList<TreeTask>();
	
	/**
	 * This is where the magic happens. Performs a sort of in-order
	 * traversal of the Task tree based on the validator. Task Trees
	 * will be executed in the order that they are in the list.
	 * The order can be modified by adding to either the front or the
	 * end of the list. ALL TREE ROOTS WILL BE TESTED FOR VALIDITY NO
	 * MATTER WHAT HAPPENS TO THE ROOTS BEFORE THEM.
	 */
	@Override
	public final void onLoop() {
		
		// Loop over all roots in order.
		for(final TreeTask t : tasks){
			TreeTask currentTask = t;
			
			// Traverse the tree starting at the root in tasks.
			while(currentTask != null){
				// If the task validates, execute it and then move on
				// to it's success task. Otherwise, move on to it's 
				// failure task.
				if(currentTask.validate()){
					currentTask.execute();
					currentTask = currentTask.successTask();
				} else {
					currentTask = currentTask.failureTask();
				}
			}
		}
	}
	
	/**
	 * Add a new task tree to the beginning of the execution queue.
	 * 
	 * @param task
	 */
	public void addLast(TreeTask task){
		tasks.addLast(task);
	}
	
	/**
	 * Add a new task tree to the end of the execution queue.
	 * 
	 * @param task
	 */
	public void addFirst(TreeTask task){
		tasks.addFirst(task);
		
	}
	
	/**
	 * Make this the only task tree in the queue.
	 * 
	 * @param task
	 */
	public void setRoot(TreeTask task){
		tasks.clear();
		tasks.add(task);
	}
	
	/**
	 * Removes the given task tree from the queue.
	 * 
	 * @param task
	 */
	public void remove(TreeTask task){
		tasks.remove(task);
	}
}
