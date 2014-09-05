package com.eray.springmvc.exception;

public class DaoException extends ErayException {

	private static final long serialVersionUID = 864429653671407479L;

	public DaoException() {
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
