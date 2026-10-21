package com.scratch.models;

import java.net.URI;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class StatusResponse {
    private String status;
    private URI outputUri;
    private URI errorUri;
}
