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
package org.springframework.data.jpa.repository.query;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.query.JpaQueryExecution.CollectionExecution;
import org.springframework.data.jpa.repository.query.JpaQueryExecution.ModifyingExecution;
import org.springframework.data.jpa.repository.query.JpaQueryExecution.PagedExecution;
import org.springframework.data.jpa.repository.query.JpaQueryExecution.SingleEntityExecution;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;


/**
 * Abstract base class to implement {@link RepositoryQuery}s.
 * 
 * @author Oliver Gierke
 */
public abstract class AbstractJpaQuery implements RepositoryQuery {

    private final JpaQueryMethod method;
    private final EntityManager em;


    /**
     * Creates a new {@link AbstractJpaQuery} from the given
     * {@link JpaQueryMethod}.
     * 
     * @param method
     * @param em
     */
    public AbstractJpaQuery(JpaQueryMethod method, EntityManager em) {

        Assert.notNull(method);
        Assert.notNull(em);

        this.method = method;
        this.em = em;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.repository.query.RepositoryQuery#getQueryMethod
     * ()
     */
    public QueryMethod getQueryMethod() {

        return method;
    }


    /**
     * @return the em
     */
    protected EntityManager getEntityManager() {

        return em;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.repository.query.RepositoryQuery#execute(java
     * .lang.Object[])
     */
    public Object execute(Object[] parameters) {

        return doExecute(getExecution(), parameters);
    }


    /**
     * @param execution
     * @param values
     * @return
     */
    private Object doExecute(JpaQueryExecution execution, Object[] values) {

        return execution.execute(this, values);
    }


    protected JpaQueryExecution getExecution() {

        switch (method.getType()) {

        case COLLECTION:
            return new CollectionExecution();
        case PAGING:
            return new PagedExecution(method.getParameters());
        case MODIFYING:
            return method.getClearAutomatically() ? new ModifyingExecution(
                    method, em) : new ModifyingExecution(method, null);
        default:
            return new SingleEntityExecution();
        }
    }


    protected ParameterBinder createBinder(Object[] values) {

        return new ParameterBinder(getQueryMethod().getParameters(), values);
    }


    protected abstract Query createQuery(Object[] values);


    protected abstract Query createCountQuery(Object[] values);
}