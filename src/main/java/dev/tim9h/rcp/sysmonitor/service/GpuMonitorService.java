package dev.tim9h.rcp.sysmonitor.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(NvidiaService.class)
public interface GpuMonitorService {

	public Gpu getGpu();

	public record Gpu(int utilization) {
	}

}
