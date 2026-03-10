package io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoryAssignmentRequestDto implements Serializable {

  @NotNull
  private Date from;

  private boolean overwriteExistingCategories;
}
