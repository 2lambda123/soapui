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

package com.eviware.soapui.impl.wsdl.panels.request.actions;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.AbstractToolsAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.ArgumentBuilder;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.ProcessToolRunner;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.RunnerContext;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.ToolHost;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wsi.WSIAnalyzeAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wsi.WSIReportPanel;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.settings.WSISettings;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.types.StringToStringsMap;
import com.eviware.soapui.ui.support.DefaultDesktopPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wsI.testing.x2003.x03.analyzerConfig.AssertionResults;
import org.wsI.testing.x2003.x03.analyzerConfig.Configuration;
import org.wsI.testing.x2003.x03.analyzerConfig.ConfigurationDocument;
import org.wsI.testing.x2003.x03.analyzerConfig.LogFile;
import org.wsI.testing.x2003.x03.analyzerConfig.LogFile.CorrelationType;
import org.wsI.testing.x2003.x03.analyzerConfig.ReportFile;
import org.wsI.testing.x2003.x03.common.AddStyleSheet;
import org.wsI.testing.x2003.x03.log.Environment;
import org.wsI.testing.x2003.x03.log.HttpMessageEntry;
import org.wsI.testing.x2003.x03.log.Implementation;
import org.wsI.testing.x2003.x03.log.Log;
import org.wsI.testing.x2003.x03.log.LogDocument;
import org.wsI.testing.x2003.x03.log.MessageEntry;
import org.wsI.testing.x2003.x03.log.Monitor;
import org.wsI.testing.x2003.x03.log.NameVersionPair;
import org.wsI.testing.x2003.x03.log.TcpMessageType;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Validates the request XML of a WsdlRequest
 *
 * @author Ole.Matzura
 */

public class WSIValidateRequestAction extends AbstractToolsAction<WsdlRequest> {
    public final static Logger log = LogManager.getLogger(WSIValidateRequestAction.class);
    private String configFile;
    private File logFile;
    private String wsiDir;
    private String profile;

    public WSIValidateRequestAction() {
        super("Check WS-I Compliance", "Validates the current request/response against the WS-I Basic Profile");
    }

    protected void generate(StringToStringMap values, ToolHost toolHost, WsdlRequest modelItem) throws Exception {
        if (modelItem.getResponse() == null) {
            UISupport.showErrorMessage("Response required for WS-I validations");
            return;
        }

        wsiDir = SoapUI.getSettings().getString(WSISettings.WSI_LOCATION,
                System.getProperty(WSIAnalyzeAction.WSI_DIR_PROP_NAME, System.getenv(WSIAnalyzeAction.WSI_HOME_ENV_VAR_NAME)));
        if (wsiDir == null) {
            UISupport.showErrorMessage("WSI Test Tools directory must be set in global preferences");
            return;
        }

        if (modelItem.getAttachmentCount() > 0 || modelItem.getResponse().getAttachments().length > 0) {
            if (!UISupport.confirm("Message contains attachments which is not supported by "
                    + "validation tools, validate anyway?", "Validation Warning")) {
                return;
            }
        }

        profile = SoapUI.getSettings().getString(WSISettings.PROFILE_TYPE, WSISettings.BASIC_PROFILE_10_TAD);

        ProcessBuilder builder = new ProcessBuilder();

        File reportFile = File.createTempFile(WSIAnalyzeAction.WSI_REPORT_NAME, WSIAnalyzeAction.XML_EXTENSION);

        ArgumentBuilder args = buildArgs(reportFile, modelItem);
        builder.command(args.getArgs());
        builder.directory(new File(wsiDir));

        toolHost.run(new WSIProcessToolRunner(builder, reportFile, modelItem));
    }

    private ArgumentBuilder buildArgs(File reportFile, WsdlRequest modelItem) throws Exception {
        File logFile = buildLog(modelItem);
        File file = buildConfig(reportFile, logFile, modelItem);
        Settings settings = modelItem.getSettings();

        ArgumentBuilder builder = new ArgumentBuilder(new StringToStringMap());
        builder.startScript(wsiDir + File.separator +
                        (profile.equals(WSISettings.BASIC_PROFILE_10_TAD)?WSIAnalyzeAction.ANALYZER_V10_NAME:WSIAnalyzeAction.ANALYZER_V11_NAME),
                WSIAnalyzeAction.WIN_BATCH_FILE_EXTENSION, WSIAnalyzeAction.UNIX_BATCH_FILE_EXTENSION);
        builder.addArgs("-config", file.getAbsolutePath());

        return builder;
    }

