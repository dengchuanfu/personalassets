package com.kunkunyu.personalassets.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import com.kunkunyu.personalassets.PersonalAssetGroup;

/**
 * A service for {@link PersonalAssetGroup}.
 *
 */
public interface PersonalAssetGroupService {
    
    /**
     * List personalAsset groups.
     *
     * @param request request
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAssetGroup>> listPersonalAssetGroup(QueryListRequest request);
    
    /**
     * Create a personalAsset group.
     *
     * @param name name
     * @return a mono of personalAsset group
     */
    Mono<PersonalAssetGroup> deletePersonalAssetGroup(String name);
    
}
