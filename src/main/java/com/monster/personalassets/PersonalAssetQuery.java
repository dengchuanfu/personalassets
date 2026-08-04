package com.monster.personalassets;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static run.halo.app.extension.router.QueryParamBuildUtil.sortParameter;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import run.halo.app.extension.router.IListRequest;
import run.halo.app.extension.router.SortableRequest;

/**
 * A query object for {@link PersonalAsset} list.
 *
 */
public class PersonalAssetQuery extends SortableRequest {

    public PersonalAssetQuery(ServerWebExchange exchange) {
        super(exchange);
    }

    @Schema(description = "PersonalAssets filtered by group.")
    public String getGroup() {
        return queryParams.getFirst("group");
    }

    @Nullable
    @Schema(description = "PersonalAssets filtered by keyword.")
    public String getKeyword() {
        return queryParams.getFirst("keyword");
    }

    public static void buildParameters(Builder builder) {
        IListRequest.buildParameters(builder);
        builder.parameter(sortParameter())
            .parameter(parameterBuilder()
                .in(ParameterIn.QUERY)
                .name("keyword")
                .description("PersonalAssets filtered by keyword.")
                .implementation(String.class)
                .required(false))
            .parameter(parameterBuilder()
                .in(ParameterIn.QUERY)
                .name("group")
                .description("personalAsset group name")
                .implementation(String.class)
                .required(false));
    }
}
