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

package com.eviware.soapui.model.tree.nodes.support;

import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.support.TestSuiteListenerAdapter;
import com.eviware.soapui.model.testsuite.LoadTest;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestSuiteListener;
import com.eviware.soapui.support.UISupport;

/**
 * ModelItem for LoadTests node
 *
 * @author ole.matzura
 */

public class WsdlLoadTestsModelItem extends BaseTestsModelItem {
    private TestSuiteListener listener = new InternalTestSuiteListener();

    public WsdlLoadTestsModelItem(TestCase testCase) {
        super(testCase, createLabel(testCase), UISupport.createImageIcon("/loadtests.png"));

        testCase.getTestSuite().addTestSuiteListener(listener);
    }

    private static String createLabel(TestCase testCase) {
        return "Load Tests (" + testCase.getLoadTestCount() + ")";
    }

    public Settings getSettings() {
        return testCase.getSettings();
    }

    @Override
    public void release() {
        super.release();
        testCase.getTestSuite().removeTestSuiteListener(listener);
    }

    @Override
    public String getName() {
        return createLabel(testCase);
    }

    public void updateLabel() {
        setName(createLabel(testCase));
    }

    public class InternalTestSuiteListener extends TestSuiteListenerAdapter implements TestSuiteListener {
        @Override
        public void loadTestAdded(LoadTest loadTest) {
            if (loadTest.getTestCase() == testCase) {
                updateLabel();
            }
        }

        @Override
        public void loadTestRemoved(LoadTest loadTest) {
            if (loadTest.getTestCase() == testCase) {
                updateLabel();
            }
        }

        @Override
        public void testCaseRemoved(TestCase testCase) {
            if (testCase == WsdlLoadTestsModelItem.this.testCase) {
                testCase.getTestSuite().removeTestSuiteListener(listener);
            }
        }
    }
}
