package codetest.domain;

public class CannotReadFileRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public static final String WRONG_HEADER = "File header is not correct.";

	public CannotReadFileRuntimeException() {
	}

	public CannotReadFileRuntimeException(String s) {
		super(s);
	}

	public CannotReadFileRuntimeException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public CannotReadFileRuntimeException(Throwable throwable) {
		super(throwable);
	}
}
