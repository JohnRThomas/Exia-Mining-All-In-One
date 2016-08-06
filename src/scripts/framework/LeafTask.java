package scripts.framework;
/**
 * This class provides a task that is meant to be the leaf of a task tree.
 * It has no children and is always executed.
 * 
 * @author Exia
 *
 */
public abstract class LeafTask extends TreeTask{
	
	@Override
	public final boolean validate() {
		return true;
	}
	
	@Override
	public final TreeTask successTask(){
		return null;
	}
	
	@Override
	public final TreeTask failureTask(){
		return null;
	}
}
