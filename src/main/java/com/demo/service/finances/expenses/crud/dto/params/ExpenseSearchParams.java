package com.demo.service.finances.expenses.crud.dto.params;

import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ExpenseSearchParams extends ExpenseQueryParams implements Serializable {

  @QueryParam("encodedCursor")
  String encodedCursor;
}