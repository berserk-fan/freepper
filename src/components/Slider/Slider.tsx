import React, { ReactElement, useCallback, useState } from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import Box from "@material-ui/core/Box";
import { Dots, Numbers, SliderArrows } from "./helpers";
import { useSliderVirtualization } from "./utils";

const WINDOW_SIZE = 2;

export default function Slider({
  slides,
  className = "",
  onChange,
  isShowingArrows = true,
}: {
  slides: ReactElement[];
  className?: string;
  onChange?: (slideNum: number) => void;
  isShowingArrows?: boolean;
}) {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const slideIdx = s.details().relativeSlide;
      setCurrentSlide(slideIdx);
      onChange?.(slideIdx);
    },
  });

  const { isRendering } = useSliderVirtualization({
    currentSlide,
    virtualizationRange: WINDOW_SIZE,
    totalLength: slides.length,
  });

  const sliderPrev = useCallback(() => slider.prev(), [slider]);
  const sliderNext = useCallback(() => slider.next(), [slider]);

  return (
    <Box className={className} position="relative">
      <div ref={sliderRef as any} className="keen-slider">
        {slides.map((slide, idx) => (
          <div key={slide.key} className="keen-slider__slide">
            {isRendering(idx) ? slide : null}
          </div>
        ))}
      </div>
      {isShowingArrows && (
        <SliderArrows
          showForwardButton={currentSlide !== slides.length - 1}
          showBackButton={currentSlide !== 0}
          forwardHandler={sliderNext}
          backHandler={sliderPrev}
        />
      )}
      {slider &&
        slides.length > 1 &&
        (slides.length <= 7 ? (
          <Dots
            currentSlide={currentSlide}
            onClick={slider.moveToSlideRelative}
            size={slider.details().size}
          />
        ) : (
          <Numbers>
            {currentSlide + 1}/{slider.details().size}
          </Numbers>
        ))}
    </Box>
  );
}
