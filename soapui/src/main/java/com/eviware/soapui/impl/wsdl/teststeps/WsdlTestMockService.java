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

package com.eviware.soapui.impl.wsdl.teststeps;

import com.eviware.soapui.config.MockServiceConfig;
import com.eviware.soapui.impl.wsdl.mock.WsdlMockService;

public class WsdlTestMockService extends WsdlMockService {
    private final WsdlMockResponseTestStep mockResponseStep;

    public WsdlTestMockService(WsdlMockResponseTestStep step, MockServiceConfig config) {
        super(step.getTestCase().getTestSuite().getProject(), config);
        this.mockResponseStep = step;
    }

    public WsdlMockResponseTestStep getMockResponseStep() {
        return mockResponseStep;
    }
}
