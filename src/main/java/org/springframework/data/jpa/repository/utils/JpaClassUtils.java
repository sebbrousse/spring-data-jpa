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
package org.springframework.data.jpa.repository.utils;

import javax.persistence.EntityManager;


/**
 * Utility class to work with classes.
 * 
 * @author Oliver Gierke
 */
public abstract class JpaClassUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private JpaClassUtils() {

    }


    /**
     * Returns whether the given {@link EntityManager} is of the given type.
     * 
     * @param em
     * @param type the fully qualified expected {@link EntityManager} type.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isEntityManagerOfType(EntityManager em, String type) {

        try {

            Class<? extends EntityManager> emType =
                    (Class<? extends EntityManager>) Class.forName(type);

            emType.cast(em);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
