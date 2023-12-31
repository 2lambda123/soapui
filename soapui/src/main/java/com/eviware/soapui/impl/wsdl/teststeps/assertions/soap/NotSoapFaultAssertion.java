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

package com.eviware.soapui.impl.wsdl.teststeps.assertions.soap;

import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.panels.assertions.AssertionCategoryMapping;
import com.eviware.soapui.impl.wsdl.panels.assertions.AssertionListEntry;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.soap.SoapUtils;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.AbstractTestAssertionFactory;
import com.eviware.soapui.model.TestPropertyHolder;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.testsuite.Assertable;
import com.eviware.soapui.model.testsuite.AssertionError;
import com.eviware.soapui.model.testsuite.AssertionException;
import com.eviware.soapui.model.testsuite.ResponseAssertion;

/**
 * Assertion that checks that the associated WsdlTestRequests response is not a
 * SOAP Fault
 *
 * @author Ole.Matzura
 */

public class NotSoapFaultAssertion extends WsdlMessageAssertion implements ResponseAssertion {
    public static final String ID = "SOAP Fault Assertion";
    public static final String LABEL = "Not SOAP Fault";
    public static final String DESCRIPTION = "Validates that the last received message is not a SOAP Fault. Applicable to SOAP TestSteps.";

    public NotSoapFaultAssertion(TestAssertionConfig assertionConfig, Assertable assertable) {
        super(assertionConfig, assertable, false, false, false, true);
    }

    public String internalAssertResponse(MessageExchange messageExchange, SubmitContext context)
            throws AssertionException {
        String responseContent = messageExchange.getResponseContent();
        try {
            // check manually before resource intensive xpath
            SoapVersion soapVersion = ((WsdlMessageExchange) messageExchange).getOperation().getInterface()
                    .getSoapVersion();
            if (SoapUtils.isSoapFault(responseContent, soapVersion)) {
                throw new AssertionException(new AssertionError("Response is a SOAP Fault"));
            }
        } catch (Exception e) {
            throw new AssertionException(new AssertionError(e.getMessage()));
        }

        return "Response is not a SOAP Fault";
    }

    protected String internalAssertProperty(TestPropertyHolder source, String propertyName,
                                            MessageExchange messageExchange, SubmitContext context) throws AssertionException {
        return null;
    }

    @Override
    protected String internalAssertRequest(MessageExchange messageExchange, SubmitContext context)
            throws AssertionException {
        return null;
    }

    public static class Factory extends AbstractTestAssertionFactory {
        public Factory() {
            super(NotSoapFaultAssertion.ID, NotSoapFaultAssertion.LABEL, NotSoapFaultAssertion.class, WsdlRequest.class);
        }

        @Override
        public String getCategory() {
            return AssertionCategoryMapping.STATUS_CATEGORY;
        }

        @Override
        public Class<? extends WsdlMessageAssertion> getAssertionClassType() {
            return NotSoapFaultAssertion.class;
        }

        @Override
        public AssertionListEntry getAssertionListEntry() {
            return new AssertionListEntry(NotSoapFaultAssertion.ID, NotSoapFaultAssertion.LABEL,
                    NotSoapFaultAssertion.DESCRIPTION);
        }
    }
}
