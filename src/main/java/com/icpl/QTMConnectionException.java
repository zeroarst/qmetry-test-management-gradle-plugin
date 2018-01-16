package com.icpl;

public class QTMConnectionException extends Exception
{
	public QTMConnectionException(String message)
	{
		super(message);
	}

	public QTMConnectionException()
	{
		super("");
	}
}