package com.xceptance.xlt.common.actions;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.xlt.common.util.action.validation.URLActionDataExecutableResult;

public class XhrLightWeightPageAction extends LightWeightPageAction
{
    private WebResponse xhrResponse;

    public XhrLightWeightPageAction(final LightWeightPageAction previousAction,
                                    final String name,
                                    final WebRequest webRequest,
                                    final Downloader downloader)
    {
        super(previousAction, name, webRequest, downloader);
    }

    public XhrLightWeightPageAction(final String name, final WebRequest webRequest)
    {
        super(name, webRequest);
    }
    @Override
    protected void execute() throws Exception
    {
        this.xhrResponse = getWebClient().loadWebResponse(this.webRequest);
    }
    @Override
    protected void postValidate() throws Exception
    {
        result = new URLActionDataExecutableResult(xhrResponse);
    }

    @Override
    public URLActionDataExecutableResult getResult()
    {
        return this.result;
    }
}
