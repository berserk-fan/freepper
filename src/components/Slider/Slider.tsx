import React, { useEffect, useState } from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import { Box, IconButton } from "@material-ui/core";
import { ArrowForwardIos } from "@material-ui/icons";
import { useStyles } from "./styles";
import { Dots, Numbers } from "./helpers";

const WINDOW_SIZE = 2;

export default function Slider({
  slides,
  className = "",
  onChange,
}: {
  slides: any[];
  className?: string;
  onChange?: (slideNum: number) => void;
}) {
  const classes = useStyles();
  const [currentSlide, setCurrentSlide] = React.useState(0);
  const [toLoad, setToLoad] = useState([0, 2]);

  function changeSlide(slideIdx: number) {
    setCurrentSlide(slideIdx);
    if (onChange) {
      onChange(slideIdx);
    }
  }

  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const slideIdx = s.details().relativeSlide;
      changeSlide(slideIdx);
    },
  });

  useEffect(() => {
    setToLoad([
      Math.max(currentSlide - WINDOW_SIZE, 0),
      Math.min(currentSlide + WINDOW_SIZE, slides.length),
    ]);
  }, [currentSlide]);

  function shouldLoad(idx: number) {
    return toLoad[0] <= idx && idx < toLoad[1];
  }

  return (
    <Box className={className} position="relative">
      <div ref={sliderRef as any} className="keen-slider">
        {slides.map((slide, idx) => (
          <div key={slide.key} className="keen-slider__slide">
            {shouldLoad(idx) ? slide : null}
          </div>
        ))}
      </div>
      {currentSlide !== slides.length - 1 && (
        <IconButton
          onClick={() => slider.next()}
          className={`${classes.forwardButton}`}
        >
          <ArrowForwardIos className={classes.icon} />
        </IconButton>
      )}
      {currentSlide !== 0 && (
        <IconButton
          onClick={() => slider.prev()}
          className={`${classes.backButton}`}
        >
          <ArrowForwardIos className={classes.icon} />
        </IconButton>
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
