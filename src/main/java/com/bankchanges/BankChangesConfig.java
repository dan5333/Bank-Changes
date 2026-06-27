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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(BankChangesConfig.GROUP)
public interface BankChangesConfig extends Config
{
	String GROUP = "bank-changes";

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show overlay",
		description = "Show an overlay listing what changed in your bank since you last opened it",
		position = 0
	)
	default boolean showOverlay()
	{
		return true;
	}

	@Range(min = 1)
	@ConfigItem(
		keyName = "quantityThreshold",
		name = "Quantity threshold",
		description = "Only show changes whose absolute quantity difference is at least this value",
		position = 1
	)
	default int quantityThreshold()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "maxRows",
		name = "Max rows",
		description = "Maximum number of changed items to list in the overlay",
		position = 2
	)
	default int maxRows()
	{
		return 15;
	}
}
