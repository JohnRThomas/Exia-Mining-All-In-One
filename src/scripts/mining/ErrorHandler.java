package scripts.mining;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

public class ErrorHandler {

	private static LinkedList<Exception> errors = new LinkedList<Exception>();
	
	public static void add(Exception e) {
		errors.add(e);
	}

	public static void throwAll(MiningStyle miner) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);		
		for(Exception e : errors){
			e.printStackTrace(pw);
			pw.println("----------------\n");
		}
		String errors = sw.toString();
		throw new ErrorHandlerException(miner, errors);
	}
	
	public static boolean hasErrors() {
		return errors.size() > 0;
	}

	private static class ErrorHandlerException extends RuntimeException{
		private static final long serialVersionUID = -4575630592933381077L;

		public ErrorHandlerException(MiningStyle miner, String errors){
			super(miner.getLocationName() + "(" + miner.getOre() + "): " +
		         ErrorHandler.errors.size() + " supressed error" + (ErrorHandler.errors.size() == 1 ? "" : "s") +
		         " occured durring runtime please submit " + (ErrorHandler.errors.size() == 1 ? "it" : "them") +
		         " now!\n" +
		         errors);
		}
	}
}
