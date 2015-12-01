package pers.futuremac.mapper;

import org.springframework.stereotype.Component;
import pers.futuremac.domain.ChatItem;
import pers.futuremac.domain.User;

import java.util.HashMap;

/**
 * Created by 前程 on 2015/10/22.
 */
@Component
public interface UserMapper {
    public User getUser();
    public void storeChatItem(HashMap<String,String> cmap);
}