    private File buildLog(WsdlRequest modelItem) throws Exception {
        LogDocument logDoc = LogDocument.Factory.newInstance();
        Log log = logDoc.addNewLog();
        log.setTimestamp(Calendar.getInstance());

        addMonitorConfig(log);
        addMessageConfig(log, modelItem);

        logFile = File.createTempFile("wsi-analyzer-log", ".xml");
        logDoc.save(logFile);
        return logFile;
    }

    private File buildConfig(File reportFile, File logFile, WsdlRequest modelItem) throws IOException {
        Settings settings = modelItem.getSettings();

        ConfigurationDocument configDoc = ConfigurationDocument.Factory.newInstance();
        Configuration config = configDoc.addNewConfiguration();

        config.setVerbose(settings.getBoolean(WSISettings.VERBOSE));
        AssertionResults results = config.addNewAssertionResults();
        /*results.setType(AssertionResults.Type.Enum.forString(settings.getString(WSISettings.RESULTS_TYPE,
                AssertionResults.Type.ONLY_FAILED.toString())));*/
        results.setType(AssertionResults.Type.Enum.forString(WSIAnalyzeAction.ALL_RESULT_TYPE));

        results.setMessageEntry(settings.getBoolean(WSISettings.MESSAGE_ENTRY));
        results.setFailureMessage(settings.getBoolean(WSISettings.FAILURE_MESSAGE));
        results.setAssertionDescription(settings.getBoolean(WSISettings.ASSERTION_DESCRIPTION));

        ReportFile report = config.addNewReportFile();
        report.setLocation(reportFile.getAbsolutePath());
        report.setReplace(true);

        AddStyleSheet stylesheet = report.addNewAddStyleSheet();
        stylesheet.setHref(WSIAnalyzeAction.PROFILES_DIR_RELATED_PATH + profile);
        stylesheet.setType("text/xsl");
        stylesheet.setAlternate(false);

        config.setTestAssertionsFile(WSIAnalyzeAction.PROFILES_DIR_RELATED_PATH + profile);

        LogFile logFileConfig = config.addNewLogFile();
        logFileConfig.setStringValue(logFile.getAbsolutePath());
        logFileConfig.setCorrelationType(CorrelationType.ENDPOINT);

        configFile = configDoc.toString();

        File file = File.createTempFile(WSIAnalyzeAction.WSI_ANALYZER_CONFIG, WSIAnalyzeAction.XML_EXTENSION);

        configDoc.save(file);
        return file;
    }

    private void addMessageConfig(Log log, WsdlRequest modelItem) throws MalformedURLException {
        HttpMessageEntry requestMessage = HttpMessageEntry.Factory.newInstance();
        requestMessage.addNewMessageContent().setStringValue(modelItem.getRequestContent());
        requestMessage.setConversationID("1");
        requestMessage.setTimestamp(Calendar.getInstance());
        requestMessage.setID("1");
        URL endpoint = new URL(modelItem.getEndpoint());
        requestMessage.setSenderHostAndPort("localhost");

        if (endpoint.getPort() > 0) {
            requestMessage.setReceiverHostAndPort(endpoint.getHost() + ":" + endpoint.getPort());
        } else {
            requestMessage.setReceiverHostAndPort(endpoint.getHost());
        }

        requestMessage.setType(TcpMessageType.REQUEST);

        Response response = modelItem.getResponse();
        HttpMessageEntry responseMessage = HttpMessageEntry.Factory.newInstance();
        responseMessage.addNewMessageContent().setStringValue(response.getContentAsString());
        responseMessage.setConversationID("1");
        responseMessage.setType(TcpMessageType.RESPONSE);
        responseMessage.setTimestamp(Calendar.getInstance());
        responseMessage.setID("2");
        responseMessage.setSenderHostAndPort(requestMessage.getReceiverHostAndPort());
        responseMessage.setReceiverHostAndPort(requestMessage.getSenderHostAndPort());

        String requestHeaders = buildHttpHeadersString(response.getRequestHeaders());
        requestMessage.setHttpHeaders("POST " + endpoint.getPath() + " HTTP/1.1\r\n" + requestHeaders);

        responseMessage.setHttpHeaders(buildHttpHeadersString(response.getResponseHeaders()));

        log.setMessageEntryArray(new MessageEntry[]{requestMessage, responseMessage});
    }

