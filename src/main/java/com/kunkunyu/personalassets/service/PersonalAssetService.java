package com.kunkunyu.personalassets.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import com.kunkunyu.personalassets.PersonalAsset;
import com.kunkunyu.personalassets.PersonalAssetQuery;

/**
 * A service for {@link PersonalAsset}.
 *
 */
public interface PersonalAssetService {
    
    /**
     * List personalAsset.
     *
     * @param query query
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAsset>> listPersonalAsset(PersonalAssetQuery query);
}
