package com.google.inject.extensions.security;

import java.util.Set;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {

    String getUsername();

    Set<Class<? extends SecurityRole>> getRoles();

  //  String getToken();

}
