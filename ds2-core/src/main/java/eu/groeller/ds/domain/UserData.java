package eu.groeller.ds.domain;

public interface UserData extends Identifiable {
    String getUsername();
    String getEmail();
    String getKeycloakId();
}
