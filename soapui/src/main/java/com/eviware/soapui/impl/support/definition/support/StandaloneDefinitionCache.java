/*
 * SoapUI, Copyright (C) 2004-2022 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence"); 
 * You may not use this work except in compliance with the Licence. 
 * You may obtain a copy of the Licence at: 
 * 
 * http://ec.europa.eu/idabc/eupl 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is 
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the Licence for the specific language governing permissions and limitations 
 * under the Licence. 
 */

package com.eviware.soapui.impl.support.definition.support;

import com.eviware.soapui.config.DefinitionCacheConfig;
import com.eviware.soapui.impl.support.AbstractInterface;

public class StandaloneDefinitionCache<T extends AbstractInterface<?>> extends AbstractDefinitionCache<T> {
    public StandaloneDefinitionCache() {
        super(DefinitionCacheConfig.Factory.newInstance(), null);
    }

    protected DefinitionCacheConfig reinit(T owner) {
        return DefinitionCacheConfig.Factory.newInstance();
    }
}
