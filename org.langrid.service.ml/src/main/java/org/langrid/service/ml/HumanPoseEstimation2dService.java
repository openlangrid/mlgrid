package org.langrid.service.ml;

public interface HumanPoseEstimation2dService {
	HumanPoseEstimation2dResult[] estimate(
			String format, byte[] image, double threashold, int maxResults);
}
