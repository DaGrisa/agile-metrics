# Agile Metrics

[![Build Status](https://travis-ci.org/DaGrisa/agile-metrics.svg?branch=master)](https://travis-ci.org/DaGrisa/agile-metrics)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=bugs)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=code_smells)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=coverage)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Duplicated LoC Density](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![NCLOC](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=ncloc)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Sqale Rating](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=sqale_rating)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Alert Status](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=alert_status)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=reliability_rating)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=security_rating)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Sqale Index](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=sqale_index)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=at.grisa.agile-metrics%3Aagile-metrics&metric=vulnerabilities)](https://sonarcloud.io/dashboard/index/at.grisa.agile-metrics%3Aagile-metrics)

## Overview

Agile Metrics is a collector for software development process KPI data.
It collects measurements from [Producers](#producer), creates metrics and sends them to [Consumers](#consumer).

![Agile Metrics Overview](overview.png)

## <a href="producer"></a>Producer

A producer is a data source that offers measurement data.

### BitBucket Server

#### Authentication Properties

- `producer.bitbucketserver.baseUrl`
- `producer.bitbucketserver.username`
- `producer.bitbucketserver.password`

#### Metrics

- Daily Commits
    - per Author
    - per Project
    - per Repository

### JIRA Software Server

#### Authentication Properties

- `producer.jirasoftware.baseUrl`
- `producer.jirasoftware.username`
- `producer.jirasoftware.password`

#### Metrics

- Issue Volume
- Cumulative Flow
- Estimated Story Points
    - Completed
    - Not Completed
- Lead Time
- Bug Rate
- Recidivism
    - `producer.jirasoftware.workflow` needs to be defined (comma separated list)
- Acceptance Criteria Volatility
    - `producer.jirasoftware.acceptanceCriteriaFieldName` needs to be defined
- Velocity
- Labels

### SonarQube

#### Authentication Properties

- `producer.sonarqube.baseUrl`
- `producer.sonarqube.username`
- `producer.sonarqube.password`

#### Metrics

All SonarQube metrics defined as comma separated list of keys in `producer.sonarqube.metrics`. A list of all Metrics can be found at: [SonarQube Documentation](https://docs.sonarqube.org/display/SONAR/Metric+Definitions).

## <a href="consumer"></a>Consumer

A consumer is a data sink that takes the metrics data to provide further processing, for example visualization.

### ElasticSearch

#### Authentication Properties

- `consumer.elasicsearch.baseUrl`

## Getting Started

To get started set all the authentication properties of the systems you would like to use in the `application.properties` file.
The server is started with the command `java -jar agile-metrics-VERSION.jar`. 
Authentication properties are checked at startup, so you will see any errors in the logfile `logs/rollingfile.log`.
Metrics collecting runs every day at 00:15, you can change it with the property `cron.expression.daily` ([crontab pattern](http://www.manpagez.com/man/5/crontab/)).