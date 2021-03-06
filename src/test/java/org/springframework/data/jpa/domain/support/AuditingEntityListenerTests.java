/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.domain.support;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.sample.AuditableRole;
import org.springframework.data.jpa.domain.sample.AuditableUser;
import org.springframework.data.jpa.domain.sample.AuditorAwareStub;
import org.springframework.data.jpa.repository.sample.AuditableUserRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for {@link AuditingEntityListener}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:auditing/auditing-entity-listener.xml")
@Transactional
@DirtiesContext
public class AuditingEntityListenerTests {

    @Autowired
    AuditableUserRepository repository;

    @Autowired
    AuditorAwareStub auditorAware;

    AuditableUser user;


    @Before
    public void setUp() {

        user = new AuditableUser();
        auditorAware.setAuditor(user);

        repository.save(user);
    }


    @Test
    public void auditsRootEntityCorrectly() throws Exception {

        assertDatesSet(user);
        assertUserIsAuditor(user, user);
    }


    @Test
    public void auditsTransitiveEntitiesCorrectly() throws Exception {

        AuditableRole role = new AuditableRole();
        role.setName("ADMIN");

        user.addRole(role);
        repository.save(user);
        role = user.getRoles().iterator().next();

        assertDatesSet(user);
        assertDatesSet(role);
        assertUserIsAuditor(user, user);
        assertUserIsAuditor(user, role);
    }


    private static void assertDatesSet(Auditable<?, ?> auditable) {

        assertThat(auditable.getCreatedDate(), is(notNullValue()));
        assertThat(auditable.getLastModifiedDate(), is(notNullValue()));
    }


    private static void assertUserIsAuditor(AuditableUser user,
            Auditable<AuditableUser, ?> auditable) {

        assertThat(auditable.getCreatedBy(), is(user));
        assertThat(auditable.getLastModifiedBy(), is(user));
    }
}
