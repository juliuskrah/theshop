package com.shoperal.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class StructuredLDData implements Serializable {
    private static final long serialVersionUID = 7245654602432992959L;
    private String id;
    private String context;
    private String name;
    private Set<URI> image;
    private String type;
    Map<String, Object> extra;
}
