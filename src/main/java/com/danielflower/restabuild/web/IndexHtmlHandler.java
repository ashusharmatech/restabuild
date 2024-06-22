package com.danielflower.restabuild.web;

import com.danielflower.restabuild.Config;
import com.danielflower.restabuild.build.BuildResult;
import io.muserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.muserver.Mutils.coalesce;

public class IndexHtmlHandler implements RouteHandler {

    private final String template;

    public IndexHtmlHandler(Config config) throws IOException {

        String version = coalesce(getClass().getPackage().getImplementationVersion(), "dev");

        StringBuilder inputBoxAttributes = new StringBuilder();
        String urlPattern = config.allowedRepoUrlPattern().pattern();
        if (!urlPattern.equals(".*")) {
            inputBoxAttributes.append(" pattern=\"").append(Mutils.htmlEncode(urlPattern)).append("\" title=\"").append(Mutils.htmlEncode(config.allowedRepoUrlValidationMessage())).append("\"");
        }
        if (!Mutils.nullOrEmpty(config.exampleURl())) {
            inputBoxAttributes.append(" placeholder=\"").append(Mutils.htmlEncode(config.exampleURl())).append("\"");
        }

        InputStream template = IndexHtmlHandler.class.getResourceAsStream("/web/index.html");
        this.template = new String(Mutils.toByteArray(template, 8192), StandardCharsets.UTF_8)
            .replace("{{buildfilename}}", BuildResult.buildFile)
            .replace("{{restabuildversion}}", version)
            .replace("{{gitUrlTextBoxAttributes}}", inputBoxAttributes.toString())

        ;
    }

    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams) {
        response.contentType(ContentTypes.TEXT_HTML_UTF8);
        response.write(template);
    }
}
