package exiabots.newmining;

import com.runemate.game.api.script.framework.tree.TreeTask;

public class SwapTask extends TreeTask{
	private TreeTask task;
	
	public void setTask(TreeTask task) {
		this.task = task;
	}

	@Override
	public void execute() {
		task.execute();
	}

	@Override
	public TreeTask failureTask() {
		return task.failureTask();
	}

	@Override
	public boolean isLeaf() {
		return task.isLeaf();
	}

	@Override
	public TreeTask successTask() {
		return task.successTask();
	}

	@Override
	public boolean validate() {
		return task.validate();
	}
}
