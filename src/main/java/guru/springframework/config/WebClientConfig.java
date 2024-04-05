package guru.springframework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig implements WebClientCustomizer {
    private final String roolUrl;

    private final ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager;

    public WebClientConfig(@Value("${webclient.root-url}") String roolUrl,
                           ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager) {
        this.roolUrl = roolUrl;
        this.reactiveOAuth2AuthorizedClientManager = reactiveOAuth2AuthorizedClientManager;
    }

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(reactiveOAuth2AuthorizedClientManager);

        oauth.setDefaultClientRegistrationId("springauth");

        webClientBuilder.filter(oauth).baseUrl(roolUrl);
    }
}
