package io.twasyl.days.calculator.core;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.twasyl.days.calculator.core.DaysCalculator.*;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static org.junit.Assert.assertEquals;

/**
 * @author Thierrry Wasylczenko
 * @version 1.0
 * @since 1.0
 */
public class DaysCalculatorTest {

    @Test
    public void parseString() {
        final LocalDate actual = DaysCalculator.instance().parse("2016-01-02");
        final LocalDate expected = LocalDate.of(2016, Month.JANUARY, 02);

        assertEquals(expected, actual);
    }

    @Test
    public void startAndEndDatesAreEquals() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(LocalDate.now())
                .endAt(LocalDate.now());

        assertEquals(1, dc.calculate());
    }

    @Test
    public void startAndEndDatesAreEqualsAndAsString() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt("2016-05-10")
                .endAt("2016-05-10");

        assertEquals(1, dc.calculate());
    }

    @Test
    public void twoDays() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1));

        assertEquals(2, dc.calculate());
    }

    @Test
    public void twoDaysWithExcludedDayOfWeeks() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(2))
                .excludeDaysOfWeek(LocalDate.now().plusDays(1).getDayOfWeek());

        assertEquals(2, dc.calculate());
    }

    @Test
    public void twoDaysWithExcludedDate() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(2))
                .excludeDay(LocalDate.now().plusDays(1));

        assertEquals(2, dc.calculate());
    }

    @Test
    public void twoDaysWithExcludedDates() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(3))
                .excludeDays(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        assertEquals(2, dc.calculate());
    }

    @Test
    public void twoDaysWithExcludedDateAsString() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt("2016-05-10")
                .endAt("2016-05-12")
                .excludeDay("2016-05-11");

        assertEquals(2, dc.calculate());
    }

    @Test
    public void twoDaysWithExcludedDatesAsString() {
        final DaysCalculator dc = DaysCalculator.instance()
                .startAt("2016-05-10")
                .endAt("2016-05-13")
                .excludeDays("2016-05-11", "2016-05-12");

        assertEquals(2, dc.calculate());
    }

    @Test
    public void fiveDaysWithPeriod() {
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(10);

        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(startDate)
                .endAt(endDate)
                .excludePeriod(startDate.plusDays(1), startDate.plusDays(6));

        assertEquals(5, dc.calculate());
    }

    @Test
    public void fiveDaysWithPeriodAsString() {
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(10);

        final DaysCalculator dc = DaysCalculator.instance()
                .startAt(startDate)
                .endAt(endDate)
                .excludePeriod(String.format("%1$s->%2$s", startDate.plusDays(1), startDate.plusDays(6)));

        assertEquals(5, dc.calculate());
    }

    @Test
    public void configuration() {
        final Properties properties = new Properties();

        final LocalDate now = LocalDate.now();
        final LocalDate startDate = now.with(firstInMonth(MONDAY));
        final LocalDate endDate = startDate.plusDays(11);

        properties.put(START_DATE_PROPERTY, startDate.toString());
        properties.put(END_DATE_PROPERTY, endDate.toString());

        properties.put(EXCLUDED_DAYS_PROPERTY, new StringJoiner(", ")
                .add(startDate.plusDays(1).toString())
                .add(startDate.plusDays(2).toString())
                .add(startDate.plusDays(6) + "->" +startDate.plusDays(9))
                .toString());

        properties.put(EXCLUDED_DAYS_OF_WEEKS_PROPERTY, new StringJoiner(", ")
                .add(startDate.plusDays(3).getDayOfWeek().toString())
                .add(startDate.plusDays(4).getDayOfWeek().toString())
                .toString());

        final DaysCalculator dc = DaysCalculator.instance().configure(properties);

        assertEquals(2, dc.calculate());
    }
}
