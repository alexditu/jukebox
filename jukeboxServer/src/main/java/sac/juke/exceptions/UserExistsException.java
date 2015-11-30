package sac.juke.exceptions;

public class UserExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8509883593003720661L;
	
	public UserExistsException() { super(); }
	public UserExistsException(String msg) { super(msg); }
	public UserExistsException(String message, Throwable cause) { super(message, cause); }
	public UserExistsException(Throwable cause) { super(cause); }

}
