package com.kyd.springmachine.msg;

/**
 * 心跳包协议1002
 * @author 8015
 *
 */
public class RootHeart {
    /**
     * timestamp : 12327378872
     */

    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
