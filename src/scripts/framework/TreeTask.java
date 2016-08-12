package scripts.framework;

import com.runemate.game.api.script.framework.task.Task;

/**
 * 
 * This is a base class for Branches and Leaves to extend.
 * This class allows certain functions to be overridden as
 * final in it's subclasses while at the same time making 
 * sure that this class cannot be extended directly. 
 * 
 * Extension of this class instead of LeafTask or BranchTask
 * could lead to undefined behavior.
 * 
 * @author Exia
 *
 */
public abstract class TreeTask extends Task {
	/**
	 * This constructor is default access so that it cannot be
	 * extended directly outside of the framework package.
	 */
	TreeTask(){}
	
	/**
	 * @return TreeTask - The task that should be evaluated on success of validation.
	 */
	public abstract TreeTask successTask();
	
	/**
	 * @return TreeTask - The task that should be evaluated on failure of validation.
	 */
	public abstract TreeTask failureTask();
	
	/**
	 * @return boolean - Whether or not this node is a leaf.
	 */
	public abstract boolean isLeaf();
}
