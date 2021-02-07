import React from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import styles from "./Slider.module.css";
import { Box } from "@material-ui/core";

export default function Slider({
  slides,
  className = "",
  onChange
}: {
  slides: any[];
  className?: string;
  onChange?: (slideNum: number) => void
}) {
  const [currentSlide, setCurrentSlide] = React.useState(0);
  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const slideIdx = s.details().relativeSlide;
      setCurrentSlide(slideIdx);
      if(onChange) {
        onChange(slideIdx);
      }
    },
  });

  React.useEffect(() => {}, [slides]);

  return (
    <div className={className}>
      <div className={`${styles["navigation-wrapper"]}`}>
        <div ref={sliderRef as any} className="keen-slider">
          {slides.map((slide) => (
            <div className="keen-slider__slide">{slide}</div>
          ))}
        </div>
        {slider && slides.length > 1 && (
          <Box
            className={`${styles.dotsBox} w-full flex justify-center absolute`}
          >
            <div className={styles.dots}>
              {[...Array(slider.details().size).keys()].map((idx) => {
                return (
                  <button
                    key={idx}
                    onClick={() => {
                      slider.moveToSlideRelative(idx);
                    }}
                    className={`${styles.dot} ${
                      currentSlide === idx ? styles.active : ""
                    }`}
                  />
                );
              })}
            </div>
          </Box>
        )}
      </div>
    </div>
  );
}
