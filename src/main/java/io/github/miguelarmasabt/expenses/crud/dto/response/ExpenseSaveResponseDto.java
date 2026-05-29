package io.github.miguelarmasabt.expenses.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSaveResponseDto implements Serializable {

  private String expenseId;
}
