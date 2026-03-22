package cn.com.mz.app.finance.datasource.mysql.mapper.ai;

import cn.com.mz.app.finance.datasource.mysql.entity.ai.AiMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 消息 Mapper
 *
 * @author mz
 */
@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessageDO> {
}
