package com.hyeonho.linkedmap.marker;

import com.hyeonho.linkedmap.marker.marker.CreateMarkerDTO;

import java.util.List;

public interface MarkerQueryRepository {
    List<CreateMarkerDTO> getMarkerList(Long memberId, Long roomId);
}
