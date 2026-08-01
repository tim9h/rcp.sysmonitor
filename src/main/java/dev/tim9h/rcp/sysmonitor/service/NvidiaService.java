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
		Process process = null;
		try {
			process = new ProcessBuilder("nvidia-smi", "--format=csv,noheader,nounits", "--query-gpu=utilization.gpu")
					.redirectErrorStream(true)
					.start();
			try (var stdout = process.getInputStream()) {
				var output = IOUtils.toString(stdout, Charset.defaultCharset()).trim();
				var exitCode = process.waitFor();
				if (exitCode != 0) {
					logger.warn("nvidia-smi exited with code {}: {}", exitCode, output);
					return new Gpu(-1);
				}
				return new Gpu(Integer.parseInt(output));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Interrupted while reading GPU utilization", e);
			return new Gpu(-1);
		} catch (IOException e) {
			logger.error("Unable to read GPU utilization", e);
			return new Gpu(-1);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}

}
