package com.demo.service.finances.expenses.csv.service.impl;

import com.demo.commons.validations.BodyValidator;
import com.demo.service.commons.config.csv.CsvConfig;
import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.finances.expenses.crud.mapper.ExpenseSaveMapper;
import com.demo.service.finances.expenses.crud.repository.ExpenseRepository;
import com.demo.service.finances.expenses.crud.repository.entity.ExpenseEntity;
import com.demo.service.finances.expenses.csv.dto.ExpenseCsvRowDto;
import com.demo.service.finances.expenses.csv.exceptions.CsvReadException;
import com.demo.service.finances.expenses.csv.helper.ImportExpenseRowProcessor;
import com.demo.service.finances.expenses.csv.helper.ImportExpenseCsvValidator;
import com.demo.service.finances.expenses.csv.mapper.ExportExpenseCsvMapper;
import com.demo.service.finances.expenses.csv.service.ImportExpenseCsvService;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import io.quarkus.mongodb.panache.common.reactive.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class ImportExpenseCsvServiceImpl implements ImportExpenseCsvService {

  private final ExpenseRepository expenseRepository;
  private final ExportExpenseCsvMapper csvMapper;
  private final ExpenseSaveMapper saveMapper;
  private final BodyValidator bodyValidator;
  private final ApplicationProperties properties;

  @Override
  public Uni<Void> importCsv(String userCode, FileUpload file) {
    return Uni.createFrom()
        .item(() -> parseAndValidateRows(userCode, file))
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
        .onItem().transformToMulti(Multi.createFrom()::iterable)
        .onItem().transform(csvMapper::toSaveRequest)
        .onItem().transformToUniAndConcatenate(bodyValidator::validateAndGet)
        .onItem().transform(row -> saveMapper.toEntity(userCode, row))
        .collect().asList()
        .flatMap(this::persistAllOrNothingInTransaction);
  }

  private List<ExpenseCsvRowDto> parseAndValidateRows(String userCode, FileUpload file) {
    ImportExpenseCsvValidator.validateImportFile(userCode, file);

    ImportExpenseRowProcessor rowProcessor = new ImportExpenseRowProcessor();
    CsvParserSettings settings = CsvConfig.createParserSettings(rowProcessor);

    try (Reader reader = Files.newBufferedReader(file.uploadedFile(), StandardCharsets.UTF_8)) {
      CsvParser parser = new CsvParser(settings);
      parser.parse(reader);

      ImportExpenseCsvValidator.validateHeaders(rowProcessor.getHeaders());

      return rowProcessor.getRows();
    } catch (IOException exception) {
      throw new CsvReadException(exception);
    }
  }

  private Uni<Void> persistAllOrNothingInTransaction(List<ExpenseEntity> entities) {
    if (entities.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    return Panache.withTransaction(() ->
        Multi.createFrom().iterable(entities)
            .group().intoLists().of(properties.features().csv().imports().batchSize())
            .onItem().transformToUniAndConcatenate(expenseRepository::saveAll)
            .collect().last()
            .replaceWithVoid()
    );
  }
}