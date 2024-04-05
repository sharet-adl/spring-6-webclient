package guru.springframework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig implements WebClientCustomizer {
    private final String roolUrl;

    public WebClientConfig(@Value("${webclient.root-url}") String roolUrl) {
        this.roolUrl = roolUrl;
    }

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.baseUrl(roolUrl);
    }
}
