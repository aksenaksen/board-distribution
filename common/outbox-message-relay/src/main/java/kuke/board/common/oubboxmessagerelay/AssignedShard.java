package kuke.board.common.oubboxmessagerelay;

import lombok.Getter;

import java.util.List;
import java.util.stream.LongStream;

@Getter
public class AssignedShard {

    public List<Long> shards;

    public static AssignedShard of(String appId, List<String> appIds, Long shardCount) {
        AssignedShard assignedShard = new AssignedShard();
        assignedShard.shards = assign(appId, appIds, shardCount);
        return assignedShard;
    }

    private static List<Long> assign(String appId, List<String> appIds, Long shardCount) {
        int appIndex = findAppIndex(appId, appIds);
        if(appIndex == -1) {
            return List.of();
        }
        long start = appIndex * shardCount / appIds.size();
        long end = (appIndex + 1) * shardCount / appIds.size();

        return LongStream.rangeClosed(start,end).boxed().toList();
    }

    private static int findAppIndex(String appId, List<String> appIds) {
        for(int i=0; i< appIds.size(); i++) {
            if(appId.equals(appIds.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
