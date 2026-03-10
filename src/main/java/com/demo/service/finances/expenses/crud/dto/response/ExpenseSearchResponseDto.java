package com.demo.service.finances.expenses.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSearchResponseDto implements Serializable {

  private List<ExpenseResponseDto> expenses;
  private String nextCursor;
}
