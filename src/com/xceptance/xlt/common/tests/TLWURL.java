/**  
 *  Copyright 2014 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *
 */
package com.xceptance.xlt.common.tests;

import org.junit.Test;

import com.xceptance.xlt.api.actions.AbstractLightWeightPageAction;
import com.xceptance.xlt.common.actions.LWSimpleURL;
import com.xceptance.xlt.common.util.CSVBasedURLAction;
import com.xceptance.xlt.common.util.StaticContentDownloader;
import com.xceptance.xlt.engine.XltWebClient;

/**
 * This is a simple single URL test case. It can be used to create considerable load for simple investigations. 
 * Cookie handling, as well as content comparison is handled automatically. See the properties too. 
 * 
 * This test cases uses the lightweight action programming pattern to save system resources while testing. 
 * This should enable the test to create a lot of load.
 */
public class TLWURL extends AbstractURLTestCase
{
    @Test
    public void testURLs() throws Throwable
    {
        // our action tracker to build up a correct chain of pages
        AbstractLightWeightPageAction lastAction = null; 
        StaticContentDownloader downloader = null;

        try
        {
            // let's loop about the data we have
            for (final CSVBasedURLAction csvBasedAction : csvBasedActions)
            {
                // ok, action or static?
                if (csvBasedAction.isAction())
                {
                    // if anything is left from the previous action, finish that first
                    if (downloader != null)
                    {
                        downloader.waitForCompletion();
                    }                     
                
                    if (lastAction == null)
                    {
                        // our first action, so start the browser too
                        lastAction = new LWSimpleURL(this, csvBasedAction, login, password);
                    }
                    else
                    {
                        // subsequent actions
                        lastAction = new LWSimpleURL(this, lastAction, csvBasedAction);
                    }

                    // run it
                    lastAction.run();
                }
                
                // this is the part that deals with the static downloads
                if (csvBasedAction.isStaticContent())
                {
                    if (downloader == null)
                    {
                        if (lastAction == null)
                        {
                            // we do not have any action yet, so we have to make one up
                            lastAction = new LWSimpleURL(this, csvBasedAction, login, password);
                        }
                        
                        // build a downloader only when needed
                        downloader = new StaticContentDownloader(((XltWebClient)lastAction.getWebClient()), 
                                                                 getProperty("com.xceptance.xlt.staticContent.downloadThreads", 1),
                                                                 getProperty("userAgent.UID", false));
                    }
                    downloader.addRequest(csvBasedAction.getURL(this));
                }              
            }
        }
        finally
        {
            // make sure we wait for all resources and clean up too
            if (downloader != null)
            {
                downloader.waitForCompletion();
                downloader.shutdown();
            }
        }        
    }
}
