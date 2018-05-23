package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.entity.Metric;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class MetricQueue {
    private static final Logger log = LogManager.getLogger(MetricQueue.class);

    private Integer metricsCounter;

    public static final String QUEUE_DIR = "queuedFiles";

    @Autowired
    ObjectMapper mapper;

    public MetricQueue() {
        createDirectory(QUEUE_DIR);
    }

    public static void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            try {
                log.info("creating directory " + directoryPath);
                directory.mkdir();
            } catch (SecurityException e) {
                log.error("unable to create directory " + directoryPath, e);
            }
        }
    }

    public void enqueueMetric(Metric metric) {
        log.debug("enqueuing metric " + metric);
        metricsCounter++;

        String filepath = QUEUE_DIR + File.separator + System.currentTimeMillis() + "_" + Math.random() * 1000 + ".json";

        try {
            mapper.writeValue(new File(filepath), metric);
        } catch (IOException e) {
            log.error("error when trying to save metric to file " + filepath, e);
        }
    }

    public Metric dequeueMetric() {
        File nextMetricFile = getNextMetricFile();

        if (nextMetricFile != null) {
            Metric metric = null;

            try {
                metric = mapper.readValue(nextMetricFile, Metric.class);
                log.debug("dequeued metric " + metric);
            } catch (IOException e) {
                log.error("could not read json data from file " + nextMetricFile.getName() + ", moving to error folder", e);
                moveErrorMetricFile(nextMetricFile);
            }

            deleteMetricFile(nextMetricFile);

            return metric;
        }

        return null;
    }

    private File getNextMetricFile() {
        File directory = new File(QUEUE_DIR);
        File[] files = directory.listFiles();
        Arrays.sort(files);

        File nextMetricFile = null;

        for (File file : files) {
            if (!file.isDirectory()) {
                nextMetricFile = file;
                break;
            }
        }

        return nextMetricFile;
    }

    private void moveErrorMetricFile(File nextMetricFile) {
        File nextMetricFileError = new File(".." + File.separator + MetricErrorHandler.ERROR_DIR + File.separator + nextMetricFile.getName());
        log.debug("new metric error file " + nextMetricFile.getName());
        try {
            Files.move(nextMetricFile.toPath(), nextMetricFileError.toPath(), REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("unable to move file to error folder", e);
        }
    }

    private void deleteMetricFile(File nextMetricFile) {
        log.debug("deleting " + nextMetricFile.getName());
        try {
            Files.delete(nextMetricFile.toPath());
        } catch (IOException e) {
            log.error("unable to delete file " + nextMetricFile.getName(), e);
        }
    }

    public void resetMetricsCounter() {
        metricsCounter = 0;
    }

    public Integer getMetricsCounter() {
        return metricsCounter;
    }
}
