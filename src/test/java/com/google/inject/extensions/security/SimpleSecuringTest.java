package com.google.inject.extensions.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.OutOfScopeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author tbaum
 * @since 23.12.2013
 */
public class SimpleSecuringTest {

    private A a;
    private SecurityScope securityScope;
    private SecurityService securityService;

    @Before
    public void cleanUp() {
        Injector injector = Guice.createInjector(new TestSecurityModule());

        a = injector.getInstance(A.class);
        securityService = injector.getInstance(SecurityService.class);
        securityScope = injector.getInstance(SecurityScope.class);
    }

    @Test(expected = OutOfScopeException.class)
    public void testFailingAccessNotInScope() {
        a.anyRole();
        fail();
    }

    @Test(expected = OutOfScopeException.class)
    public void testFailingAuthenticateNotInScope() {
        securityService.authenticate(new SimpleUser("Foo"));
        fail();
    }

    @Test(expected = NotLogginException.class)
    public void testNotAuthenticated() {
        try (SecurityScope ignored = securityScope.enter()) {
            a.anyRole();
            fail();
        }
    }

    @Test
    public void testAuthenticatedAny() {
        try (SecurityScope ignored = securityScope.enter()) {
            securityService.authenticate(new SimpleUser("Foo"));
            a.anyRole();
        }
    }

    @Test(expected = NotInRoleException.class)
    public void testFailingAuthenticatedSpecial() {
        try (SecurityScope ignored = securityScope.enter()) {
            securityService.authenticate(new SimpleUser("Foo"));
            a.specialRole();
            fail();
        }
    }

    @Test
    public void testAuthenticatedSpecial() {
        try (SecurityScope ignored = securityScope.enter()) {
            securityService.authenticate(new SimpleUser("Foo", SpecialRole.class));
            a.specialRole();
        }
    }

    public static class A {

        @Secured protected boolean anyRole() {
            return true;
        }

        @Secured(SpecialRole.class) protected boolean specialRole() {
            return true;
        }
    }


    public interface SpecialRole extends SecurityRole {

    }
}
