package app.back.dto;

import java.util.List;
import java.util.UUID;

public class KeycloakUser {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private boolean enabled;
    private boolean totp;
    private List<String> disableableCredentialTypes;
    private List<String> requiredActions;
    private long notBefore;
    private Access access;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTotp() {
        return totp;
    }

    public void setTotp(boolean totp) {
        this.totp = totp;
    }

    public List<String> getDisableableCredentialTypes() {
        return disableableCredentialTypes;
    }

    public void setDisableableCredentialTypes(List<String> disableableCredentialTypes) {
        this.disableableCredentialTypes = disableableCredentialTypes;
    }

    public List<String> getRequiredActions() {
        return requiredActions;
    }

    public void setRequiredActions(List<String> requiredActions) {
        this.requiredActions = requiredActions;
    }

    public long getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(long notBefore) {
        this.notBefore = notBefore;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public static class Access {
        private boolean manageGroupMembership;
        private boolean view;
        private boolean mapRoles;
        private boolean impersonate;
        private boolean manage;

        public boolean isManageGroupMembership() {
            return manageGroupMembership;
        }

        public void setManageGroupMembership(boolean manageGroupMembership) {
            this.manageGroupMembership = manageGroupMembership;
        }

        public boolean isView() {
            return view;
        }

        public void setView(boolean view) {
            this.view = view;
        }

        public boolean isMapRoles() {
            return mapRoles;
        }

        public void setMapRoles(boolean mapRoles) {
            this.mapRoles = mapRoles;
        }

        public boolean isImpersonate() {
            return impersonate;
        }

        public void setImpersonate(boolean impersonate) {
            this.impersonate = impersonate;
        }

        public boolean isManage() {
            return manage;
        }

        public void setManage(boolean manage) {
            this.manage = manage;
        }
    }
}
