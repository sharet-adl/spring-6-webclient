package guru.springframework.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.model.BeerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class BeerClient implements IBeerClient {
    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerID}";
    private final WebClient webClient;

    public BeerClient(WebClient.Builder webClientBuilder) {
        //this.webClient = webClientBuilder.baseUrl("http://localhost:8092").build(); // .baseUrl() -> used the WebClientConfigurator instead
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<String> listBeer() {
        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(String.class);
    }

    @Override
    public Flux<Map> listBeerMap() {
        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(Map.class);
    }

    @Override
    public Flux<JsonNode> listBeersJsonNode() {
        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<BeerDTO> listBeerDtos() {
        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(BeerDTO.class); // ask Jackson to deserialize into a POJO
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
                .retrieve().bodyToMono(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getBeerByBeerStyle(String beerStyle) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH).queryParam("beerStyle", beerStyle).build())
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO newDto) {
        return webClient.post()
                .uri(BEER_PATH)
                .body(Mono.just(newDto), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity ->
                    Mono.just(voidResponseEntity.getHeaders().get("Location").get(0)))
                    .map(path -> path.split("/")[path.split("/").length-1])
                .flatMap(this::getBeerById);

    }

    @Override
    public Mono<BeerDTO> updateBeer(BeerDTO beerDto) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(beerDto.getId()))
                .body(Mono.just(beerDto), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerDto.getId()));
    }

    @Override
    public Mono<BeerDTO> patchBeer(BeerDTO dto) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(dto.getId()))
                .body(Mono.just(dto), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(dto.getId()));
    }

    @Override
    public Mono<Void> deleteBeer(BeerDTO dto) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(dto.getId()))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
