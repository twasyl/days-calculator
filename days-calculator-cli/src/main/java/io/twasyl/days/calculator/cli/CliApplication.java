package io.twasyl.days.calculator.cli;

import io.twasyl.days.calculator.core.DaysCalculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Thierry Wasylczenko
 * @version 0.1
 * @since 0.1
 */
public class CliApplication {
    private static final Logger LOGGER = Logger.getLogger(CliApplication.class.getName());

    protected static final char ARGUMENT_NAME_VALUE_SEPARATOR = '=';
    protected static final String FILE_ARGUMENT_NAME = "file";
    protected static final String START_DATE_ARGUMENT_NAME = "startDate";
    protected static final String END_DATE_ARGUMENT_NAME = "endDate";
    protected static final String EXCLUDED_DAYS_ARGUMENT_NAME = "excludedDays";
    protected static final String EXCLUDED_DAYS_OF_WEEKS_ARGUMENT_NAME = "excludedDaysOfWeeks";

    public static void main(String[] args) {
        final Set<Argument> arguments = parseArguments(args);

        final DaysCalculator calculator = DaysCalculator.instance();

        Argument<File> file = arguments.stream()
                .filter(argument -> FILE_ARGUMENT_NAME.equals(argument.getName()))
                .findFirst()
                .orElse(null);
        manageFileArgument(calculator, file);

        Argument<LocalDate> startDate = arguments.stream()
                .filter(argument ->  START_DATE_ARGUMENT_NAME.equals(argument.name))
                .findFirst()
                .orElse(null);
        manageStartDateArgument(calculator, startDate);

        Argument<LocalDate> endDate = arguments.stream()
                .filter(argument ->  END_DATE_ARGUMENT_NAME.equals(argument.name))
                .findFirst()
                .orElse(null);
        manageEndDateArgument(calculator, endDate);

        Argument<LocalDate[]> excludedDays = arguments.stream()
                .filter(argument ->  EXCLUDED_DAYS_ARGUMENT_NAME.equals(argument.name))
                .findFirst()
                .orElse(null);
        manageExcludedDaysArgument(calculator, excludedDays);

        Argument<DayOfWeek[]> excludedDaysOfWeek = arguments.stream()
                .filter(argument ->  EXCLUDED_DAYS_OF_WEEKS_ARGUMENT_NAME.equals(argument.name))
                .findFirst()
                .orElse(null);
        manageExcludedDaysOfWeekArgument(calculator, excludedDaysOfWeek);

        System.out.println("Number of days: " + calculator.calculate());
    }

    protected static Set<Argument> parseArguments(final String[] arguments) {
        final Set<Argument> parsedArguments = new HashSet<>();

        if(arguments != null && arguments.length > 0) {
            Arrays.stream(arguments).forEach(argument -> parsedArguments.add(parseArgument(argument)));
        }

        return parsedArguments;
    }

    protected static Argument parseArgument(final String argument) {
        if(argument == null || argument.trim().isEmpty()) return null;

        final String trimedArgument = argument.trim();
        int equalSignIndex = trimedArgument.indexOf(ARGUMENT_NAME_VALUE_SEPARATOR);

        String name;
        String value = null;

        if(equalSignIndex == -1) {
            name = trimedArgument;
        }
        else {
            name = trimedArgument.substring(0, equalSignIndex);
            value = trimedArgument.substring(equalSignIndex + 1);
        }

        return createArgument(name, value);
    }

    protected static Argument createArgument(final String name, final String value) {
        Argument argument;

        switch (name) {
            case FILE_ARGUMENT_NAME:
                argument = new Argument(name, new File(value));
                break;
            case START_DATE_ARGUMENT_NAME:
            case END_DATE_ARGUMENT_NAME:
                argument = new Argument(name, LocalDate.parse(value, DateTimeFormatter.ISO_DATE));
                break;
            case EXCLUDED_DAYS_ARGUMENT_NAME:
                final LocalDate[] days = Arrays.stream(value.split(","))
                        .map(day -> LocalDate.parse(day, DateTimeFormatter.ISO_DATE))
                        .toArray(LocalDate[]::new);
                argument = new Argument(name, days);
                break;
            case EXCLUDED_DAYS_OF_WEEKS_ARGUMENT_NAME:
                final DayOfWeek[] daysOfWeek = Arrays.stream(value.split(","))
                        .map(DayOfWeek::valueOf)
                        .toArray(DayOfWeek[]::new);
                argument = new Argument(name, daysOfWeek);
                break;
            default:
                argument = new Argument(name, null);
        }

        return argument;
    }

    protected static boolean isArgumentNonNull(final Argument argument) {
        return argument != null && argument.getValue() != null;
    }

    protected static void manageFileArgument(final DaysCalculator calculator, final Argument<File> argument) {
        if(isArgumentNonNull(argument) && argument.getValue().exists()) {
            final Properties properties = new Properties();
            try(final FileInputStream stream = new FileInputStream(argument.getValue())) {
                properties.load(stream);
                calculator.configure(properties);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Can not load data file", e);
            }
        }
    }

    protected static void manageStartDateArgument(final DaysCalculator calculator, final Argument<LocalDate> argument) {
        if(isArgumentNonNull(argument)) {
            calculator.startAt(argument.getValue());
        }
    }

    protected static void manageEndDateArgument(final DaysCalculator calculator, final Argument<LocalDate> argument) {
        if(isArgumentNonNull(argument)) {
            calculator.endAt(argument.getValue());
        }
    }

    protected static void manageExcludedDaysArgument(final DaysCalculator calculator, final Argument<LocalDate[]> argument) {
        if(isArgumentNonNull(argument)) {
            calculator.excludeDays(argument.getValue());
        }
    }

    protected static void manageExcludedDaysOfWeekArgument(final DaysCalculator calculator, final Argument<DayOfWeek[]> argument) {
        if(isArgumentNonNull(argument)) {
            calculator.excludeDaysOfWeek(argument.getValue());
        }
    }
}
