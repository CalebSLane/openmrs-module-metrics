package org.openmrs.module.metrics;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;


public class TomcatJMXMetrics implements MeterBinder {
	private static final Logger log = LoggerFactory.getLogger(TomcatJMXMetrics.class);

    private final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    

    @Override
    public void bindTo(MeterRegistry registry) {
        registerSessionMetrics(registry, "/openmrs", "localhost");
        registerThreadPoolMetrics(registry, "http-nio-8080");
    }

    private void registerSessionMetrics(MeterRegistry registry, String contextPath, String host) {
        try {
            ObjectName managerName = new ObjectName(
                    String.format("Catalina:type=Manager,host=%s,context=%s", contextPath, host));

            Gauge.builder("tomcat.sessions.active.current", () -> getAttribute(managerName, "activeSessions"))
                    .description("Current active sessions")
                    .register(registry);

            Gauge.builder("tomcat.sessions.expired", () -> getAttribute(managerName, "expiredSessions"))
                    .description("Total expired sessions")
                    .register(registry);

            Gauge.builder("tomcat.sessions.rejected", () -> getAttribute(managerName, "rejectedSessions"))
                    .description("Sessions rejected due to limits")
                    .register(registry);

            Gauge.builder("tomcat.sessions.created", () -> getAttribute(managerName, "sessionCounter"))
                    .description("Total created sessions")
                    .register(registry);

            Gauge.builder("tomcat.sessions.active.max", () -> getAttribute(managerName, "maxActive"))
                    .description("Max active sessions")
                    .register(registry);

            Gauge.builder("tomcat.sessions.alive.max", () -> getAttribute(managerName, "sessionMaxAliveTime"))
                    .description("max session alive time")
                    .register(registry);

        } catch (Exception e) {
            log.error("Error registering session metrics", e);
        }
    }

    private void registerThreadPoolMetrics(MeterRegistry registry, String connectorName) {
        try {
            ObjectName threadPoolName = new ObjectName(
                    String.format("Catalina:type=ThreadPool,name=\"%s\"", connectorName));

            Gauge.builder("tomcat.threads.current", () -> getAttribute(threadPoolName, "currentThreadCount"))
                    .description("Current thread count")
                    .register(registry);

            Gauge.builder("tomcat.threads.busy", () -> getAttribute(threadPoolName, "currentThreadsBusy"))
                    .description("Currently busy threads")
                    .register(registry);

        } catch (Exception e) {
            log.error("Error registering thread pool metrics", e);
        }
    }

    private double getAttribute(ObjectName name, String attribute) {
        try {
            Object value = mbeanServer.getAttribute(name, attribute);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}

