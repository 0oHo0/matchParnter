package com.duu.matchPartner.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : duu
 * @data : 2023/11/8
 * @from ：https://github.com/0oHo0
 **/
@Data
public class TeamDeleteRequest implements Serializable {
    private static final long serialVersionUID = 1726625520380957412L;

    // id
    private Long id;
}
