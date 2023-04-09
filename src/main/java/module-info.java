module rcp.sysmonitor {
	exports dev.tim9h.rcp.sysmonitor;

	requires transitive rcp.api;
	requires com.google.guice;
	requires org.apache.logging.log4j;
	requires transitive javafx.controls;
	requires com.github.oshi;
	requires org.apache.commons.io;
}