#!/bin/bash
set -e

source ./commons.sh

JQ="$(resolve_jq)"
SEED_JSON_ABS="$(abs_path "$SEED_JSON")"

print_title "Validate bank receipt seed JSON"

print_log "Seed file: $SEED_JSON_ABS"
print_log "jq binary: $JQ"

[[ -f "$SEED_JSON_ABS" ]] || fail "Seed JSON file does not exist: $SEED_JSON_ABS"

if ! "$JQ" empty "$SEED_JSON_ABS" >/dev/null 2>&1; then
  fail "Seed JSON has invalid JSON syntax: $SEED_JSON_ABS"
fi

if ! "$JQ" -e '
  (.dictionaries | type == "object")
' "$SEED_JSON_ABS" >/dev/null; then
  fail "Seed JSON must contain an object field: dictionaries"
fi

if ! "$JQ" -e '
  (.templates | type == "array")
' "$SEED_JSON_ABS" >/dev/null; then
  fail "Seed JSON must contain an array field: templates"
fi

MISSING_REFS="$("$JQ" -r '
  . as $root
  | [
      $root.templates[]
      | select(.extraction.date.replacementsRef? != null)
      | select($root.dictionaries[.extraction.date.replacementsRef] == null)
      | {
          code: .code,
          replacementsRef: .extraction.date.replacementsRef
        }
    ]
  | .[]
  | "template=" + .code + ", replacementsRef=" + .replacementsRef
' "$SEED_JSON_ABS")"

if [[ -n "$MISSING_REFS" ]]; then
  echo -e "${RED}Missing dictionary references:${NC}" >&2
  echo "$MISSING_REFS" >&2
  exit 1
fi

DUPLICATED_CODES="$("$JQ" -r '
  .templates
  | group_by(.code)
  | map(select(length > 1))
  | .[]
  | .[0].code
' "$SEED_JSON_ABS")"

if [[ -n "$DUPLICATED_CODES" ]]; then
  echo -e "${RED}Duplicated template codes:${NC}" >&2
  echo "$DUPLICATED_CODES" >&2
  exit 1
fi

EMPTY_CODES="$("$JQ" -r '
  .templates[]
  | select(.code == null or (.code | tostring | length == 0))
  | @json
' "$SEED_JSON_ABS")"

if [[ -n "$EMPTY_CODES" ]]; then
  echo -e "${RED}There are templates without code:${NC}" >&2
  echo "$EMPTY_CODES" >&2
  exit 1
fi

echo -e "${CHECK_SYMBOL} Seed JSON is valid"