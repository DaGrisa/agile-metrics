package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.entity.Metric;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@Component
public class MetricErrorHandler {
    private static final Logger log = LogManager.getLogger(MetricQueue.class);

    public static final String ERROR_DIR = MetricQueue.QUEUE_DIR + File.separator + "error";

    @Autowired
    ObjectMapper mapper;

    public MetricErrorHandler() {
        MetricQueue.createDirectory(ERROR_DIR);
    }

    public void saveErrorMetrics(Collection<Metric> metrics) {
        for (Metric metric : metrics) {
            saveErrorMetric(metric);
        }
    }

    public void saveErrorMetric(Metric metric) {
        log.debug("saving error metric " + metric);

        String filepath = ERROR_DIR + File.separator + System.currentTimeMillis() + "_" + Math.random() * 1000 + ".json";

        try {
            mapper.writeValue(new File(filepath), metric);
        } catch (IOException e) {
            log.error("error when trying to save metric to file " + filepath, e);
        }
    }
}
