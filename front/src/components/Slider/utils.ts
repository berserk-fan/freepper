import { useCallback, useMemo } from "react";

export type UseSliderVirtualizationProps = {
  currentSlide: number;
  virtualizationRange: number;
  totalLength: number;
};

export function useSliderVirtualization({
  currentSlide,
  virtualizationRange,
  totalLength,
}: UseSliderVirtualizationProps) {
  const renderRange = useMemo(
    () => [
      Math.max(currentSlide - virtualizationRange, 0),
      Math.min(currentSlide + virtualizationRange, totalLength),
    ],
    [currentSlide, virtualizationRange, totalLength],
  );

  const isRendering = useCallback(
    (index: number) => renderRange[0] <= index && index < renderRange[1],
    [renderRange],
  );

  return {
    renderRange,
    isRendering,
  };
}
