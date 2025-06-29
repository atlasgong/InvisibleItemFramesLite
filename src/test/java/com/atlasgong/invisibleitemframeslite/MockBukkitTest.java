package com.atlasgong.invisibleitemframeslite;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class MockBukkitTest {
    @BeforeAll
    static void setSystemProperties() {
        System.setProperty("bstats.relocatecheck", "false");
    }

    @AfterAll
    static void unsetSystemProperties() {
        System.setProperty("bstats.relocatecheck", "true");
    }
}
