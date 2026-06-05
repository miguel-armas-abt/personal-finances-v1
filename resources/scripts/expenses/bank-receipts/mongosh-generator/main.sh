#!/bin/bash
set -e

source ./commons.sh

: > "$LOG_FILE"
exec > >(tee -a "$LOG_FILE") 2>&1

print_menu_title() {
  echo -e "\n########## ${CYAN} Bank Receipt Template Generator ${NC}##########\n"
}

script_caller() {
  $1
  print_menu_title
}

print_menu_title

options=(
  "Validate seed JSON"
  "Generate mongosh script"
  "Exit"
)

while true; do
  select option in "${options[@]}"; do
    case $REPLY in
      1) script_caller "./validate-seed-json.sh"; break ;;
      2) script_caller "./generate-mongosh-script.sh"; break ;;
      3) exit ;;
      *) echo -e "${RED}Invalid option${NC}" >&2 ;;
    esac
  done

  sed -i -r "$ANSI_REGEX" "$LOG_FILE" || true
done