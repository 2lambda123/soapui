/*
 * Copyright 2004-2022 SmartBear Software
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

package soapui.demo;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestRunContext;
import com.eviware.soapui.model.testsuite.TestRunner;

public class DemoListener extends TestRunListenerAdapter
{
	private long startTime;

	public void beforeRun( TestRunner testRunner, TestRunContext runContext )
	{
		startTime = System.nanoTime();
	}
	
	public void afterRun( TestRunner testRunner, TestRunContext runContext )
	{
		long endTime = System.nanoTime();
		SoapUI.log.info( "TestCase [" + testRunner.getTestCase().getName() + "] took " + (endTime-startTime) + " nanoseconds." );
	}
}
