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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class BankChangesOverlay extends OverlayPanel
{
	private final BankChangesPlugin plugin;
	private final BankChangesConfig config;

	@Inject
	private BankChangesOverlay(BankChangesPlugin plugin, BankChangesConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlay())
		{
			return null;
		}

		List<BankChange> changes = plugin.getLastChanges();
		if (changes.isEmpty())
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Bank Changes")
			.color(Color.WHITE)
			.build());

		int maxRows = Math.max(1, config.maxRows());
		int shown = Math.min(maxRows, changes.size());

		for (int i = 0; i < shown; i++)
		{
			BankChange change = changes.get(i);
			int delta = change.getDelta();
			Color color = delta >= 0 ? Color.GREEN : Color.RED;
			String text = (delta > 0 ? "+" : "") + delta;

			panelComponent.getChildren().add(LineComponent.builder()
				.left(change.getItemName())
				.right(text)
				.rightColor(color)
				.build());
		}

		int remaining = changes.size() - shown;
		if (remaining > 0)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("+" + remaining + " more...")
				.leftColor(Color.LIGHT_GRAY)
				.build());
		}

		return super.render(graphics);
	}
}
