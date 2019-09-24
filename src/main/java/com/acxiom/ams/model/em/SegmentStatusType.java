package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by cldong on 12/8/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum SegmentStatusType {
    SEGMENT_NEW(1, "New"), SEGMENT_DISTRIBUTED(2, "Distributed"), SEGMENT_DISTRIBUTING(3, "Distributing"),
    SEGMENT_DISTRIBUTE_FAILED(4, "Distribute Failed"), LOOKALIKE_READY(5, "Ready"), LOOKALIKE_RUNNING(6, "Running"),
    LOOKALIKE_FAILED(7, "Failed"), LOOKALIKE_DONE(8, "Done"), CAMPAIGN_SAVED(9, "Campaign Saved"),
    CAMPAIGN_PREPARING(10, "Campaign PREPARING"), CAMPAIGN_READY(11, "Campaign Ready"),
    CAMPAIGN_DISTRIBUTED(12, "Campaign Distributed"), CAMPAIGN_DISTRIBUTE_FAILED(13, "Campaign Distribute Failed"),
    CAMPAIGN_PREPARING_FAILED(14, "Campaign Preparing Failed"), CAMPAIGN_DISTRIBUTING(15, "Campaign Distributing"),
    UNIVERSE_PROCESSING(16, "Universe Processing"), UNIVERSE_SUCCESS(17, "Universe Success"), UNIVERSE_FAILED(18, "Universe Failed"),
    UNIVERSE_UPDATING(19, "Universe Updating"), LOOKALIKE_PENDING(20, "Pending"),
    SEGMENT_RUNNING(21, "Running"), SEGMENT_FAILED(22, "Failed"),  SHARE_COMPLETED(23, "Share Completed"),
    CAMPAIGN_DISTRIBUTION_REQUESTED(24, "Campaign Distribution Requested"),
    CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED(25, "Campaign Distribution Request Rejected");
    private Integer code;
    private String value;
}
