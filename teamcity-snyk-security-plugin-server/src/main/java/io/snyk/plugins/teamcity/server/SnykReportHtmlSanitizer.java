package io.snyk.plugins.teamcity.server;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Sanitizes HTML produced by snyk-to-html (and embedded in the TeamCity UI). The artifact must be
 * treated as untrusted because CLI JSON can influence report HTML (e.g. markdown via marked).
 */
public final class SnykReportHtmlSanitizer {

  /**
   * Composed policies from OWASP {@link Sanitizers}; an extra policy adds safe semantic tags and
   * {@code data-*} attributes used by snyk-to-html templates.
   */
  private static final PolicyFactory POLICY =
      Objects.requireNonNull(Sanitizers.FORMATTING)
          .and(Objects.requireNonNull(Sanitizers.BLOCKS))
          .and(Objects.requireNonNull(Sanitizers.STYLES))
          .and(Objects.requireNonNull(Sanitizers.LINKS))
          .and(Objects.requireNonNull(Sanitizers.TABLES))
          .and(Objects.requireNonNull(Sanitizers.IMAGES))
          .and(
              Objects.requireNonNull(
                  new HtmlPolicyBuilder()
                      .allowElements(
                          "pre",
                          "hr",
                          "main",
                          "header",
                          "footer",
                          "section",
                          "article",
                          "nav",
                          "aside",
                          "figure",
                          "figcaption",
                          "details",
                          "summary")
                      .allowAttributes("class", "id", "role", "lang", "dir")
                      .globally()
                      .allowAttributes("data-pane", "data-toggle")
                      .globally()
                      .toFactory()));

  private SnykReportHtmlSanitizer() {}

  @NotNull
  public static String sanitize(@NotNull String html) {
    return POLICY.sanitize(html);
  }
}
