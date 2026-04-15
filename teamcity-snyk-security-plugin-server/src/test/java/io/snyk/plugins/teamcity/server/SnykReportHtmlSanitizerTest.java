package io.snyk.plugins.teamcity.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnykReportHtmlSanitizerTest {

  @Test
  void removesImgOnerrorFromSnykToHtmlStylePayload() {
    String malicious = "<hr/><!-- Overview --><img src=x onerror=\"alert(1)\">";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("onerror"));
    assertFalse(safe.contains("alert(1)"));
  }

  @Test
  void stripsScriptTags() {
    String malicious = "<div>hi<script>alert(1)</script></div>";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("<script"));
    assertFalse(safe.contains("alert(1)"));
  }

  @Test
  void neutralizesJavascriptInHref() {
    String malicious = "<a href=\"javascript:alert(1)\">click</a>";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("javascript:"));
  }

  @Test
  void preservesBenignReportLikeMarkup() {
    String html =
        "<html><head><title>Report</title><style>body{font-size:12px;}</style></head>"
            + "<body><div class=\"card\"><table><tr><th>Sev</th></tr><tr><td>high</td></tr></table>"
            + "<a href=\"https://example.com/issue\">link</a></div></body></html>";
    String safe = SnykReportHtmlSanitizer.sanitize(html);
    assertTrue(safe.contains("<table"));
    assertTrue(safe.contains("https://example.com/issue"));
    assertTrue(safe.contains("<div"));
  }

  @Test
  void stripsDataUriJavascriptPayload() {
    String malicious = "<a href=\"data:text/html;base64,PHNjcmlwdD5hbGVydCgxKTwvc2NyaXB0Pg==\">click</a>";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("data:"));
  }

  @Test
  void stripsMetaRefreshRedirect() {
    String malicious = "<meta http-equiv=\"refresh\" content=\"0;url=https://evil.com/phishing\">";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("<meta"));
    assertFalse(safe.contains("evil.com"));
  }

  @Test
  void stripsStyleAndLinkTags() {
    String malicious =
        "<style>body{display:none}</style><link rel=\"stylesheet\" href=\"https://evil.com/x.css\">";
    String safe = SnykReportHtmlSanitizer.sanitize(malicious);
    assertFalse(safe.toLowerCase().contains("<style"));
    assertFalse(safe.toLowerCase().contains("<link"));
    assertFalse(safe.contains("evil.com"));
  }

  @Test
  void stripsDocumentShellElementsWhenEmbedded() {
    String html = "<html><head><title>t</title></head><body><main>safe-content</main></body></html>";
    String safe = SnykReportHtmlSanitizer.sanitize(html);
    assertFalse(safe.toLowerCase().contains("<html"));
    assertFalse(safe.toLowerCase().contains("<head"));
    assertFalse(safe.toLowerCase().contains("<body"));
    assertTrue(safe.contains("safe-content"));
  }

  @Test
  void sanitizeCompletesForLargeInput() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 500; i++) {
      sb.append("<p>line ").append(i).append("</p>");
    }
    String html = sb.toString();
    assertDoesNotThrow(() -> SnykReportHtmlSanitizer.sanitize(html));
  }
}
