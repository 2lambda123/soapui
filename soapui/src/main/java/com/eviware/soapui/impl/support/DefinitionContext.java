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

package com.eviware.soapui.impl.support;

import com.eviware.soapui.impl.support.definition.InterfaceDefinition;
import com.eviware.soapui.model.iface.Interface;

public interface DefinitionContext<T extends Interface> {
    public boolean hasSchemaTypes();

    public boolean isCached();

    public T getInterface();

    public InterfaceDefinition<T> getInterfaceDefinition() throws Exception;

    public String export(String path) throws Exception;

    public boolean loadIfNecessary() throws Exception;
}
