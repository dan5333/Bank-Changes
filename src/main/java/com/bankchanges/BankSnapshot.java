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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

/**
 * An immutable snapshot of a bank's contents, mapping item id to total quantity.
 */
@Getter
public final class BankSnapshot
{
	private final Map<Integer, Integer> items;

	public BankSnapshot(Map<Integer, Integer> items)
	{
		this.items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
	}

	/**
	 * Builds a snapshot from a bank {@link ItemContainer}. Quantities of identical
	 * item ids are summed, and empty (id <= 0) slots are skipped.
	 */
	public static BankSnapshot fromContainer(ItemContainer container)
	{
		Map<Integer, Integer> items = new LinkedHashMap<>();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				int id = item.getId();
				int qty = item.getQuantity();
				if (id <= 0 || qty <= 0)
				{
					continue;
				}
				items.merge(id, qty, Integer::sum);
			}
		}
		return new BankSnapshot(items);
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}
}
