package com.sportifyindia.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String FACILITY_OWNER = "ROLE_FACILITY_OWNER";

    public static final String FACILITY_ADMIN = "ROLE_FACILITY_ADMIN";

    public static final String TRAINER = "ROLE_TRAINER";

    public static final String SALES_PERSON = "ROLE_SALES_PERSON";

    private AuthoritiesConstants() {}
}
