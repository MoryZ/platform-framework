package com.old.silence.core.logging;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * @author moryzang
 */
class ProcessorMonitor {
    private static final List<String> OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList("com.ibm.lang.management.OperatingSystemMXBean", "com.sun.management.OperatingSystemMXBean");
    private static final double CPU_USAGE_THRESHOLD = 70.0;
    private static final ProcessorMonitor INSTANCE = new ProcessorMonitor();
    private final OperatingSystemMXBean operatingSystemBean =  ManagementFactory.getOperatingSystemMXBean();
    @Nullable
    private final Class<?> operatingSystemBeanClass;
    @Nullable
    private final Method systemCpuUsage;
    @Nullable
    private final Method processCpuUsage;
    private ScheduledExecutorService scheduler;
    private volatile boolean idle = true;

    ProcessorMonitor() {
        this.operatingSystemBeanClass = getFirstClassFound(OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        Method getCpuLoad = this.detectMethod("getCpuLoad");
        this.systemCpuUsage = getCpuLoad != null ? getCpuLoad : this.detectMethod("getSystemCpuLoad");
        this.processCpuUsage = this.detectMethod("getProcessCpuLoad");
    }

    @Nullable
    private static Class<?> getFirstClassFound(List<String> classNames) {
        Iterator iterator = classNames.iterator();

        while (iterator.hasNext()) {
            String className = (String) iterator.next();

            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {

            }
        }
        return null;
    }

    @Nullable
    private Method detectMethod(String name) {
        if (this.operatingSystemBeanClass == null) {
            return null;
        } else {
            try {
                this.operatingSystemBeanClass.cast(this.operatingSystemBean);
                return this.operatingSystemBeanClass.getDeclaredMethod(name);
            } catch (NoSuchMethodException |  SecurityException | ClassCastException e) {
                return null;
            }
        }
    }

    public static ProcessorMonitor getSharedInstance() {
        return INSTANCE;
    }

    public synchronized void start() {
        if (this.scheduler == null) {
            CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("ProcessorMonitor");
            threadFactory.setDaemon(true);
            this.scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
            this.scheduler.scheduleAtFixedRate(this::detectCpuStatus, 0L, 5L, TimeUnit.SECONDS);
        }
    }

    private void detectCpuStatus() {
        if (this.getProcessCpuUsage() > CPU_USAGE_THRESHOLD) {
            this.idle = false;
        } else {
            this.idle = !(this.getSystemCpuUsage() > CPU_USAGE_THRESHOLD);
        }
    }

    private double getProcessCpuUsage() {
        return this.invoke(this.processCpuUsage);
    }

    private double getSystemCpuUsage() {
        return this.invoke(this.systemCpuUsage);
    }

    private double invoke(@Nullable Method method) {
        try {
            return method != null ? (Double) method.invoke(this.operatingSystemBean) : Double.NaN;
        } catch (ReflectiveOperationException e) {
            return Double.NaN;
        }
    }

    public boolean isIdle() {
        return this.idle;
    }

    public synchronized void stop() {
        if (this.scheduler != null) {
            this.scheduler.shutdown();
            this.scheduler = null;
        }
    }
}
