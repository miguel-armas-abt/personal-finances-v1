#!/bin/bash
set -e

source ./commons.sh

JQ="$(resolve_jq)"
SEED_JSON_ABS="$(abs_path "$SEED_JSON")"

GENERATED_DIR_ABS="$(abs_path "$GENERATED_DIR/.")"
OUTPUT_FILE="$GENERATED_DIR_ABS/$GENERATED_MONGOSH_FILE"

print_title "Generate mongosh script"

./validate-seed-json.sh

mkdir -p "$GENERATED_DIR_ABS"

DICTIONARIES_JSON="$("$JQ" -c '.dictionaries' "$SEED_JSON_ABS")"
TEMPLATES_JSON="$("$JQ" -c '.templates' "$SEED_JSON_ABS")"

cat > "$OUTPUT_FILE" <<EOF
(function () {
  const COLLECTION_NAME = "$MONGO_COLLECTION";

  const DICTIONARIES = $DICTIONARIES_JSON;

  const templates = $TEMPLATES_JSON;

  const collection = db.getCollection(COLLECTION_NAME);

  function resolveDictionaryReferences(template) {
    if (!template.extraction || !template.extraction.date) {
      return template;
    }

    const date = template.extraction.date;
    const ref = date.replacementsRef;

    if (!ref) {
      return template;
    }

    const dictionary = DICTIONARIES[ref];

    if (!dictionary) {
      throw new Error("Dictionary not found: " + ref + " for template: " + template.code);
    }

    date.replacements = dictionary;
    delete date.replacementsRef;

    return template;
  }

  templates
    .map(resolveDictionaryReferences)
    .forEach((template) => {
      template.updatedAt = new Date();

      collection.updateOne(
        { code: template.code },
        {
          \$set: template,
          \$setOnInsert: {
            createdAt: new Date()
          }
        },
        { upsert: true }
      );

      print("Upserted bank receipt template: " + template.code);
    });

  print("Bank receipt templates were generated successfully.");
}());
EOF

[[ -f "$OUTPUT_FILE" ]] || fail "Generated file was not created: $OUTPUT_FILE"

echo -e "${CHECK_SYMBOL} Generated: $OUTPUT_FILE"