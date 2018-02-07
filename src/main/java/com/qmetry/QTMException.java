package com.qmetry;

public class QTMException extends Exception
{
	public QTMException(String message)
	{
		super(message);
	}

	public QTMException()
	{
		super("");
	}
}