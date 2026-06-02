package io.github.miguelarmasabt.expenses.extracted.utils;

import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.repository.gmail.model.MessagePart;
import io.github.miguelarmasabt.repository.gmail.model.MessagePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractExpenseUtil {

  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  public static String extractHtmlBody(MessagePayload payload) {
    return Optional.ofNullable(payload.getParts())
        .stream()
        .flatMap(List::stream)
        .map(MessagePart::getParts)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(p -> "text/html".equals(p.getMimeType()))
        .map(p -> new String(Base64.getUrlDecoder().decode(p.getBody().getData()), StandardCharsets.UTF_8))
        .findFirst()
        .orElse(StringUtils.EMPTY);
  }

  public static String toPlainText(String html) {
    return MULTIPLE_SPACES.matcher(Jsoup.parse(html).text())
        .replaceAll(Strings.SPACE)
        .trim();
  }
}
