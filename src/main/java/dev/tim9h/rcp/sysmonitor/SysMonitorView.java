package dev.tim9h.rcp.sysmonitor;

import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import dev.tim9h.rcp.logging.InjectLogger;
import dev.tim9h.rcp.spi.CCard;
import dev.tim9h.rcp.spi.Gravity;
import dev.tim9h.rcp.spi.Position;
import dev.tim9h.rcp.sysmonitor.service.GpuMonitorService;
import dev.tim9h.rcp.sysmonitor.service.GpuMonitorService.Gpu;
import dev.tim9h.rcp.sysmonitor.service.SysMonitorService;
import dev.tim9h.rcp.sysmonitor.service.SysMonitorService.Cpu;
import dev.tim9h.rcp.sysmonitor.service.SysMonitorService.Memory;
import dev.tim9h.rcp.sysmonitor.service.SysMonitorService.Traffic;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import oshi.util.FormatUtil;

public class SysMonitorView implements CCard {

	private static final String CSS_CLASS_SECONDARY = "secondary";

	@InjectLogger
	private Logger logger;

	@Inject
	private SysMonitorService service;

	@Inject
	private GpuMonitorService gpuService;

	private Label lblCpuValue;

	private Label lblMemValue;

	private Label lblUploadValue;

	private Label lblDownloadValue;

	private Label lblGpuValue;

	@Override
	public String getName() {
		return "System Monitor";
	}

	@Override
	public Optional<Node> getNode() throws IOException {
		var pane = new GridPane();
		pane.getStyleClass().add("ccCard");
		var col = new ColumnConstraints();
		col.setPercentWidth(23.3);
		col.setHgrow(Priority.ALWAYS);
		col.setHalignment(HPos.LEFT);
		var narrow = new ColumnConstraints();
		narrow.setPercentWidth(15);
		narrow.setHgrow(Priority.ALWAYS);
		narrow.setHalignment(HPos.LEFT);
		pane.getColumnConstraints().addAll(narrow, narrow, col, col, col);

		var lblCpu = new Label("CPU");
		lblCpu.getStyleClass().add(CSS_CLASS_SECONDARY);
		var lblGpu = new Label("GPU");
		lblGpu.getStyleClass().add(CSS_CLASS_SECONDARY);
		var lblMem = new Label("Memory");
		lblMem.getStyleClass().add(CSS_CLASS_SECONDARY);
		var lblUp = new Label("Upload 🠕");
		lblUp.getStyleClass().add(CSS_CLASS_SECONDARY);
		var lblDown = new Label("Download 🠗");
		lblDown.getStyleClass().add(CSS_CLASS_SECONDARY);

		lblCpuValue = new Label("??%");
		lblGpuValue = new Label("??%");
		lblMemValue = new Label("?.? GB (??%)");
		lblUploadValue = new Label("?? Kbps");
		lblDownloadValue = new Label("?? Kbps");

		pane.add(lblCpu, 0, 0);
		pane.add(lblGpu, 1, 0);
		pane.add(lblMem, 2, 0);
		pane.add(lblUp, 3, 0);
		pane.add(lblDown, 4, 0);

		pane.add(lblCpuValue, 0, 1);
		pane.add(lblGpuValue, 1, 1);
		pane.add(lblMemValue, 2, 1);
		pane.add(lblUploadValue, 3, 1);
		pane.add(lblDownloadValue, 4, 1);

		var timer = new Timer("sysStatsUpdater", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				CompletableFuture.runAsync(() -> updateStats(service.getMemory(), service.getCpu(),
						service.getNetworkTraffic(), gpuService.getGpu()));
			}
		}, 0, 1000);

		return Optional.of(pane);
	}

	private void updateStats(Memory memory, Cpu processor, Traffic traffic, Gpu gpu) {
		Platform.runLater(() -> {
			lblMemValue.setText(String.format("%s (%d%%)", FormatUtil.formatBytes(memory.used()),
					Integer.valueOf(memory.percent())));
			lblCpuValue.setText(String.format("%d%%", Integer.valueOf(processor.load())));
			lblUploadValue.setText(FormatUtils.formatBitsPerSecond(traffic.upBytes()));
			lblDownloadValue.setText(FormatUtils.formatBitsPerSecond(traffic.downBytes()));
			lblGpuValue.setText(String.format("%d%%", Integer.valueOf(gpu.utilization())));
		});
	}

	@Override
	public Gravity getGravity() {
		return new Gravity(Position.MIDDLE);
	}

}
