package com.old.silence.webmvc.test.restdocs;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * @author moryzang
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public class SecurityDocumentations extends Documentations {

    @Override
    protected ResultActions documentRequest(MockHttpServletRequestBuilder builder, Snippet... snippets) throws
            Exception {
        return documentRequest(builder, (UserDetails) null, snippets);
    }
    protected  ResultActions documentRequest(MockHttpServletRequestBuilder builder, UserDetails userDetails,
                                             Snippet... snippets) throws Exception{
        if (userDetails != null) {
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        String accessToken = "Bearer eee";
        builder.header(HttpHeaders.AUTHORIZATION, accessToken);
        HeaderDescriptor authorizationHeader = header(HttpHeaders.AUTHORIZATION, StringUtils.abbreviate(accessToken, 50));
        RequestHeadersSnippet requestHeadersSnippet = HeaderDocumentation.requestHeaders(authorizationHeader);

        return super.documentRequest(builder, ArrayUtils.add(snippets, requestHeadersSnippet));
    }
}
