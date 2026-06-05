db.bank_receipt_templates.createIndex(
  { code: 1 },
  {
    unique: true,
    name: "uk_bank_receipt_templates_code"
  }
);

db.bank_receipt_templates.createIndex(
  {
    enabled: 1,
    "gmail.from": 1
  },
  {
    name: "idx_bank_receipt_templates_enabled_from"
  }
);

