package exiabots.newmining;

import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;

public class WaitTask extends LeafTask {

	@Override
	public void execute() {
		Paint.status = "Waiting";
		Execution.delay(100, 200);
	}
}
