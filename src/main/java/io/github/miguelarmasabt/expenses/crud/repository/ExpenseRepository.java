package io.github.miguelarmasabt.expenses.crud.repository;

import io.github.miguelarmasabt.commons.exceptions.UserCodeRequiredException;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.commons.utils.MongoDbUtil;
import io.github.miguelarmasabt.expenses.crud.exceptions.ExpenseNotFoundException;
import io.github.miguelarmasabt.expenses.crud.repository.aggregation.MonthlyCategoryTotalAggregation;
import io.github.miguelarmasabt.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.crud.utils.CursorEncoder;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpenseRepository implements ReactivePanacheMongoRepository<ExpenseEntity> {

  private static final String FIELD_ID = "_id";
  private static final String FIELD_USER_CODE = "userCode";
  private static final String FIELD_DATE = "date";
  private static final String FIELD_AMOUNT = "amount";
  private static final String FIELD_CURRENCY = "currency";
  private static final String FIELD_GMAIL_MESSAGE_ID = "gmailMessageId";
  private static final String FIELD_DETAIL_CATEGORY = "detail.category";
  private static final String FIELD_DETAIL_RECIPIENT = "detail.recipient";

  private static final String AGG_FIELD_CATEGORY = "category";
  private static final String AGG_FIELD_CURRENCY = "currency";
  private static final String AGG_FIELD_TOTAL = "total";

  private static Bson buildSearchFilter(ExpenseSearchCriteria criteria) {
    validateUserCode(criteria.getUserCode());

    List<Bson> filters = new ArrayList<>();
    filters.add(Filters.eq(FIELD_USER_CODE, criteria.getUserCode()));

    addEqualsFilter(filters, FIELD_DETAIL_CATEGORY, criteria.getCategory());
    addEqualsFilter(filters, FIELD_CURRENCY, criteria.getCurrency());
    addDateRangeFilter(filters, criteria.getFrom(), criteria.getTo());
    addRecipientFilter(filters, criteria.getRecipient());

    return Filters.and(filters);
  }

  private static void addEqualsFilter(List<Bson> filters, String field, String value) {
    Optional.ofNullable(value)
        .map(String::trim)
        .filter(trimmed -> !trimmed.isBlank())
        .ifPresent(trimmed -> filters.add(Filters.eq(field, trimmed)));
  }

  private static void addDateRangeFilter(List<Bson> filters, Instant from, Instant to) {
    if (Objects.nonNull(from)) {
      filters.add(Filters.gte(FIELD_DATE, from));
    }

    if (Objects.nonNull(to)) {
      filters.add(Filters.lte(FIELD_DATE, to));
    }
  }

  private static void addRecipientFilter(List<Bson> filters, String recipient) {
    Optional.ofNullable(recipient)
        .map(String::trim)
        .filter(trimmed -> !trimmed.isBlank())
        .map(trimmed -> Pattern.compile(Pattern.quote(trimmed), Pattern.CASE_INSENSITIVE))
        .ifPresent(pattern -> filters.add(Filters.regex(FIELD_DETAIL_RECIPIENT, pattern)));
  }

  private static Bson buildCursorFilter(CursorEncoder.Cursor cursor) {
    if (Objects.isNull(cursor)) {
      return null;
    }

    return Filters.or(
        Filters.lt(FIELD_DATE, cursor.date()),
        Filters.and(
            Filters.eq(FIELD_DATE, cursor.date()),
            Filters.lt(FIELD_ID, cursor.id())
        )
    );
  }

  private static Bson mergeFilters(Bson baseFilter, Bson additionalFilter) {
    if (Objects.isNull(additionalFilter)) {
      return baseFilter;
    }

    return Filters.and(baseFilter, additionalFilter);
  }

  private static List<Bson> buildCurrentMonthTotalsByCategoryPipeline(String userCode, Instant from, Instant to) {
    return List.of(
        Aggregates.match(Filters.and(
            Filters.eq(FIELD_USER_CODE, userCode),
            Filters.gte(FIELD_DATE, from),
            Filters.lt(FIELD_DATE, to),
            Filters.ne(FIELD_DETAIL_CATEGORY, null),
            Filters.ne(FIELD_DETAIL_CATEGORY, ""),
            Filters.ne(FIELD_CURRENCY, null),
            Filters.ne(FIELD_CURRENCY, "")
        )),
        Aggregates.group(
            new Document()
                .append(AGG_FIELD_CATEGORY, "$" + FIELD_DETAIL_CATEGORY)
                .append(AGG_FIELD_CURRENCY, "$" + FIELD_CURRENCY),
            Accumulators.sum(AGG_FIELD_TOTAL, "$" + FIELD_AMOUNT)
        ),
        Aggregates.project(Projections.fields(
            Projections.excludeId(),
            Projections.computed(AGG_FIELD_CATEGORY, "$_id." + AGG_FIELD_CATEGORY),
            Projections.computed(AGG_FIELD_CURRENCY, "$_id." + AGG_FIELD_CURRENCY),
            Projections.computed(AGG_FIELD_TOTAL, "$" + AGG_FIELD_TOTAL)
        )),
        Aggregates.sort(Sorts.ascending(AGG_FIELD_CATEGORY, AGG_FIELD_CURRENCY))
    );
  }

  private static BigDecimal toBigDecimal(Object value) {
    return Optional.ofNullable(value)
        .map(Object::toString)
        .map(BigDecimal::new)
        .orElse(BigDecimal.ZERO);
  }

  private static void validateUserCode(String userCode) {
    if (isBlank(userCode)) {
      throw new UserCodeRequiredException();
    }
  }

  private static boolean isBlank(String value) {
    return Objects.isNull(value) || value.isBlank();
  }

  private static boolean isNotBlank(String value) {
    return !isBlank(value);
  }

  private static Document buildSortByDateDocument() {
    return toDocument(Sorts.orderBy(
        Sorts.descending(FIELD_DATE),
        Sorts.descending(FIELD_ID)
    ));
  }

  private static Document toDocument(Bson bson) {
    return Document.parse(bson.toBsonDocument().toJson());
  }

  public Uni<String> save(ExpenseEntity expense) {
    return persist(expense)
        .map(ExpenseEntity::getId)
        .map(ObjectId::toString);
  }

  public Uni<Void> saveAll(List<ExpenseEntity> expenses) {
    if (Objects.isNull(expenses) || expenses.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    return persist(expenses).replaceWithVoid();
  }

  public Uni<Boolean> existsGmailMessage(String gmailMessageId) {
    return count(FIELD_GMAIL_MESSAGE_ID, gmailMessageId)
        .map(count -> count > 0);
  }

  public Uni<Void> updateById(String expenseId, ExpenseEntity expense) {
    return MongoDbUtil.validateAndGetObjectId(expenseId)
        .flatMap(this::findById)
        .onItem().ifNull().failWith(() -> new ExpenseNotFoundException(expenseId))
        .flatMap(existingExpense -> updateExpense(existingExpense, expense))
        .replaceWithVoid();
  }

  public Uni<Void> deleteById(String expenseId) {
    return MongoDbUtil.validateAndGetObjectId(expenseId)
        .flatMap(this::deleteById)
        .flatMap(deleted -> deleted
            ? Uni.createFrom().voidItem()
            : Uni.createFrom().failure(new ExpenseNotFoundException(expenseId)));
  }

  public Multi<ExpenseEntity> search(ExpenseSearchCriteria criteria) {
    return find(
        toDocument(buildSearchFilter(criteria)),
        buildSortByDateDocument()
    ).stream();
  }

  public Multi<ExpenseEntity> searchBySortPagination(ExpenseSearchCriteria criteria,
                                                     int limit,
                                                     CursorEncoder.Cursor cursor) {
    Bson filter = mergeFilters(
        buildSearchFilter(criteria),
        buildCursorFilter(cursor));

    return find(toDocument(filter), buildSortByDateDocument())
        .page(0, limit + 1)
        .stream();
  }

  public Uni<MonthlyCategoryTotalAggregation> getCurrentMonthTotalsByCategory(String userCode) {
    validateUserCode(userCode);

    Instant from = DateUtil.firstDayOfCurrentMonth();
    Instant to = DateUtil.firstDayOfNextMonth();

    return mongoCollection()
        .aggregate(buildCurrentMonthTotalsByCategoryPipeline(userCode, from, to), Document.class)
        .collect().asList()
        .map(this::toMonthlyCategoryTotalsResponse);
  }

  private Uni<ExpenseEntity> updateExpense(ExpenseEntity target, ExpenseEntity source) {
    if (Objects.nonNull(source.getDate())) {
      target.setDate(source.getDate());
    }

    if (Objects.nonNull(source.getCurrency())) {
      target.setCurrency(source.getCurrency());
    }

    if (Objects.nonNull(source.getAmount())) {
      target.setAmount(source.getAmount());
    }

    if (Objects.nonNull(source.getDetail())) {
      if (Objects.isNull(target.getDetail())) {
        target.setDetail(new ExpenseEntity.ExpenseDetail());
      }

      if (Objects.nonNull(source.getDetail().getCategory())) {
        target.getDetail().setCategory(source.getDetail().getCategory());
      }

      if (Objects.nonNull(source.getDetail().getRecipient())) {
        target.getDetail().setRecipient(source.getDetail().getRecipient());
      }

      if (Objects.nonNull(source.getDetail().getComments())) {
        target.getDetail().setComments(source.getDetail().getComments());
      }
    }

    return persistOrUpdate(target);
  }

  private MonthlyCategoryTotalAggregation toMonthlyCategoryTotalsResponse(List<Document> documents) {
    var grouped = documents.stream()
        .filter(Objects::nonNull)
        .filter(document -> isNotBlank(document.getString(AGG_FIELD_CATEGORY)))
        .filter(document -> isNotBlank(document.getString(AGG_FIELD_CURRENCY)))
        .collect(Collectors.groupingBy(
            document -> document.getString(AGG_FIELD_CATEGORY),
            Collectors.mapping(
                document -> new MonthlyCategoryTotalAggregation.CurrencyTotal(
                    document.getString(AGG_FIELD_CURRENCY),
                    toBigDecimal(document.get(AGG_FIELD_TOTAL))
                ),
                Collectors.toList()
            )
        ));

    List<MonthlyCategoryTotalAggregation.ExpenseCategory> categories = grouped.entrySet().stream()
        .map(entry -> new MonthlyCategoryTotalAggregation.ExpenseCategory(entry.getKey(), entry.getValue()))
        .toList();

    return new MonthlyCategoryTotalAggregation(categories);
  }
}