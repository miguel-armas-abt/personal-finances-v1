package io.github.miguelarmasabt.expenses.bank.receipts;

import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class BankReceiptTemplateSeed implements Serializable {

  private Map<String, Map<String, String>> dictionaries;
  private List<BankReceiptTemplateEntity> templates;

}
