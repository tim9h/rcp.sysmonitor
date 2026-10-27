package dev.tim9h.rcp.sysmonitor.service;

import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import dev.tim9h.rcp.settings.Settings;
import dev.tim9h.rcp.sysmonitor.SysMonitorView;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class OshiService implements SysMonitorService {

	private static final long MAX_SAMPLE_GAP_NANOS = TimeUnit.SECONDS.toNanos(5);

	@Inject
	private Settings settings;

	private final HardwareAbstractionLayer hal;

	private long[] prevTicks;

	private long bytesSent;

	private long bytesRecv;

	private long lastCpuSampleNanos;

	private long lastNetworkSampleNanos;

	private int lastNetworkInterface = -1;

	public OshiService() {
		hal = new SystemInfo().getHardware();
		prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
		lastCpuSampleNanos = System.nanoTime();
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
		var now = System.nanoTime();
		if (now - lastCpuSampleNanos > MAX_SAMPLE_GAP_NANOS) {
			prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
			lastCpuSampleNanos = now;
			return new Cpu(0, -1);
		}

		var cpuLoad = (int) (hal.getProcessor().getSystemCpuLoadBetweenTicks(prevTicks) * 100);
		prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
		lastCpuSampleNanos = now;
		return new Cpu(0, cpuLoad);
	}

	@Override
	public Traffic getNetworkTraffic() {
		var interfaceIndex = settings.getInt(SysMonitorView.SETTING_NETWORK_IF).intValue();
		var i = hal.getNetworkIFs().get(interfaceIndex);
		i.updateAttributes();
		var now = System.nanoTime();
		var currentSent = i.getBytesSent();
		var currentRecv = i.getBytesRecv();
		var elapsed = now - lastNetworkSampleNanos;
		var validSample = lastNetworkSampleNanos != 0 && elapsed <= MAX_SAMPLE_GAP_NANOS
				&& interfaceIndex == lastNetworkInterface && currentSent >= bytesSent && currentRecv >= bytesRecv;
		var traffic = validSample ? new Traffic(currentSent - bytesSent, currentRecv - bytesRecv)
				: new Traffic(-1, -1);
		this.bytesSent = currentSent;
		this.bytesRecv = currentRecv;
		lastNetworkSampleNanos = now;
		lastNetworkInterface = interfaceIndex;
		return traffic;
	}

}
