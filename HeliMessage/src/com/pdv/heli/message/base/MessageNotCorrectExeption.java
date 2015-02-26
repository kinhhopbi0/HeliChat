package com.pdv.heli.message.base;

public class MessageNotCorrectExeption extends Exception {

	public MessageNotCorrectExeption(String pMessage) {
		super(pMessage);
	}

	public MessageNotCorrectExeption() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageNotCorrectExeption(String pMessage, Throwable pCause,
			boolean pEnableSuppression, boolean pWritableStackTrace) {
		super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public MessageNotCorrectExeption(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
		// TODO Auto-generated constructor stub
	}

	public MessageNotCorrectExeption(Throwable pCause) {
		super(pCause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
