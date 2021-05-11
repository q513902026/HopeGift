package me.hope.core;

import me.hope.core.enums.GiftResultType;
import me.hope.core.enums.GiftType;

import java.util.List;

public class RepeatGift extends Gift{
    private final List<String> userList;
    public RepeatGift(String name,GiftType giftType, GiftResultType resultType,List<String> userList) {
        super(name,giftType, resultType);
        this.userList = userList;
    }
    @Override
    public boolean run(final String player){
        if (!userList.contains(player)){
            userList.add(player);
            return super.run(player);
        }else{
            return false;
        }
    }
    public List<String> getUserList(){
        return userList;
    }
    @Override
    public String toString() {
        return "RepeatGift{" +
                "giftType=" + giftType +
                ", resultType=" + resultType +
                ", cmds=" + cmds +
                ", userList=" + userList +
                '}';
    }
}
