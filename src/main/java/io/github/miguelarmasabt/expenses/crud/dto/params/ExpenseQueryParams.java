package io.github.miguelarmasabt.expenses.crud.dto.params;

import io.github.miguelarmasabt.commons.constants.Regex;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ExpenseQueryParams implements Serializable {

  @QueryParam("recipient")
  protected String recipient;

  @QueryParam("category")
  protected String category;

  @QueryParam("currency")
  protected String currency;

  @QueryParam("from")
  @Pattern(regexp = Regex.DATE_ISO_8601)
  protected String from;

  @QueryParam("to")
  @Pattern(regexp = Regex.DATE_ISO_8601)
  protected String to;
}