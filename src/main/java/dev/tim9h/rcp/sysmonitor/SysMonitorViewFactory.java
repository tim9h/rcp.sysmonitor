package dev.tim9h.rcp.sysmonitor;

import java.util.Map;

import com.google.inject.Inject;

import dev.tim9h.rcp.spi.CCard;
import dev.tim9h.rcp.spi.CCardFactory;

public class SysMonitorViewFactory implements CCardFactory {

	public static final String SETTING_NETWORK_IF = "sysmonitor.networkif";

	@Inject
	private SysMonitorView view;

	@Override
	public String getId() {
		return "sysmonitor";
	}

	@Override
	public CCard createCCard() {
		return view;
	}

	@Override
	public Map<String, String> getSettingsContributions() {
		return Map.of(SETTING_NETWORK_IF, "0");
	}

}
