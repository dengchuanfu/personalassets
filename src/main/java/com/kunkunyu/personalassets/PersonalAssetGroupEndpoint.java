package com.kunkunyu.personalassets;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;
import com.kunkunyu.personalassets.service.PersonalAssetGroupService;

/**
 * A custom endpoint for {@link PersonalAsset}.
 */
@Component
@RequiredArgsConstructor
public class PersonalAssetGroupEndpoint implements CustomEndpoint {

    private final PersonalAssetGroupService personalAssetGroupService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return route()
            .GET(LegacyResourceNames.GROUPS_PLURAL, this::listPersonalAssetGroup,
                builder -> {
                    builder.operationId("ListPersonalAssets")
                        .description("List personal asset groups.")
                        .response(responseBuilder().implementation(
                            ListResult.generateGenericClass(PersonalAssetGroup.class))
                        );
                    PersonalAssetQuery.buildParameters(builder);
                }
            )
            .DELETE(LegacyResourceNames.GROUPS_PLURAL + "/{name}", this::deletePersonalAssetGroup,
                builder -> builder.operationId("DeletePersonalAssetGroup")
                    .description("Delete personalAsset group.")
                    .parameter(parameterBuilder()
                        .name("name")
                        .in(ParameterIn.PATH)
                        .description("PersonalAsset group name")
                        .implementation(String.class)
                        .required(true)
                    )
                    .response(responseBuilder().implementation(PersonalAssetGroup.class))
            )
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion(LegacyResourceNames.CONSOLE_API_VERSION);
    }

    private Mono<ServerResponse> deletePersonalAssetGroup(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");
        if (StringUtils.isBlank(name)) {
            throw new ServerWebInputException("PersonalAsset group name must not be blank.");
        }
        return personalAssetGroupService.deletePersonalAssetGroup(name)
            .flatMap(personalAssetGroup -> ServerResponse.ok().bodyValue(personalAssetGroup));
    }

    private Mono<ServerResponse> listPersonalAssetGroup(ServerRequest serverRequest) {
        var request = new PersonalAssetQuery(serverRequest.exchange());
        return personalAssetGroupService.listPersonalAssetGroup(request)
            .flatMap(personalAssetGroups -> ServerResponse.ok().bodyValue(personalAssetGroups));
    }

}
