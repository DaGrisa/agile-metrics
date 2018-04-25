package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.entity.Metric;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class MetricQueue {
    private final static Logger log = LogManager.getLogger(MetricQueue.class);

    private final String QUEUE_DIR = "queuedFiles";
    private final String ERROR_DIR = QUEUE_DIR + "/error";

    public MetricQueue() {
        createDirectory(QUEUE_DIR);
        createDirectory(ERROR_DIR);
    }

    private void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            try {
                directory.mkdir();
            } catch (SecurityException e) {
                log.error("unable to create directory " + directoryPath, e);
            }
        }
    }

    public void enqueueMetric(Collection<Metric> metrics) {
        for (Metric metric : metrics) {
            enqueueMetric(metric);
        }
    }

    public void enqueueMetric(Metric metric) {
        String filepath = QUEUE_DIR + "/" + System.currentTimeMillis() + "_" + Math.random() * 1000 + ".json";
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File(filepath), metric);
        } catch (IOException e) {
            log.error("error when trying to save metric to file " + filepath, e);
        }
    }

    public Metric dequeueMetric() {
        File nextMetricFile = getNextMetricFile();

        if (nextMetricFile != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Metric metric = null;

            try {
                metric = objectMapper.readValue(nextMetricFile, Metric.class);
            } catch (IOException e) {
                log.error("could not read json data from file " + nextMetricFile.getName(), e);
                moveErrorMetricFile(nextMetricFile, e);
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

    private void moveErrorMetricFile(File nextMetricFile, IOException e) {
        File nextMetricFileError = new File(ERROR_DIR + nextMetricFile.getName());
        try {
            Files.move(nextMetricFile.toPath(), nextMetricFileError.toPath(), REPLACE_EXISTING);
        } catch (IOException e1) {
            log.error("unable to move file to error folder", e);
        }
    }

    private void deleteMetricFile(File nextMetricFile) {
        try {
            Files.delete(nextMetricFile.toPath());
        } catch (IOException e) {
            log.error("unable to delete file " + nextMetricFile.getName(), e);
        }
    }
}
