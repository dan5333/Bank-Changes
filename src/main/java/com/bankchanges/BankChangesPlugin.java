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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Bank Changes",
	description = "Shows how your bank contents have changed since you last opened it",
	tags = {"bank", "changes", "diff", "snapshot", "inventory"}
)
public class BankChangesPlugin extends Plugin
{
	private static final String SNAPSHOT_KEY = "snapshot";
	private static final Type SNAPSHOT_TYPE = new TypeToken<Map<Integer, Integer>>()
	{
	}.getType();

	@Inject
	private BankChangesConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BankChangesOverlay overlay;

	@Inject
	private Gson gson;

	private BankSnapshot previousSnapshot;
	private boolean bankJustOpened;
	private volatile List<BankChange> lastChanges = Collections.emptyList();

	@Override
	protected void startUp()
	{
		previousSnapshot = loadSnapshot();
		lastChanges = Collections.emptyList();
		bankJustOpened = false;
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		lastChanges = Collections.emptyList();
		bankJustOpened = false;
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN)
		{
			// The bank container is (re)populated right after the interface loads;
			// the next ItemContainerChanged for the bank is its "on open" state.
			bankJustOpened = true;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.BANK || !bankJustOpened)
		{
			return;
		}

		bankJustOpened = false;

		ItemContainer container = event.getItemContainer();
		BankSnapshot current = BankSnapshot.fromContainer(container);
		if (current.isEmpty())
		{
			return;
		}

		if (previousSnapshot != null)
		{
			lastChanges = computeChanges(previousSnapshot, current);
			log.debug("Bank changes detected: {} item(s) changed", lastChanges.size());
		}

		previousSnapshot = current;
		saveSnapshot(current);
	}

	private List<BankChange> computeChanges(BankSnapshot before, BankSnapshot after)
	{
		int threshold = Math.max(1, config.quantityThreshold());

		Set<Integer> ids = new LinkedHashSet<>();
		ids.addAll(before.getItems().keySet());
		ids.addAll(after.getItems().keySet());

		List<BankChange> changes = new ArrayList<>();
		for (int id : ids)
		{
			int oldQty = before.getItems().getOrDefault(id, 0);
			int newQty = after.getItems().getOrDefault(id, 0);
			int delta = newQty - oldQty;
			if (delta == 0 || Math.abs(delta) < threshold)
			{
				continue;
			}

			String name = itemManager.getItemComposition(id).getName();
			changes.add(new BankChange(id, name, oldQty, newQty));
		}

		changes.sort((a, b) -> Integer.compare(Math.abs(b.getDelta()), Math.abs(a.getDelta())));
		return changes;
	}

	private BankSnapshot loadSnapshot()
	{
		String json = configManager.getConfiguration(BankChangesConfig.GROUP, SNAPSHOT_KEY);
		if (json == null || json.isEmpty())
		{
			return null;
		}

		try
		{
			Map<Integer, Integer> items = gson.fromJson(json, SNAPSHOT_TYPE);
			return items == null ? null : new BankSnapshot(items);
		}
		catch (RuntimeException e)
		{
			log.debug("Failed to parse stored bank snapshot", e);
			return null;
		}
	}

	private void saveSnapshot(BankSnapshot snapshot)
	{
		configManager.setConfiguration(BankChangesConfig.GROUP, SNAPSHOT_KEY,
			gson.toJson(new HashMap<>(snapshot.getItems()), SNAPSHOT_TYPE));
	}

	List<BankChange> getLastChanges()
	{
		return lastChanges;
	}

	@Provides
	BankChangesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankChangesConfig.class);
	}
}
