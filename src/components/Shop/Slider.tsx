import React, { ReactNode, useEffect, useState } from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import { Box, Theme, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles((theme: Theme) => ({
  dot: {
    border: "none",
    width: "10px",
    height: "10px",
    background: "#c5c5c5",
    borderRadius: "50%",
    margin: "0 5px",
    padding: "5px",
    cursor: "pointer",
    "&focus": {
      outline: "none",
    },
  },
  active: {
    background: theme.palette.secondary.main,
  },
  navigationContainer: {
    background: theme.palette.background.default,
    bottom: 0,
    paddingLeft: "10px",
    paddingRight: "10px",
    height: "24px",
    position: "absolute",
    justifyContent: "center",
    alignItems: "center",
    display: "flex",
    marginLeft: "auto",
    marginRight: "auto",
  },
  arrow: {
    width: "30px",
    height: "30px",
    position: "absolute",
    top: "50%",
    transform: "translateY(-50%)",
    WebkitTransform: "translateY(-50%)",
    fill: "#fff",
    cursor: "pointer",
  },
  arrow_left: { left: "5px" },
  arrow_right: { left: "auto", right: "5px" },
  arrow_disabled: { fill: "rgba(255, 255, 255, 0.5)" },
}));

const WINDOWSIZE = 2;
export default function Slider({
  slides,
  className = "",
  onChange,
}: {
  slides: [string, ReactNode][];
  className?: string;
  onChange?: (slideNum: number) => void;
}) {
  const classes = useStyles();
  const [currentSlide, setCurrentSlide] = React.useState(0);

  function changeSlide(slideIdx: number) {
    setCurrentSlide(slideIdx);
    onChange && onChange(slideIdx);
  }

  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const slideIdx = s.details().relativeSlide;
      changeSlide(slideIdx);
    },
  });

  const [toLoad, setToLoad] = useState([0, 2]);
  useEffect(() => {
    setToLoad([
      Math.max(currentSlide - WINDOWSIZE, 0),
      Math.min(currentSlide + WINDOWSIZE, slides.length),
    ]);
  }, [currentSlide]);

  const Dot = ({ idx, isActive }: { idx: number; isActive: boolean }) => (
    <button
      onClick={() => slider.moveToSlideRelative(idx)}
      className={`${classes.dot} ${isActive ? classes.active : ""}`}
    />
  );

  const Dots = () => (
    <Box className={classes.navigationContainer}>
      {[...Array(slider.details().size).keys()].map((idx) => (
        <Dot key={idx} idx={idx} isActive={currentSlide === idx} />
      ))}
    </Box>
  );

  const Numbers = () => (
    <Box className={classes.navigationContainer}>
      <Typography variant="caption" align="center">
        <Box fontFamily="Monospace">
          {currentSlide + 1}/{slider.details().size}
        </Box>
      </Typography>
    </Box>
  );

  function shouldLoad(idx: number) {
    return toLoad[0] <= idx && idx < toLoad[1];
  }

  return (
    <Box className={className} position="relative">
      <div ref={sliderRef as any} className="keen-slider">
        {slides.map(([key, slide], idx) => (
          <div key={key} className="keen-slider__slide">
            {shouldLoad(idx) ? slide : null}
          </div>
        ))}
      </div>
      {slider &&
        slides.length > 1 &&
        (slides.length <= 7 ? <Dots /> : <Numbers />)}
    </Box>
  );
}
