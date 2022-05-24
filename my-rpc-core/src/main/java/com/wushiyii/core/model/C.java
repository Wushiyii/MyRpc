package com.wushiyii.core.model;

public interface C {

    String DEFAULT_SERIALIZE = "protobuf";

    String DEFAULT_LOAD_BALANCE = "random";

    Double DEFAULT_WEIGHT = 1.0D;

    /* protocol constants */
    byte PROTOCOL_MAGIC_CODE = 0x35;

    byte REQUEST_PROTOCOL_TYPE = 0;

    byte RESPONSE_PROTOCOL_TYPE = 1;

}
