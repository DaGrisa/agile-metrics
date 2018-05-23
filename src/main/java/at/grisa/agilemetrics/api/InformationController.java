package at.grisa.agilemetrics.api;

import at.grisa.agilemetrics.cron.CronObserver;
import at.grisa.agilemetrics.persistence.IStatisticRepository;
import at.grisa.agilemetrics.persistence.entity.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class InformationController {
    @Autowired
    private CronObserver cronObserver;

    @Autowired
    private IStatisticRepository statisticRepository;

    @RequestMapping(path = "/", method = GET)
    public Information getInformation() {
        Statistic lastRun = statisticRepository.findFirstByOrderByLastRunDesc();
        ZonedDateTime lastRunDateTime = lastRun == null ? null : lastRun.getLastRun();
        Integer processedMetricsLastRun = lastRun == null ? 0 : lastRun.getMetricsCount();
        return new Information(cronObserver.getConsumersAsString(), cronObserver.getProducersAsString(), processedMetricsLastRun, lastRunDateTime);
    }
}
