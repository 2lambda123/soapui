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

package com.eviware.soapui.impl.wsdl.actions.mockservice;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.analytics.Analytics;
import com.eviware.soapui.analytics.SoapUIActions;
import com.eviware.soapui.impl.rest.mock.RestMockService;
import com.eviware.soapui.impl.wsdl.mock.WsdlMockService;
import com.eviware.soapui.model.mock.MockService;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.ui.desktop.DesktopPanel;

import javax.swing.SwingUtilities;

import static com.eviware.soapui.analytics.SoapUIActions.START_REST_MOCK_FROM_NAVIGATOR;
import static com.eviware.soapui.analytics.SoapUIActions.START_SOAP_MOCK_FROM_NAVIGATOR;
import static com.eviware.soapui.analytics.SoapUIActions.STOP_REST_MOCK_FROM_NAVIGATOR;
import static com.eviware.soapui.analytics.SoapUIActions.STOP_SOAP_MOCK_FROM_NAVIGATOR;

/**
 * Clones a WsdlMockService
 *
 * @author Ole.Matzura
 */

public class StartMinimizedMockServiceAction<MockServiceType extends MockService>
        extends AbstractSoapUIAction<MockServiceType> {
    public final static String SOAPUI_ACTION_ID = "StartMinimizedMockServiceAction";

    public StartMinimizedMockServiceAction() {
        super("Start Minimized", "Starts this MockService and minimizes its desktop window");
    }

    public void perform(MockServiceType mockService, Object param) {
        try {
            UISupport.setHourglassCursor();
            final DesktopPanel desktopPanel = UISupport.showDesktopPanel(mockService);
            if (mockService.getMockRunner() == null) {
                mockService.start();
                sendAnalytic(mockService);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SoapUI.getDesktop().minimize(desktopPanel);
                }
            });
        } catch (Exception e) {
            UISupport.showErrorMessage(e);
        } finally {
            UISupport.resetCursor();
        }
    }

    private void sendAnalytic(MockServiceType mockService) {
        if (mockService != null) {
            if (mockService instanceof WsdlMockService) {
                Analytics.trackAction(START_SOAP_MOCK_FROM_NAVIGATOR);
            } else if (mockService instanceof RestMockService) {
                Analytics.trackAction(START_REST_MOCK_FROM_NAVIGATOR);
            }
        }
    }
}
