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

package com.eviware.soapui.support.action;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.PropertyChangeNotifier;

/**
 * An action for a ModelItem
 *
 * @author ole.matzura
 */

public interface SoapUIAction<T extends ModelItem> extends PropertyChangeNotifier {
    public final static String ENABLED_PROPERTY = SoapUIAction.class.getName() + "@enabled";

    public void perform(T target, Object param);

    public String getId();

    public String getName();

    public String getDescription();

    public boolean isEnabled();

    public boolean isDefault();

    public boolean applies(T target);
}
