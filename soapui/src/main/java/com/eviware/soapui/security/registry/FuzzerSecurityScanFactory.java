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

package com.eviware.soapui.security.registry;

import com.eviware.soapui.config.SecurityScanConfig;
import com.eviware.soapui.impl.wsdl.teststeps.HttpTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.security.scan.AbstractSecurityScan;
import com.eviware.soapui.security.scan.FuzzerSecurityScan;

/**
 * Factory for creation Fuzzer scan
 *
 * @author nebojsa.tasic
 */

public class FuzzerSecurityScanFactory extends AbstractSecurityScanFactory {

    public FuzzerSecurityScanFactory() {
        super(FuzzerSecurityScan.TYPE, FuzzerSecurityScan.NAME, "Executes the specified fuzzer security scan",
                "/fuzzer_security_scan.gif");
    }

    @Override
    public SecurityScanConfig createNewSecurityScan(String name) {
        SecurityScanConfig securityCheckConfig = SecurityScanConfig.Factory.newInstance();
        securityCheckConfig.setType(FuzzerSecurityScan.TYPE);
        securityCheckConfig.setName(name);
        return securityCheckConfig;
    }

    @Override
    public AbstractSecurityScan buildSecurityScan(TestStep testStep, SecurityScanConfig config, ModelItem parent) {
        return new FuzzerSecurityScan(testStep, config, parent, "/fuzzer_security_scan.gif");
    }

    @Override
    public boolean canCreate(TestStep testStep) {
        return testStep instanceof WsdlTestRequestStep || testStep instanceof RestTestRequestStep
                || testStep instanceof HttpTestRequestStep;
    }
}
