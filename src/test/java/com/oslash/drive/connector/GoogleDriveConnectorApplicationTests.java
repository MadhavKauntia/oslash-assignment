package com.oslash.drive.connector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;

@SpringBootTest
class GoogleDriveConnectorApplicationTests {

	/**
	 * google.api.key=
	 * google.drive.folder.id=
	 * output.folder=
	 * max.concurrency.batch.size=10
	 * periodic.check.time.in.mins=5
	 * events.threshold=10
	 */

	@BeforeAll
	public static void beforeAll() {
		System.setProperty("output.folder", Paths.get("src", "test", "resources").toFile().getAbsolutePath());
	}

	@Test
	void contextLoads() {
	}

}
