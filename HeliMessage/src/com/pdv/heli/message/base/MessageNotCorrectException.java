package com.pdv.heli.message.base;

public class MessageNotCorrectExeption extends Exception {

	public MessageNotCorrectExeption(String pMessage) {
		super(pMessage);
	}

	public MessageNotCorrectExeption() {
		super();		
	}

	public MessageNotCorrectExeption(String pMessage, Throwable pCause,
			boolean pEnableSuppression, boolean pWritableStackTrace) {
		super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
		
	}

	public MessageNotCorrectExeption(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
		
	}

	public MessageNotCorrectExeption(Throwable pCause) {
		super(pCause);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
