import React from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import styles from "./Slider.module.css";
import { Box } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";

const useStyles = makeStyles({
  thumb: {
    border: "2px solid transparent",
  },
  thumbActive: {
    border: `2px solid ${theme.palette.grey["800"]}`,
  },
});

export default function SliderThumbs({
  slides,
  thumbs,
  className = "",
}: {
  slides: any[];
  className?: string;
  thumbs: any[];
}) {
  const classes = useStyles();
  const [currentSlide, setCurrentSlide] = React.useState(0);
  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      setCurrentSlide(s.details().relativeSlide);
    },
  });

  function onThumbClick(idx) {
    return () => {
      slider.moveToSlideRelative(idx);
      thumbser.moveToSlideRelative(idx);
    };
  }

  const [thumbsRef, thumbser] = useKeenSlider({
    spacing: 15,
    initial: 0,
    slidesPerView: 5,
  });

  React.useEffect(() => {}, [slides, thumbs]);

  return (
    <div className={className}>
      <div className={`${styles["navigation-wrapper"]}`}>
        <div ref={sliderRef as any} className="keen-slider">
          {slides.map((slide) => (
            <div className="keen-slider__slide">{slide}</div>
          ))}
        </div>
        {slider && (
          <div
            ref={thumbsRef as any}
            className="keen-slider"
            style={{ display: "inline-flex", marginTop: theme.spacing(1), marginBottom: theme.spacing(1)}}
          >
            {thumbs.map((thumb, idx) => (
              <Box
                onClick={onThumbClick(idx)}
                className={`${classes.thumb} 
                            keen-slider__slide 
                            ${currentSlide === idx ? classes.thumbActive : ""}`}
              >
                {thumb}
              </Box>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