    private void addMonitorConfig(Log log) throws Exception {
        Monitor monitor = log.addNewMonitor();

        monitor.setVersion("1.5");
        monitor.setReleaseDate(Calendar.getInstance());

        org.wsI.testing.x2003.x03.monitorConfig.Configuration conf = monitor.addNewConfiguration();
        conf.setCleanupTimeoutSeconds(0);
        conf.setLogDuration(0);

        org.wsI.testing.x2003.x03.monitorConfig.LogFile logFileConf = conf.addNewLogFile();
        logFileConf.setLocation("report.xml");
        logFileConf.setReplace(true);

		/*
		 * ArrayOfRedirectConfig mintConf = conf.addNewManInTheMiddle();
		 * RedirectConfig redirect = mintConf.addNewRedirect();
		 * redirect.setListenPort( 9999 ); redirect.setMaxConnections( 10 );
		 * redirect.setReadTimeoutSeconds( 10 );
		 * 
		 * URL endpoint = new URL( modelItem.getEndpoint()); if(
		 * endpoint.getPort() > 0 ) redirect.setSchemeAndHostPort(
		 * endpoint.getHost() + ":" + endpoint.getPort()); else
		 * redirect.setSchemeAndHostPort( endpoint.getHost() );
		 */

        Environment env = monitor.addNewEnvironment();
        NameVersionPair osConf = env.addNewOperatingSystem();
        osConf.setName("Windows");
        osConf.setVersion("2003");

        NameVersionPair rtConf = env.addNewRuntime();
        rtConf.setName("java");
        rtConf.setVersion("1.5");

        NameVersionPair xpConf = env.addNewXmlParser();
        xpConf.setName("xmlbeans");
        xpConf.setVersion("2.1.0");

        Implementation implConf = monitor.addNewImplementer();
        implConf.setName("soapui");
        implConf.setLocation("here");
    }

    private String buildHttpHeadersString(StringToStringsMap headers) {
        StringBuffer buffer = new StringBuffer();

        if (headers.containsKey("#status#")) {
            buffer.append(headers.get("#status#")).append("\r\n");
        }

        for (String header : headers.keySet()) {
            if (!header.equals("#status#")) {
                for (String value : headers.get(header)) {
                    buffer.append(header).append(": ").append(value).append("\r\n");
                }
            }
        }

        return buffer.toString();
    }

    private class WSIProcessToolRunner extends ProcessToolRunner {
        private final File reportFile;
        private final WsdlRequest modelItem;

        public WSIProcessToolRunner(ProcessBuilder builder, File reportFile, WsdlRequest modelItem) {
            super(builder, "WSI Message Validation", modelItem);
            this.reportFile = reportFile;
            this.modelItem = modelItem;
        }

        public String getDescription() {
            return "Running WSI Analysis tools...";
        }

        protected void afterRun(int exitCode, RunnerContext context) {
            if (exitCode == 0 && context.getStatus() == RunnerContext.RunnerStatus.FINISHED) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        try {
                            WSIReportPanel panel = new WSIReportPanel(WSIAnalyzeAction.transformReport(reportFile),
                                    configFile, logFile, true);
                            panel.setPreferredSize(new Dimension(600, 400));

                            UISupport.showDesktopPanel(new DefaultDesktopPanel("WS-I Report",
                                    "WS-I Report for validation of messages in request [" + modelItem.getName() + "]", panel));
                        } catch (Exception e) {
                            UISupport.showErrorMessage(e);
                        }
                    }
                });
            } else {
                ProcessBuilder processBuilder = getBuilders()[0];
                List<String> programAndArgs = processBuilder.command();
                log.error("WSI checking failed. Exit code " + new Integer(exitCode).toString() + ". Command line: " + getCommandDetails(programAndArgs));
            }
        }

        private String getCommandDetails (List<String> command){
            String str = "";
            for (String entity: command){
                str += entity + " ";
            }

            return str;
        }

        public boolean showLog() {
            return modelItem.getSettings().getBoolean(WSISettings.SHOW_LOG);
        }

        @Override
        protected void beforeProcess(ProcessBuilder processBuilder, RunnerContext context) {
            super.beforeProcess(processBuilder, context);
            processBuilder.environment().put(WSIAnalyzeAction.WSI_HOME_ENV_VAR_NAME, wsiDir);
        }
    }
}
