import React, { useRef, useState } from "react";
import KeenSlider, { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import {Box, Theme} from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles((theme: Theme) => ({
  thumb: {
    border: "3px solid transparent",
    borderRadius: "3px",
  },
  thumbActive: {
    border: `3px solid ${theme.palette.secondary.main}`,
    borderRadius: "3px",
  },
  navigationContainer: {
    bottom: 0,
    padding: "10px",
  },
  thumbSlide: {
    display: "inline-flex",
    marginTop: theme.spacing(0.5),
    marginBottom: theme.spacing(1),
  }
}));

const nolock = (lock: { current: number }): boolean => lock.current == -1;
const lockEnded = (lock: { current: number }, idx: number): boolean =>
  lock.current == idx;
const unlock = (lock: { current: number }): void => {
  lock.current = -1;
};

export default function SliderThumbs({
  slides,
  thumbs,
  className = "",
}: {
  slides: React.ReactNode[];
  className?: string;
  thumbs: React.ReactNode[];
}) {
  const classes = useStyles();
  const [activeSlide, setActiveSlide] = useState(0);

  const lock = useRef(-1);

  const thumbserDirRef = useRef<KeenSlider>(null);

  function changeSlide(idx) {
    lock.current = idx;
    slider.moveToSlideRelative(idx);
    thumbser.moveToSlideRelative(idx);
  }

  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const idx = s.details().relativeSlide;
      const thumbser = thumbserDirRef.current;
      setActiveSlide(idx);

      if (nolock(lock)) {
        thumbser && thumbser.moveToSlideRelative(idx);
      }

      if (lockEnded(lock, idx)) {
        unlock(lock);
      }
    },
  });

  const slidesPerView = 7;
  const [thumbsRef, thumbser] = useKeenSlider({
    spacing: 4,
    initial: 0,
    slidesPerView: slidesPerView,
    centered: true,
    duration: 400,
    slideChanged(s) {
      const idx = s.details().relativeSlide;
    },
  });

  React.useEffect(() => {
    slider && slider.refresh();
    thumbser && thumbser.refresh();
    setActiveSlide(0);
  }, [slides, thumbs]);

  React.useEffect(() => {
    thumbserDirRef.current = thumbser;
  }, [thumbser]);

  return (
    <div className={className}>
      <div className={`${classes.navigationContainer}`}>
        <div ref={sliderRef as any} className="keen-slider">
          {slides.map((slide) => (
            <div className="keen-slider__slide">{slide}</div>
          ))}
        </div>
        <div
          ref={thumbsRef as any}
          className={`keen-slider ${classes.thumbSlide}`}
        >
          {thumbs.map((thumb, idx) => (
            <Box
              key={idx}
              onClick={() => changeSlide(idx)}
              className={`${classes.thumb} ${
                activeSlide === idx ? classes.thumbActive : ""
              } keen-slider__slide`}
            >
              {thumb}
            </Box>
          ))}
        </div>
      </div>
    </div>
  );
}
