package dev.tim9h.rcp.sysmonitor.service;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import dev.tim9h.rcp.logging.InjectLogger;

public class NvidiaService implements GpuMonitorService {

	@InjectLogger
	private Logger logger;

	@Override
	public Gpu getGpu() {
		try {
			var p = new ProcessBuilder("nvidia-smi", "--format=csv,noheader,nounits", "--query-gpu=utilization.gpu")
					.start();
			var stdout = IOUtils.toString(p.getInputStream(), Charset.defaultCharset()).trim();
			return new Gpu(Integer.parseInt(stdout));
		} catch (IOException e) {
			logger.error("Unable to read GPU utilization", e);
			return new Gpu(-1);
		}
	}

}
