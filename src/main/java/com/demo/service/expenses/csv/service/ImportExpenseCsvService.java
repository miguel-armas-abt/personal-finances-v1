package com.demo.service.expenses.csv.service;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public interface ImportExpenseCsvService {

  Uni<Void> importCsv(String userCode, FileUpload file);
}
