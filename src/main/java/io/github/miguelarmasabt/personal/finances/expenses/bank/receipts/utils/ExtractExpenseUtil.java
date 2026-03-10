package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.utils;

import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.commons.repository.google.model.MessageBody;
import io.github.miguelarmasabt.commons.repository.google.model.MessagePart;
import io.github.miguelarmasabt.commons.repository.google.model.MessagePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractExpenseUtil {

  private static final String TEXT_HTML = "text/html";
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  public static String extractHtmlBody(MessagePayload payload) {
    return findHtmlBody(payload)
        .orElse(Strings.EMPTY);
  }

  public static String toPlainText(String html) {
    return MULTIPLE_SPACES.matcher(Jsoup.parse(html).text())
        .replaceAll(Strings.SPACE)
        .trim();
  }

  private static Optional<String> findHtmlBody(MessagePayload payload) {
    return Optional.ofNullable(payload)
        .flatMap(ExtractExpenseUtil::findHtmlBodyInPayload);
  }

  private static Optional<String> findHtmlBodyInPayload(MessagePayload payload) {
    Optional<String> rootBody = extractRootBody(payload);

    if (rootBody.isPresent()) {
      return rootBody;
    }

    return streamParts(payload.getParts())
        .map(ExtractExpenseUtil::findHtmlBodyInPart)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  private static Optional<String> findHtmlBodyInPart(MessagePart part) {
    Optional<String> partBody = extractBodyIfHtml(part.getMimeType(), part.getBody());

    if (partBody.isPresent()) {
      return partBody;
    }

    return streamParts(part.getParts())
        .map(ExtractExpenseUtil::findHtmlBodyInPart)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  private static Optional<String> extractRootBody(MessagePayload payload) {
    if (Objects.isNull(payload.getBody()) || Objects.isNull(payload.getBody().getData())) {
      return Optional.empty();
    }

    if (Objects.isNull(payload.getParts()) || payload.getParts().isEmpty() || isHtml(payload.getMimeType())) {
      return Optional.of(decodeBody(payload.getBody().getData()));
    }

    return Optional.empty();
  }

  private static Optional<String> extractBodyIfHtml(String mimeType, MessageBody body) {
    if (!isHtml(mimeType) || Objects.isNull(body) || Objects.isNull(body.getData())) {
      return Optional.empty();
    }

    return Optional.of(decodeBody(body.getData()));
  }

  private static boolean isHtml(String mimeType) {
    return TEXT_HTML.equalsIgnoreCase(mimeType);
  }

  private static Stream<MessagePart> streamParts(List<MessagePart> parts) {
    return Optional.ofNullable(parts)
        .stream()
        .flatMap(List::stream)
        .filter(Objects::nonNull);
  }

  private static String decodeBody(String data) {
    byte[] decoded = Base64.getUrlDecoder().decode(data);
    return new String(decoded, StandardCharsets.UTF_8);
  }
}