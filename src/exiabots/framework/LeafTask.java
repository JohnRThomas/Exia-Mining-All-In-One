package exiabots.framework;
/**
 * This class provides a task that is meant to be the leaf of a task tree.
 * It has no children and is always executed if evaluated.
 * 
 * @author Exia
 *
 */
public abstract class LeafTask extends TreeTask{

	/**
	 * This is ALWAYS a leaf.
	 * 
	 * @return true
	 */
	@Override
	public final boolean isLeaf(){
		return true;
	}
	
	/**
	 * This will always be executed if evaluated.
	 * 
	 * @return true
	 */
	@Override
	public final boolean validate() {
		return true;
	}
	
	/**
	 * No children allowed.
	 * @return null
	 */
	@Override
	public final TreeTask successTask(){
		return null;
	}
	
	/**
	 * No children allowed.
	 * @return null
	 */
	@Override
	public final TreeTask failureTask(){
		return null;
	}
}
