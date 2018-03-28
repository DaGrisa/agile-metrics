package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.entity.Measurement;
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
public class MeasurementQueue {
    private final static Logger log = LogManager.getLogger(MeasurementQueue.class);

    private final String QUEUE_DIR = "queuedFiles";
    private final String ERROR_DIR = QUEUE_DIR + "/error";

    public MeasurementQueue() {
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

    public void enqueueMesurements(Collection<Measurement> measurements) {
        for (Measurement measurement : measurements) {
            enqueueMesurement(measurement);
        }
    }

    public void enqueueMesurement(Measurement measurement) {
        String filepath = QUEUE_DIR + "/" + System.currentTimeMillis() + "_" + Math.random() * 1000 + ".json";
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File(filepath), measurement);
        } catch (IOException e) {
            log.error("error when trying to save measurement to file " + filepath, e);
        }
    }

    public Measurement dequeueMeasurement() {
        File nextMeasurementFile = getNextMeasurementFile();

        if (nextMeasurementFile != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Measurement measurement = null;

            try {
                measurement = objectMapper.readValue(nextMeasurementFile, Measurement.class);
            } catch (IOException e) {
                log.error("could not read json data from file " + nextMeasurementFile.getName(), e);
                moveErrorMeasurementFile(nextMeasurementFile, e);
            }

            deleteMeasurementFile(nextMeasurementFile);

            return measurement;
        }

        return null;
    }

    private File getNextMeasurementFile() {
        File directory = new File(QUEUE_DIR);
        File[] files = directory.listFiles();
        Arrays.sort(files);

        File nextMeasurementFile = null;

        for (File file : files) {
            if (!file.isDirectory()) {
                nextMeasurementFile = file;
                break;
            }
        }

        return nextMeasurementFile;
    }

    private void moveErrorMeasurementFile(File nextMeasurementFile, IOException e) {
        File nextMeasurementFileError = new File(ERROR_DIR + nextMeasurementFile.getName());
        try {
            Files.move(nextMeasurementFile.toPath(), nextMeasurementFileError.toPath(), REPLACE_EXISTING);
        } catch (IOException e1) {
            log.error("unable to move file to error folder", e);
        }
    }

    private void deleteMeasurementFile(File nextMeasurementFile) {
        try {
            Files.delete(nextMeasurementFile.toPath());
        } catch (IOException e) {
            log.error("unable to delete file " + nextMeasurementFile.getName(), e);
        }
    }
}
