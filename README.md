# Introduction

This project simply aims to calculate the number of days between two dates. It provides features for excluding days and days of week.

# Architecture

The project is composed of two modules:

* **days-calculator-core** which contains the core classes allowing the computation ;
* **days-calculator-cli** which is a CLI client.

# Usage

In order to use the project you will need the **days-calculator-cli** JAR as well as the **days-calculator-cli** JAR placed into a **lib** folder placed next to the CLI jar. Then, to execute, open a command line interface and type this command:

    java -jar days-calculator-cli-[version].jar [OPTIONS ...]

Where possible options are:

* **startDate=&lt;ISO DATE&gt;** : defines the start date at which the computation will start. The date must be given in the *YYYY-MM-DD* format.
* **endDate=&lt;ISO DATE&gt;** : defines the end date (included) at which the computation will end. The date must be given in the *YYYY-MM-DD* format.
* **excludedDays=&lt;list of ISO DATE or periods&gt;** : defines the days to exclude from the computation. The list must be comma separated.
* **excludedDaysOfWeeks=&lt;list of days&gt;** : defines the days of week to exclude from the computation. The list must be comma separated. Possible values are
  * SUNDAY
  * MONDAY
  * TUESDAY
  * WEDNESDAY
  * THURSDAY
  * FRIDAY
  * SATURDAY
* **file=&lt;FILE&gt;** : defines a file containing all desired configuration for the computation.

Periods are composed by two ISO date separated by the `->` token. For example: `2016-01-10->2016-01-20`.

## Examples

### Example 1

    java -jar days-calculator-cli-0.1.jar startDate=2016-01-01 endDate=2015-02-01

Will compute the number of days between 2016, January 1st and 2016, February 1st.

### Example 2

    java -jar days-calculator-cli-0.1.jar startDate=2016-01-01 endDate=2015-02-01 excludedDays=2016-01-20,2016-01-22,2016-01-25->2016-02-28

Will compute the number of days between 2016, January 1st and 2016, February 1st and exclude the January, 20th and 22nd from the computation as well as the period from Januaray, 25th to 28th included.

### Example 3

    java -jar days-calculator-cli-0.1.jar startDate=2016-01-01 endDate=2015-02-01 excludedDaysOfWeeks=SATURDAY,SUNDAY

Will compute the number of days between 2016, January 1st and 2016, February 1st and exclude all sundays and saturdays from the computation.

### Example 4

    java -jar days-calculator-cli-0.1.jar file=config.properties

Will compute the number of days according the configuration placed in the file. Possible properties for the configuration are:

* **startDate=&lt;ISO DATE&gt;** : defines the start date at which the computation will start. The date must be given in the *YYYY-MM-DD* format.
* **endDate=&lt;ISO DATE&gt;** : defines the end date (included) at which the computation will end. The date must be given in the *YYYY-MM-DD* format.
* **excludedDays=&lt;list of ISO DATE or periods&gt;** : defines the days to exclude from the computation. The list must be comma separated.
* **excludedDaysOfWeeks=&lt;list of days&gt;** : defines the days of week to exclude from the computation. The list must be comma separated. Possible values are
  * SUNDAY
  * MONDAY
  * TUESDAY
  * WEDNESDAY
  * THURSDAY
  * FRIDAY
  * SATURDAY
* **file=&lt;FILE&gt;** : defines a file containing all desired configuration for the computation.

# Build

In order to build the project, simply run:

    gradlew clean distZip

A `build` directory will be created and will contain a ZIP file containing the application to be run.