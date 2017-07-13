/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

/**
 * @author LLT
 *
 */
public interface IRandomness {

	/**
	 * @return
	 */
	public float randomFloat();

	/**
	 * @return
	 */
	public Boolean randomBoolean();

	/**
	 * @param bytes
	 */
	public void randomBytes(byte[] bytes);

	/**
	 * @param i
	 * @return
	 */
	public int randomInt(int i);

	/**
	 * @return
	 */
	public Double randomDouble();

	/**
	 * @return
	 */
	public int randomInt();

	/**
	 * @return
	 */
	public Long randomLong();

	/**
	 * @return
	 */
	public Short randomShort();

	/**
	 * @param stringMaxLength
	 * @return
	 */
	public String randomAlphabetic(int stringMaxLength);

	/**
	 * @param constValues
	 * @return
	 */
	public Enum<?> randomEnum(Object[] constValues);

	/**
	 * @return
	 */
	public char randomChar();

	/**
	 * @param trueProb_
	 * @param falseProb_
	 * @return
	 */
	public boolean randomBoolFromDistribution(double trueProb_, double falseProb_);

}
