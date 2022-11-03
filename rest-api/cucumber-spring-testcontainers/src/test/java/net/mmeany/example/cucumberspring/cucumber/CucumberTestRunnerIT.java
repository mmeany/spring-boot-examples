package net.mmeany.example.cucumberspring.cucumber;


import org.junit.Test;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// Remember to look at:::::
// One of these fixed this test::
//    testImplementation "org.junit.platform:junit-platform-suite:${junitPlatformVersion}"
//    testImplementation "io.cucumber:cucumber-junit-platform-engine:${cucumberVersion}"
//    testImplementation "io.cucumber:cucumber-java:${cucumberVersion}"
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "net.mmeany.example.cucumberspring.cucumber.glue"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "usage"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "html:build/cucumber-reports.html"
)
public class CucumberTestRunnerIT {
    @Test
    public void cucumberOrBust() {
        assertThat(System.currentTimeMillis() > 0, is(true));
    }
}
