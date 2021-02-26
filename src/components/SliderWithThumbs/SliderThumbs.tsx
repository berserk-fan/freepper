import React, { ReactElement, useEffect, useRef, useState } from "react";
import KeenSlider, { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import { Box, IconButton } from "@material-ui/core";
import { ArrowForwardIos } from "@material-ui/icons";
import { useStyles } from "./styles";

const THUMBSER_SLIDES_AMOUNT_PER_VIEW = 7;

const nolock = (lock: { current: number }): boolean => lock.current === -1;
const lockEnded = (lock: { current: number }, idx: number): boolean =>
  lock.current === idx;

export default function SliderThumbs({
  slides,
  thumbs,
  className = "",
}: {
  slides: ReactElement[];
  className?: string;
  thumbs: ReactElement[];
}) {
  const classes = useStyles();
  const thumbserDirRef = useRef<KeenSlider>(null);
  const [activeSlide, setActiveSlide] = useState(0);
  const [thumserSlide, setThumbserSlide] = useState(0);
  const lock = useRef(-1);
  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const idx = s.details().relativeSlide;
      const thumbser = thumbserDirRef.current;
      setActiveSlide(idx);

      if (nolock(lock)) {
        thumbser?.moveToSlideRelative(idx);
      }

      if (lockEnded(lock, idx)) {
        lock.current = -1;
      }
    },
  });

  const [thumbsRef, thumbser] = useKeenSlider({
    spacing: 4,
    initial: 0,
    slidesPerView: THUMBSER_SLIDES_AMOUNT_PER_VIEW,
    duration: 400,
    slideChanged(sliderInstance) {
      const idx = sliderInstance.details().relativeSlide;
      setThumbserSlide(idx);
    },
    // centered: true, // I'd rather turned it off...
  });

  function changeSlide(idx) {
    lock.current = idx;
    slider.moveToSlideRelative(idx);
  }

  useEffect(() => {
    slider?.refresh();
    thumbser?.refresh();
    thumbser?.moveToSlide(0);
    setActiveSlide(0);
  }, [slides, thumbs]);

  useEffect(() => {
    thumbserDirRef.current = thumbser;
  }, [thumbser]);

  console.log(thumserSlide);

  return (
    <div className={className}>
      <div className={`${classes.navigationContainer}`}>
        <Box position="relative">
          <div ref={sliderRef as any} className="keen-slider">
            {slides.map((slide) => (
              <div key={slide.key} className="keen-slider__slide">
                {slide}
              </div>
            ))}
          </div>
          {activeSlide !== thumbs.length - 1 && (
            <IconButton
              onClick={() => slider.next()}
              className={`${classes.forwardButton} ${classes.bigButton}`}
            >
              <ArrowForwardIos className={classes.icon} />
            </IconButton>
          )}
          {activeSlide !== 0 && (
            <IconButton
              onClick={() => slider.prev()}
              className={`${classes.backButton} ${classes.bigButton}`}
            >
              <ArrowForwardIos className={classes.icon} />
            </IconButton>
          )}
        </Box>
        <Box position="relative">
          <div
            ref={thumbsRef as any}
            className={`keen-slider ${classes.thumbSlide}`}
          >
            {thumbs.map((thumb, idx) => (
              <Box
                key={thumb.key}
                onClick={() => changeSlide(idx)}
                className={`${classes.thumb} ${
                  activeSlide === idx ? classes.thumbActive : ""
                } keen-slider__slide`}
              >
                {thumb}
              </Box>
            ))}
            {thumbs.length < THUMBSER_SLIDES_AMOUNT_PER_VIEW && // КОСТЫЛИК ))
              new Array(THUMBSER_SLIDES_AMOUNT_PER_VIEW - thumbs.length)
                .fill(1)
                .map((el, index) => index)
                .map((element) => (
                  <Box key={element} className="keen-slider__slide" />
                ))}
          </div>
          {thumserSlide + THUMBSER_SLIDES_AMOUNT_PER_VIEW < thumbs.length && (
            <IconButton
              onClick={() => thumbser.next()}
              className={`${classes.forwardButton} ${classes.smallButton}`}
            >
              <ArrowForwardIos className={classes.smallIcon} />
            </IconButton>
          )}
          {thumserSlide !== 0 && (
            <IconButton
              onClick={() => thumbser.prev()}
              className={`${classes.backButton} ${classes.smallButton}`}
            >
              <ArrowForwardIos className={classes.smallIcon} />
            </IconButton>
          )}
        </Box>
      </div>
    </div>
  );
}
