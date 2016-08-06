package scripts.framework;

import com.runemate.game.api.script.framework.task.Task;

/**
 * 
 * A Task that holds up to two child Tasks that will be
 * executed when the validator either succeeds or fails.
 * 
 * @author Exia
 *
 */
public abstract class TreeTask extends Task {
	public abstract TreeTask successTask();
	public abstract TreeTask failureTask();
}
