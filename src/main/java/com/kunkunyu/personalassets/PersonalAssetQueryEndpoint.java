package com.kunkunyu.personalassets;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import com.kunkunyu.personalassets.finders.PersonalAssetPublicQueryService;
import com.kunkunyu.personalassets.vo.PersonalAssetVo;
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
import run.halo.app.plugin.ReactiveSettingFetcher;
/**
 * Public endpoint for personalAsset queries.
 */
@Component
@RequiredArgsConstructor
public class PersonalAssetQueryEndpoint implements CustomEndpoint {

    private final PersonalAssetPublicQueryService personalAssetPublicQueryService;

    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = LegacyResourceNames.PUBLIC_API_VERSION + "/PersonalAsset";
        return route()
            .GET(LegacyResourceNames.ASSETS_PLURAL, this::listPersonalAssets,
                builder -> {
                    builder.operationId("queryPersonalAssets")
                        .description("List personal assets.")
                        .tag(tag)
                        .response(responseBuilder()
                            .implementation(ListResult.generateGenericClass(PersonalAssetVo.class)));
                    PersonalAssetPublicQuery.buildParameters(builder);
                }
            )
            .GET("asset-id-options", this::getAssetIdOptions,
                builder -> builder.operationId("getAssetIdOptions")
                    .description("Get asset ID options.")
                    .tag(tag)
                    .response(responseBuilder().implementation(AssetIdOptions.class))
            )
            .build();
    }

    private Mono<ServerResponse> listPersonalAssets(ServerRequest request) {
        PersonalAssetPublicQuery query = new PersonalAssetPublicQuery(request.exchange());
        return personalAssetPublicQueryService.listPersonalAssets(query.toListOptions(), query.toPageRequest())
            .flatMap(result -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result));
    }

    private Mono<ServerResponse> getAssetIdOptions(ServerRequest request) {
        return AssetIdOptions.fetch(settingFetcher)
            .flatMap(options -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(options));
    }
    
    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion(LegacyResourceNames.PUBLIC_API_VERSION);
    }
}
