#!/bin/bash

source ./variables.env

ANSI_REGEX=$'s/\\x1B\\[[0-9;]*[A-Za-z]//g'

RED='\033[0;31m'
NC='\033[0m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'

CHECK_SYMBOL="\033[0;32m\xE2\x9C\x94\033[0m"
WRENCH_SYMBOL="\xE2\x9C\xA8"

function get_timestamp {
  date +"%l:%M:%S:%3N%p"
}

function print_log {
  local command=$1
  echo "$(get_timestamp) .......... $command" >&2
}

function print_title {
  local title="$1"
  local width=${2:-80}
  local color="${3:-$PURPLE}"

  local border_line
  border_line=$(printf '%*s' "$width" '' | tr ' ' '*')

  local padding=$(( (width - ${#title} - 2) / 2 ))
  local line
  line="$(printf '*%*s%s%*s*' "$padding" '' "$title" "$padding" '')"

  line="${line:0:$((width-1))}*"

  echo -e "${color}${border_line}${NC}"
  echo -e "${color}${line}${NC}"
  echo -e "${color}${border_line}${NC}"
  echo "" >&2
}

function fail {
  echo -e "${RED}$1${NC}" >&2
  exit 1
}

function resolve_jq {
  if [[ -f "$JQ_BIN" ]]; then
    echo "$JQ_BIN"
    return
  fi

  if command -v jq >/dev/null 2>&1; then
    echo "jq"
    return
  fi

  fail "jq was not found. Put jq.exe in ./bin/jq.exe or install jq in PATH."
}

function abs_path {
  local path="$1"
  local dir
  local file

  dir="$(dirname "$path")"
  file="$(basename "$path")"

  cd "$dir" >/dev/null
  echo "$(pwd)/$file"
  cd - >/dev/null
}

function ensure_dir {
  local dir="$1"
  mkdir -p "$dir"
}