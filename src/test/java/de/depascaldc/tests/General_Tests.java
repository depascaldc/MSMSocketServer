package de.depascaldc.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.depascaldc.managing.socket.logger.Logger;

public class General_Tests {

	private Logger l;

	public General_Tests() {
		l = new Logger(true);
	}

	@DisplayName("General Tests")
	@Test
	void testLogger() {
		Assertions.assertAll(() -> {
			l.debug("Loggertest DEBUG");
			l.debug("Loggertest T_DEBUG", new RuntimeException("Tests__ Exception throwable..."));
		}, () -> {
			l.error("Loggertest ERROR");
			l.error("Loggertest T_ERROR", new RuntimeException("Tests__ Exception throwable..."));
		}, () -> {
			l.warn("Loggertest WARN");
			l.warn("Loggertest T_WARN", new RuntimeException("Tests__ Exception throwable..."));
		}, () -> {
			l.info("Loggertest INFO");
			l.info("Loggertest T_INFO", new RuntimeException("Tests__ Exception throwable..."));
		}, () -> {
			l.log("Loggertest LOG/INFO");
			l.log("Loggertest T_LOG/INFO", new RuntimeException("Tests__ Exception throwable..."));
		}, () -> {
			l.out(">> Loggertest OUT");
		});
	}

}
