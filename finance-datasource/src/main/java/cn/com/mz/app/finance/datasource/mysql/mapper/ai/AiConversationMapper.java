package cn.com.mz.app.finance.datasource.mysql.mapper.ai;

import cn.com.mz.app.finance.datasource.mysql.entity.ai.AiConversationDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 会话 Mapper
 *
 * @author mz
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationDO> {
}
