#!/bin/bash
set -e

until /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://keycloak:"$KEYCLOAK_PORT" \
  --realm master \
  --user "$KEYCLOAK_USER" \
  --password "$KEYCLOAK_PASSWORD"; do

  echo "Waiting login..."
  sleep 2
done

echo "Ajout des rôles au user"

/opt/keycloak/bin/kcadm.sh add-roles \
  -r "$KEYCLOAK_REALM" \
  --uusername service-account-"$KEYCLOAK_CLIENT_ID"\
  --cclientid realm-management \
  --rolename view-users \
  --rolename query-users \
  --rolename manage-users \
  --rolename view-realm \
  --rolename create-client \
  --rolename manage-clients \
  --rolename query-clients \
  --rolename view-clients \

echo "Fin de traitement"
