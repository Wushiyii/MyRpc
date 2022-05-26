package com.wushiyii.core.registry;

import com.wushiyii.core.model.ProviderInfo;
import com.wushiyii.core.model.NodeInfo;

import java.util.List;

public interface Registry {

    void registerProvider(ProviderInfo providerInfo);

    void subscribeProvider(String providerName);

    List<NodeInfo> getNodeListByProviderName(String providerName);

}
