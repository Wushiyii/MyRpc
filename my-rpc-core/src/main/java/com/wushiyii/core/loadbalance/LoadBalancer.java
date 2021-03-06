package com.wushiyii.core.loadbalance;


import com.wushiyii.core.model.NodeInfo;

import java.util.List;

public interface LoadBalancer {

    String type();

    NodeInfo select(List<NodeInfo> nodeList);

}
