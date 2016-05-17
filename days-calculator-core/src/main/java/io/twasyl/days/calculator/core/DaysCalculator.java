package io.twasyl.days.calculator.core;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/**
 * @author Thierry Wasylczenko
 * @version 0.1
 * @since 0.1
 */
public class DaysCalculator {

    private static final Logger LOGGER = Logger.getLogger(DaysCalculator.class.getName());

    protected static final String START_DATE_PROPERTY = "startDate";
    protected static final String END_DATE_PROPERTY = "endDate";
    protected static final String EXCLUDED_DAYS_PROPERTY = "excludedDays";
    protected static final String EXCLUDED_DAYS_OF_WEEKS_PROPERTY = "excludedDaysOfWeeks";
    protected static final String PERIOD_SEPARATOR = "->";

    private LocalDate startDate;
    private LocalDate endDate;

    private final Set<LocalDate> excludedDays;
    private final Set<DayOfWeek> excludedDaysOfWeek;
    private DateTimeFormatter formatter;

    private DaysCalculator() {
        this.excludedDays = new HashSet<>();
        this.excludedDaysOfWeek = new HashSet<>();
    }

    public static DaysCalculator instance() {
        return new DaysCalculator();
    }

    public DaysCalculator configure(final Properties properties) {
        if(properties != null) {
            properties.forEach((key, value) -> {
                if(START_DATE_PROPERTY.equals(key)) {
                    this.startAt((String) value);
                } else if(END_DATE_PROPERTY.equals(key)) {
                    this.endAt((String) value);
                } else if(EXCLUDED_DAYS_PROPERTY.equals(key)) {
                    String[] days = ((String) value).split(",");

                    this.trimAllValues(days);

                    final String[] effectiveDays = Arrays.stream(days)
                            .filter(day -> !day.contains(PERIOD_SEPARATOR))
                            .toArray(String[]::new);
                    this.excludeDays(effectiveDays);

                    final String[] effectivePeriods = Arrays.stream(days)
                            .filter(day -> day.contains(PERIOD_SEPARATOR))
                            .toArray(String[]::new);
                    this.excludePeriods(effectivePeriods);
                } else if(EXCLUDED_DAYS_OF_WEEKS_PROPERTY.equals(key)) {
                    final String[] days = ((String) value).split(",");
                    this.trimAllValues(days);
                    this.excludeDaysOfWeek(days);
                }
            });
        }

        return this;
    }

    public DaysCalculator startAt(final LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public DaysCalculator startAt(final String startDate) {
        this.startDate = this.parse(startDate);
        return this;
    }

    public DaysCalculator endAt(final LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public DaysCalculator endAt(final String endDate) {
        this.endDate = this.parse(endDate);
        return this;
    }

    public DaysCalculator excludeDay(final LocalDate day) {
        this.excludedDays.add(day);
        return this;
    }

    public DaysCalculator excludeDay(final String day) {
        return this.excludeDay(this.parse(day));
    }

    public DaysCalculator excludeDays(final LocalDate ... days) {
        if(days != null && days.length > 0) {
            Arrays.stream(days).forEach(this::excludeDay);
        }
        return this;
    }

    public DaysCalculator excludeDays(final String ... days) {
        if(days != null && days.length > 0) {
            Arrays.stream(days).map(day -> parse(day)).forEach(this::excludeDay);
        }
        return this;
    }

    public DaysCalculator excludePeriod(final LocalDate periodStart, final LocalDate periodEnd) {
        LocalDate index = LocalDate.from(periodStart);

        while(index.isBefore(periodEnd) || index.isEqual(periodEnd)) {
            this.excludeDay(index);
            index = index.plusDays(1);
        }

        return this;
    }

    public DaysCalculator excludePeriod(final String period) {
        int periodSeparatorIndex = period.indexOf(PERIOD_SEPARATOR);

        if(periodSeparatorIndex != -1) {
            final LocalDate periodStart = this.parse(period.substring(0, periodSeparatorIndex));
            final LocalDate periodEnd = this.parse(period.substring(periodSeparatorIndex + PERIOD_SEPARATOR.length()));
            return this.excludePeriod(periodStart, periodEnd);
        } else {
            LOGGER.log(WARNING, "Period ignored because no valid separator was found: " + period);
        }

        return this;
    }

    public DaysCalculator excludePeriods(final String ... periods) {
        if(periods != null && periods.length > 0) {
            Arrays.stream(periods).forEach(this::excludePeriod);
        }

        return this;
    }

    public DaysCalculator excludeMondays() {
        return this.excludeDaysOfWeek(DayOfWeek.MONDAY);
    }

    public DaysCalculator excludeTuesdays() {
        return this.excludeDaysOfWeek(DayOfWeek.TUESDAY);
    }

    public DaysCalculator excludeWednesdays() {
        return this.excludeDaysOfWeek(DayOfWeek.WEDNESDAY);
    }

    public DaysCalculator excludeThursdays() {
        return this.excludeDaysOfWeek(DayOfWeek.THURSDAY);
    }

    public DaysCalculator excludeFridays() {
        return this.excludeDaysOfWeek(DayOfWeek.FRIDAY);
    }

    public DaysCalculator excludeSaturdays() {
        return this.excludeDaysOfWeek(DayOfWeek.SATURDAY);
    }

    public DaysCalculator excludeSundays() {
        return this.excludeDaysOfWeek(DayOfWeek.SUNDAY);
    }

    public DaysCalculator excludeDaysOfWeek(final DayOfWeek ... daysOfWeek) {
        if(daysOfWeek != null && daysOfWeek.length > 0) {
            Arrays.stream(daysOfWeek).forEach(this.excludedDaysOfWeek::add);
        }

        return this;
    }

    public DaysCalculator excludeDaysOfWeek(final String ... daysOfWeek) {
        if(daysOfWeek != null && daysOfWeek.length > 0) {
            Arrays.stream(daysOfWeek).map(DayOfWeek::valueOf).forEach(this.excludedDaysOfWeek::add);
        }

        return this;
    }

    public int calculate() {
        LocalDate index = LocalDate.from(startDate);
        int numberOfDays = 0;

        while(index.isBefore(endDate) || index.isEqual(endDate)) {
            if(dayCanBeCounted(index)) {
                numberOfDays++;
            }

            index = index.plusDays(1);
        }

        return numberOfDays;
    }

    protected void trimAllValues(final String[] values) {
        for(int index = 0; index < values.length; index++) {
            values[index] = values[index].trim();
        }
    }

    protected boolean dayCanBeCounted(final LocalDate day) {
        return !(this.excludedDays.contains(day) || this.excludedDaysOfWeek.contains(day.getDayOfWeek()));
    }

    protected LocalDate parse(final String day) {
        if(formatter == null) {
            this.formatter = DateTimeFormatter.ISO_DATE;
        }

        return LocalDate.parse(day.trim(), this.formatter);
    }
}
