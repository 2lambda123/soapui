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

package com.eviware.soapui.support.editor.inspectors.jms.header;

import com.eviware.soapui.impl.support.AbstractHttpRequest;
import com.eviware.soapui.impl.wsdl.support.jms.header.JMSHeaderContainer;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.support.editor.EditorView;
import com.eviware.soapui.support.editor.inspectors.AbstractXmlInspector;
import com.eviware.soapui.support.editor.views.xml.raw.RawXmlEditorFactory;
import com.eviware.soapui.support.editor.xml.XmlDocument;
import com.jgoodies.binding.PresentationModel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public abstract class AbstractJMSHeaderInspector extends AbstractXmlInspector {
    private JPanel mainPanel;
    private SimpleBindingForm form;
    private final JMSHeaderContainer jmsContainer;

    protected AbstractJMSHeaderInspector(JMSHeaderContainer jmsContainer) {
        super("JMS Headers", "JMS header settings and properties", true, JMSHeaderInspectorFactory.INSPECTOR_ID);
        this.jmsContainer = jmsContainer;
    }

    public JComponent getComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            form = new SimpleBindingForm(
                    new PresentationModel<AbstractHttpRequest<?>>(jmsContainer.getJMSHeaderConfig()));
            buildContent(form);
            mainPanel.add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    @Override
    public void release() {
        super.release();

        if (form != null) {
            form.getPresentationModel().release();
        }
    }

    public abstract void buildContent(SimpleBindingForm form);

    @Override
    public boolean isEnabledFor(EditorView<XmlDocument> view) {
        return !view.getViewId().equals(RawXmlEditorFactory.VIEW_ID);
    }

}
