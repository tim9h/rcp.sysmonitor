package dev.tim9h.rcp.sysmonitor.service;

import com.google.inject.Inject;

import dev.tim9h.rcp.settings.Settings;
import dev.tim9h.rcp.sysmonitor.SysMonitorViewFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.HardwareAbstractionLayer;

public class OshiService implements SysMonitorService {

	@Inject
	private Settings settings;

	private HardwareAbstractionLayer hal;

	private long[] prevTicks;

	private long bytesSent;

	private long bytesRecv;

	public OshiService() {
		hal = new SystemInfo().getHardware();
		prevTicks = new long[TickType.values().length];
	}

	@Override
	public Memory getMemory() {
		var total = hal.getMemory().getTotal();
		var used = hal.getMemory().getTotal() - hal.getMemory().getAvailable();
		var percent = (int) (used * 100 / total);
		return new Memory(total, used, percent);
	}

	@Override
	public Cpu getCpu() {
		var cpuLoad = (int) (hal.getProcessor().getSystemCpuLoadBetweenTicks(prevTicks) * 100);
		prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
		return new Cpu(0, cpuLoad);
	}

	@Override
	public Traffic getNetworkTraffic() {
		var i = hal.getNetworkIFs().get(settings.getInt(SysMonitorViewFactory.SETTING_NETWORK_IF).intValue());
		i.updateAttributes();
		var traffic = new Traffic(i.getBytesSent() - bytesSent, i.getBytesRecv() - bytesRecv);
		this.bytesSent = i.getBytesSent();
		this.bytesRecv = i.getBytesRecv();
		return traffic;
	}

}
