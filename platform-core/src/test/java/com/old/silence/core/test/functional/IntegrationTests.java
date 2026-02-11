package com.old.silence.core.test.functional;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.old.silence.core.test.UnitTests;

/**
 * @author moryzang
 */
@ActiveProfiles({"integration"})
@TestPropertySource(
        properties = {"spring.liquibase.drop-first=true"}
)
public class IntegrationTests extends UnitTests {
    public IntegrationTests() {
    }
}
