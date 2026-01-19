package com.rpc.loadbalance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoadBalanceTypeEnum {
    RANDOM("random"),
    ROUND_ROBIN("roundRobin");
    private final String value;

}
