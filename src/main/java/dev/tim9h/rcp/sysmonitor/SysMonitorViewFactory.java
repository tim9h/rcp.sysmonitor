package dev.tim9h.rcp.sysmonitor;

import java.util.Map;

import com.google.inject.Inject;

import dev.tim9h.rcp.spi.Plugin;
import dev.tim9h.rcp.spi.PluginFactory;

public class SysMonitorViewFactory implements PluginFactory {

	public static final String SETTING_NETWORK_IF = "sysmonitor.networkif";

	@Inject
	private SysMonitorView view;

	@Override
	public String getId() {
		return "sysmonitor";
	}

	@Override
	public Plugin create() {
		return view;
	}

	@Override
	public Map<String, String> getSettingsContributions() {
		return Map.of(SETTING_NETWORK_IF, "0");
	}

}
