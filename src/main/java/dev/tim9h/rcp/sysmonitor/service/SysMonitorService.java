package dev.tim9h.rcp.sysmonitor.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(OshiService.class)
public interface SysMonitorService {

	public Memory getMemory();

	public Cpu getCpu();

	public Traffic getNetworkTraffic();

	public record Memory(long total, long used, int percent) {
	}

	public record Cpu(long frequency, int load) {
	}

	public record Traffic(long upBytes, long downBytes) {
	}

}
