/*
 * Copyright (c) 2026, Bank Changes contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 */
package com.bankchanges;

import lombok.Value;

/**
 * A single item-level difference between two {@link BankSnapshot}s.
 */
@Value
public class BankChange
{
	int itemId;
	String itemName;
	int oldQty;
	int newQty;

	public int getDelta()
	{
		return newQty - oldQty;
	}

	public boolean isIncrease()
	{
		return getDelta() > 0;
	}
}
