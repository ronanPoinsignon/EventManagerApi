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

echo "Ajout des rôles au user $KEYCLOAK_CLIENT_ID"

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
  --rolename impersonation \

echo "Ajout de la gestion des self-registrations"

/opt/keycloak/bin/kcadm.sh update realms/"$KEYCLOAK_REALM" \
  -s registrationAllowed=true \
  -s rememberMe=true \
  -s resetPasswordAllowed=true

echo "Mise à jour du client secret ${KEYCLOAK_CLIENT_ID}"

CLIENT_UUID=$(
  /opt/keycloak/bin/kcadm.sh get clients \
    -r "${KEYCLOAK_REALM}" \
    -q clientId="${KEYCLOAK_CLIENT_ID}" \
  | sed -n 's/.*"id"[ ]*:[ ]*"\([^"]*\)".*/\1/p' \
  | head -n 1
)

/opt/keycloak/bin/kcadm.sh update clients/"$CLIENT_UUID" \
  -r "$KEYCLOAK_REALM" \
  -s secret="$KEYCLOAK_CLIENT_SECRET"

echo "Mise à jour du client secret ${DISCORD_CLIENT_ID}"

CLIENT_UUID=$(
  /opt/keycloak/bin/kcadm.sh get clients \
    -r "${KEYCLOAK_REALM}" \
    -q clientId="${DISCORD_CLIENT_ID}" \
  | sed -n 's/.*"id"[ ]*:[ ]*"\([^"]*\)".*/\1/p' \
  | head -n 1
)

/opt/keycloak/bin/kcadm.sh update clients/"$CLIENT_UUID" \
  -r "$KEYCLOAK_REALM" \
  -s secret="$DISCORD_CLIENT_SECRET"

echo "Fin de traitement"
