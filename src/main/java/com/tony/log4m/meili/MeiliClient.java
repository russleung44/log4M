package com.tony.log4m.meili;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;

/**
 * @author TonyLeung
 * @date 2022/10/13
 */
public class MeiliClient {

    public final static Client client = new Client(new Config("http://127.0.0.1:7700", ""));

}
