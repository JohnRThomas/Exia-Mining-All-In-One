package scripts.framework;

/**
 * 
 * A Task that holds up to two child Tasks. This task
 * will never be executed and is used to control
 * logic flow.
 * 
 * @author Exia
 *
 */
public abstract class BranchTask extends TreeTask {
	/**
	 * Always do nothing in this function, and do 
	 * not allow it to be overridden.
	 */
	@Override
	public final void execute(){}
	
	/**
	 * This can NEVER be a leaf.
	 * @return false
	 */
	@Override
	public final boolean isLeaf(){
		return false;
	}
}