package ru.otus.fitstore.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.fitstore.domain.SegmentInfo;

import java.util.List;

@FeignClient(name = "${segment.service.name}", url = "${segment.service.url}")
public interface SegmentFinderProxy {

	@GetMapping(value = "/segment/id/{id}")
	List<SegmentInfo> findSegments(@PathVariable("id") String id);
}
