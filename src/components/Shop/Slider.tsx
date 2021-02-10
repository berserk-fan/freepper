import React, { useEffect, useState } from "react";
import { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import { Box, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";
import { Skeleton } from "@material-ui/lab";

const useStyles = makeStyles({
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
});

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
  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const slideIdx = s.details().relativeSlide;
      setCurrentSlide(slideIdx);
      if (onChange) {
        onChange(slideIdx);
      }
    },
  });

  const WINDOWSIZE = 2;
  const [toLoad, setToLoad] = useState([0, 2]);
  useEffect(() => {
    setToLoad([
      Math.max(currentSlide - WINDOWSIZE, 0),
      Math.min(currentSlide + WINDOWSIZE, slides.length),
    ]);
  }, [currentSlide]);

  const Dot = ({ key, isActive }: { key: number; isActive: boolean }) => (
    <button
      key={key}
      onClick={() => slider.moveToSlideRelative(key)}
      className={`${classes.dot} ${isActive ? classes.active : ""}`}
    />
  );

  const Dots = () => (
    <Box className={classes.navigationContainer}>
      {[...Array(slider.details().size).keys()].map((idx) => (
        <Dot key={idx} isActive={currentSlide === idx} />
      ))}
    </Box>
  );

  const Numbers = () => (
    <Box className={classes.navigationContainer}>
      <Typography variant={"caption"} align={"center"}>
        <Box fontFamily={"Monospace"}>
          {currentSlide + 1} / {slider.details().size}
        </Box>
      </Typography>
    </Box>
  );

  return (
    <Box className={className}>
      <Box position={"relative"}>
        <div ref={sliderRef as any} className="keen-slider">
          {slides.map((slide, idx) => (
            <div className="keen-slider__slide">
              {toLoad[0] <= idx && idx < toLoad[1] ? (
                slide
              ) : (
                <Skeleton
                  animation={"wave"}
                  variant={"rect"}
                  width={"100%"}
                  height={"100%"}
                />
              )}
            </div>
          ))}
        </div>
        {slider &&
          slides.length > 1 &&
          (slides.length <= 7 ? <Dots /> : <Numbers />)}
      </Box>
    </Box>
  );
}
