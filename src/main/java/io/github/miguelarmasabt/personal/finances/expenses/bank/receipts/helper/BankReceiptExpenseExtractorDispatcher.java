package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.helper;

import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.utils.GmailMessageHeaderUtil;
import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions.UnsupportedExtractExpenseStrategyException;
import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.github.miguelarmasabt.personal.finances.expenses.sync.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.commons.repository.google.model.MessageContentResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BankReceiptExpenseExtractorDispatcher {

  private final ExpenseExtractorHelper expenseExtractorHelper;
  private final GenericBankReceiptExpenseExtractor genericExtractor;

  public Uni<ExtractExpenseResponseDto> toDto(MessageContentResponseWrapper message,
                                              List<BankReceiptTemplateEntity> templates) {
    GmailMessageHeaderUtil.GmailMessageHeaders headers =
        GmailMessageHeaderUtil.extractHeaders(message.getPayload());

    BankReceiptTemplateEntity template = expenseExtractorHelper
        .resolveTemplate(templates, headers.from(), headers.subject())
        .orElseThrow(() -> new UnsupportedExtractExpenseStrategyException(headers.from(), headers.subject()));

    return genericExtractor.toDto(message, template);
  }
}