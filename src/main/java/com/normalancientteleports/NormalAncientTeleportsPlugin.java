package com.normalancientteleports;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Normal Ancient Teleports",
		description = "Renames Ancient Teleports to their actual locations."
)
public class NormalAncientTeleportsPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private NormalAncientTeleportsConfig config;

	public static String replaceTeleportNames (String original) {
		return original.replace("Senntisten", "Exam Centre")
				.replace("Paddewwa", "Edgeville Dungeon")
				.replace("Kharyrll", "Canifis")
				.replace("Lassar", "Ice Mountain")
				.replace("Dareeyak", "Crazy Archaeologist")
				.replace("Carrallanger", "Graveyard of Shadows")
				.replace("Annakarl", "Demonic Ruins")
				.replace("Ghorrock", "Frozen Waste");
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event) {
		if (event.getScriptId() != ScriptID.MAGIC_SPELLBOOK_INITIALISESPELLS) {
			return;
		}
		int[] stack = client.getIntStack();
		int sz = client.getIntStackSize();
		int spellBookEnum = stack[sz - 12]; // eg 1982, 5285, 1983, 1984, 1985
		clientThread.invokeLater(() -> renameSpells(spellBookEnum));

	}

	private void renameSpells(int spellBookEnum) {
		EnumComposition spellbook = client.getEnum(spellBookEnum);
		for (int i = 0; i < spellbook.size(); ++i) {
			int spellObjId = spellbook.getIntValue(i);
			ItemComposition spellObj = client.getItemDefinition(spellObjId);
			int spellComponent = spellObj.getIntValue(ParamID.SPELL_BUTTON);
			Widget w = client.getWidget(spellComponent);

			w.setName(
					replaceTeleportNames(w.getName()));
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e) {
		if(e.getGroupId() == 17 && config.replacePortalNexus()) {
			Widget widget = client.getWidget(17, 12);

			for(Widget w : widget.getChildren()) {
				w.setText(
						replaceTeleportNames(w.getText()));
			}
		}
	}


	@Provides
	NormalAncientTeleportsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NormalAncientTeleportsConfig.class);
	}
}
