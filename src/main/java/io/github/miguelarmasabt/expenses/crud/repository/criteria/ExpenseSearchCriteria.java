package io.github.miguelarmasabt.expenses.crud.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSearchCriteria implements Serializable {

  String userCode;

  String recipient;

  String category;

  String currency;

  Instant from;

  Instant to;
}
