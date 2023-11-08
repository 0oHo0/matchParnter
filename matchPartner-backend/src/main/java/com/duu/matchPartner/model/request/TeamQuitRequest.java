package com.duu.matchPartner.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : duu
 * @data : 2023/11/7
 * @from ：https://github.com/0oHo0
 **/
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 5345091945632559581L;

    /**
     * id
     */
    private Long teamId;
}
