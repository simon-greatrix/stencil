package com.pippsford.stencil.blocks.value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;
import com.pippsford.util.CopyOnWriteMap;

/**
 * A date-time value formatted in some way.
 */
class BaseDateTimeValue extends BaseValue {

  static ZonedDateTime convert(Object msg, ZoneId zoneId) {
    // Convert object to a Date.
    if (msg instanceof Date) {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Date) msg).getTime()), zoneId);
    } else if (msg instanceof Number) {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Number) msg).longValue()), zoneId);
    } else if (msg instanceof Calendar) {
      Calendar calendar = (Calendar) msg;
      return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    } else if (msg instanceof Instant) {
      return ZonedDateTime.ofInstant((Instant) msg, zoneId);
    } else if (msg instanceof OffsetDateTime) {
      return ((OffsetDateTime) msg).toZonedDateTime();
    } else if (msg instanceof ZonedDateTime) {
      return (ZonedDateTime) msg;
    } else if (msg instanceof LocalTime) {
      return ((LocalTime) msg).atDate(LocalDate.now(zoneId)).atZone(zoneId);
    } else if (msg instanceof LocalDate) {
      return ((LocalDate) msg).atStartOfDay(zoneId);
    } else if (msg instanceof LocalDateTime) {
      return ((LocalDateTime) msg).atZone(zoneId);
    } else {
      throw new IllegalArgumentException("Cannot convert an instance of " + msg.getClass() + " to a date.");
    }
  }


  @SuppressWarnings("SpellCheckingInspection")
  static DateTimeFormatter matchNamed(String name) {
    name = name.toUpperCase(Locale.ENGLISH).replaceAll("[\\s._-]", "").replace("ISO", "");
    switch (name) {
      case "BASIC":
      case "BASICDATE":
        return DateTimeFormatter.BASIC_ISO_DATE;
      case "LOCALDATE":
        return DateTimeFormatter.ISO_LOCAL_DATE;
      case "OFFSETDATE":
        return DateTimeFormatter.ISO_OFFSET_DATE;
      case "DATE":
        return DateTimeFormatter.ISO_DATE;
      case "LOCALTIME":
        return DateTimeFormatter.ISO_LOCAL_TIME;
      case "OFFSETTIME":
        return DateTimeFormatter.ISO_OFFSET_TIME;
      case "TIME":
        return DateTimeFormatter.ISO_TIME;
      case "ZONED":
      case "ZONEDDATETIME":
        return DateTimeFormatter.ISO_ZONED_DATE_TIME;
      case "DATETIME":
        return DateTimeFormatter.ISO_DATE_TIME;
      case "ORDINAL":
      case "ORDINALDATE":
        return DateTimeFormatter.ISO_ORDINAL_DATE;
      case "WEEKDATE":
        return DateTimeFormatter.ISO_WEEK_DATE;
      case "INSTANT":
        return DateTimeFormatter.ISO_INSTANT;
      case "RFC":
      case "RFC1123":
      case "RFC822":
      case "RFC1123DATETIME":
        return DateTimeFormatter.RFC_1123_DATE_TIME;
      default:
        return null;
    }
  }


  static FormatStyle matchStyle(String name) {
    name = name.toUpperCase(Locale.ENGLISH);
    switch (name) {
      case "SHORT":
        return FormatStyle.SHORT;
      case "MEDIUM":
        return FormatStyle.MEDIUM;
      case "LONG":
        return FormatStyle.LONG;
      case "FULL":
        return FormatStyle.FULL;
      default:
        return null;
    }

  }


  /** Function to generate a locale-appropriate formatter. */
  private final Function<Locale, DateTimeFormatter> formatterFunction;

  /**
   * The formatter to use.
   */
  private final CopyOnWriteMap<Locale, DateTimeFormatter> formatters = new CopyOnWriteMap<>();


  /**
   * Date value renderer.
   *
   * @param param parameter to render
   */
  protected BaseDateTimeValue(BlockTypes type, Escape escapeStyle, String param, String style, Function<FormatStyle, DateTimeFormatter> standardLocale) {
    super(type, escapeStyle, param);
    if (style == null) {
      formatterFunction = standardLocale.apply(FormatStyle.MEDIUM)::withLocale;
      return;
    }

    DateTimeFormatter formatter = matchNamed(style);
    if (formatter != null) {
      formatterFunction = formatter::withLocale;
      return;
    }

    FormatStyle formatStyle = matchStyle(style);
    if (formatStyle != null) {
      formatter = standardLocale.apply(formatStyle);
      formatterFunction = formatter::withLocale;
      return;
    }

    formatter = DateTimeFormatter.ofPattern(style);
    formatterFunction = formatter::withLocale;

  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    Object msg = data.get(param);
    if (msg == null) {
      return "";
    }

    DateTimeFormatter formatter = formatters.computeIfAbsent(locale, formatterFunction);
    return formatter.format(convert(msg, zoneId));
  }

}
