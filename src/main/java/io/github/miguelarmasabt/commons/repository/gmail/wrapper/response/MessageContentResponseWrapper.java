package io.github.miguelarmasabt.commons.repository.gmail.wrapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageContentResponseWrapper implements Serializable {

  private String id;
  private String threadId;
  private String snippet;
  private Payload payload;
  private long sizeEstimate;
  private String historyId;
  private String internalDate;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Payload implements Serializable {
    private String mimeType;
    private List<Header> headers;
    private List<Part> parts;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Header implements Serializable {
    private String name;
    private String value;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Part implements Serializable {
    private String partId;
    private String mimeType;
    private List<Header> headers;
    private Body body;
    private List<Part> parts;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Body implements Serializable {
    private long size;
    private String data;
  }
}
