package scripts.mining;

import java.util.LinkedList;

public class ErrorHandler {

	private static LinkedList<Exception> errors = new LinkedList<Exception>();
	
	public static void add(Exception e) {
		errors.add(e);
	}

	public static void throwAll() {
		ErrorHandlerException finalException = new ErrorHandlerException();
		for(Exception e : errors){
			finalException.addSuppressed(e);
		}
		throw finalException;
	}
	
	public static boolean hasErrors() {
		return errors.size() > 0;
	}

	private static class ErrorHandlerException extends RuntimeException{
		private static final long serialVersionUID = -4575630592933381077L;

		public ErrorHandlerException(){
			super(ErrorHandler.errors.size() + " supressed error" + (ErrorHandler.errors.size() == 1 ? "" : "s") + " occured durring runtime please submit " + (ErrorHandler.errors.size() == 1 ? "it" : "them") + " now!");
		}
	}
}
