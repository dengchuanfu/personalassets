package com.kunkunyu.personalassets;

import com.kunkunyu.personalassets.finders.PersonalAssetPublicQueryService;
import com.kunkunyu.personalassets.vo.PersonalAssetGroupVo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;


@Component
@RequiredArgsConstructor
public class PersonalAssetGroupQueryEndpoint implements CustomEndpoint {

    private final PersonalAssetPublicQueryService personalAssetPublicQueryService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = PersonalAssetResourceNames.PUBLIC_API_VERSION + "/PersonalAssetGroup";
        return route()
            .GET(PersonalAssetResourceNames.GROUPS_PLURAL, this::listGroups,
                builder -> {
                    builder.operationId("queryPersonalAssetGroups")
                        .description("List personal asset groups.")
                        .tag(tag)
                        .response(responseBuilder().implementation(
                            ListResult.generateGenericClass(PersonalAssetGroupVo.class))
                        );
                }
            )
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion(PersonalAssetResourceNames.PUBLIC_API_VERSION);
    }

    private Mono<ServerResponse> listGroups(ServerRequest request) {
        PersonalAssetPublicQuery query = new PersonalAssetPublicQuery(request.exchange());
        return personalAssetPublicQueryService.listGroups(query.toListOptions(), query.toPageRequest())
            .flatMap(result -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result));
    }

}
