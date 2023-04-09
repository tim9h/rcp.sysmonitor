package dev.tim9h.rcp.sysmonitor;

public class FormatUtils {

	private static final long KIBI = 1L << 10;

	private static final long MEBI = 1L << 20;

	private static final long GIBI = 1L << 30;

	private static final long TEBI = 1L << 40;

	private static final long PEBI = 1L << 50;

	private static final long EXBI = 1L << 60;

	private FormatUtils() {
		// hide implicit public constructor
	}

	public static String formatBitsPerSecond(long bytes) {
		long bits = bytes * 8;
		if (bits == 1L) { // bytes
			return String.format("%d byte", Long.valueOf(bits));
		} else if (bits < KIBI) { // bytes
			return String.format("%d bytes", Long.valueOf(bits));
		} else if (bits < MEBI) { // KiB
			return formatUnits(bits, KIBI, "Kbps");
		} else if (bits < GIBI) { // MiB
			return formatUnits(bits, MEBI, "Mbps");
		} else if (bits < TEBI) { // GiB
			return formatUnits(bits, GIBI, "Gbps");
		} else if (bits < PEBI) { // TiB
			return formatUnits(bits, TEBI, "Tbps");
		} else if (bits < EXBI) { // PiB
			return formatUnits(bits, PEBI, "Pbps");
		} else { // EiB
			return formatUnits(bits, EXBI, "Ebps");
		}
	}

	private static String formatUnits(long value, long prefix, String unit) {
		return String.format("%.1f %s", Double.valueOf((double) value / prefix), unit);
	}

}
