package org.jlibrary.test.security;

import java.util.Collection;

import org.jlibrary.test.AbstractRepositoryTest;

/**
 * Test for finding roles.
 * 
 * @author mpermar
 *
 */
public class FindRolTest extends AbstractRepositoryTest {

	public void testFindAllRoles() {
		
		try {
			Collection roles = securityService.findAllRoles(testTicket);
			assertNotNull(roles);
			assertTrue(roles.size() == 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
