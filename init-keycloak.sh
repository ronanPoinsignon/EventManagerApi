#!/bin/sh
set -e

until /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://keycloak:8080 \
  --realm master \
  --user "$KEYCLOAK_USER" \
  --password "$KEYCLOAK_PASSWORD"; do

  echo "Waiting login..."
  sleep 2
done

echo "Ajout des rôles au user"

/opt/keycloak/bin/kcadm.sh add-roles \
  -r event_organizer \
  --uusername service-account-pladonf_back_end \
  --cclientid realm-management \
  --rolename view-users

echo "Fin de traitement"
