package com.duu.matchPartner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.matchPartner.mapper.TagMapper;
import com.duu.matchPartner.model.domain.Tag;
import com.duu.matchPartner.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 47228
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-10-22 16:08:35
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




