package com.kunkunyu.personalassets;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;
import com.kunkunyu.personalassets.service.PersonalAssetService;

/**
 * A custom endpoint for {@link PersonalAsset}.
 *
 */
@Component
@RequiredArgsConstructor
public class PersonalAssetEndpoint implements CustomEndpoint {

    private final PersonalAssetService personalAssetService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = PersonalAssetResourceNames.CONSOLE_API_VERSION + "/PersonalAsset";
        return route()
            .GET(PersonalAssetResourceNames.ASSETS_PLURAL, this::listPersonalAsset,
                builder -> {
                    builder.operationId("ListPersonalAssets")
                        .description("List personal assets.")
                        .tag(tag)
                        .response(responseBuilder().implementation(
                            ListResult.generateGenericClass(PersonalAsset.class)));

                    PersonalAssetQuery.buildParameters(builder);
                }
            )
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion(PersonalAssetResourceNames.CONSOLE_API_VERSION);
    }

    private Mono<ServerResponse> listPersonalAsset(ServerRequest serverRequest) {
        PersonalAssetQuery query = new PersonalAssetQuery(serverRequest.exchange());
        return personalAssetService.listPersonalAsset(query)
            .flatMap(personalAssets -> ServerResponse.ok().bodyValue(personalAssets));
    }

}
