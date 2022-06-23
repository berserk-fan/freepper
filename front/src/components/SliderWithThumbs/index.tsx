import React, { useEffect, useRef, useState } from "react";
import KeenSlider, { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import ArrowBackIosOutlined from "@mui/icons-material/ArrowBackIosOutlined";
import ArrowForwardIosOutlined from "@mui/icons-material/ArrowForwardIosOutlined";
import Image from "next/image";
import { Image as ImageData } from "apis/image.pb";
import Typography from "@mui/material/Typography";
import { SliderArrows } from "../Slider/helpers";
import { useStyles } from "./styles";
import { useSliderVirtualization } from "../Slider/utils";

const THUMBS_SLIDES_PER_VIEW = 5;

export default function SliderWithThumbs({
  images,
  thumbs,
  sizes,
  onChange = () => {},
  resetSlideIndex = false,
}: {
  images: ImageData[];
  thumbs: ImageData[];
  sizes: string;
  onChange?: (src: string) => void;
  resetSlideIndex?: boolean;
}) {
  if (images.length === 0 || thumbs.length === 0) {
    return <Typography>Empty images</Typography>;
  }

  const classes = useStyles();
  const thumbserDirRef = useRef<KeenSlider>(null);
  const [activeSlide, setActiveSlide] = useState(0);
  const [thumbSlide, setThumbSlide] = useState(0);

  useEffect(() => onChange(images[activeSlide]?.src), [activeSlide]);

  const lock = useRef(-1);

  const [sliderRef, slider] = useKeenSlider({
    initial: 0,
    spacing: 15,
    slideChanged(s) {
      const idx = s.details().relativeSlide;
      const thumbser = thumbserDirRef.current;
      setActiveSlide(idx);
      if (lock.current === -1) {
        thumbser?.moveToSlideRelative(idx);
      }

      if (lock.current === idx) {
        lock.current = -1;
      }
    },
  });

  function changeSlide(idx) {
    if (slider) {
      lock.current = idx;
      slider.moveToSlideRelative(idx);
    }
  }

  // thumbs slider

  const [thumbsRef, thumbser] = useKeenSlider({
    spacing: 4,
    initial: 0,
    slidesPerView: THUMBS_SLIDES_PER_VIEW,
    duration: 400,
    slideChanged(sliderInstance) {
      const idx = sliderInstance.details().relativeSlide;
      setThumbSlide(idx);
    },
  });

  // virtualization

  const { isRendering } = useSliderVirtualization({
    currentSlide: activeSlide,
    virtualizationRange: 2,
    totalLength: images.length,
  });

  useEffect(() => {
    slider?.refresh();
    thumbser?.refresh();
    if (resetSlideIndex) {
      changeSlide(0);
    }
  }, [images, thumbs]);

  useEffect(() => {
    thumbserDirRef.current = thumbser;
  }, [thumbser]);

  return (
    <>
      <Box position="relative">
        <div ref={sliderRef as any} className="keen-slider">
          {images.map((image, idx) => (
            <div key={image.src} className="keen-slider__slide">
              {isRendering(idx) && (
                <Box height="500px">
                  <Image
                    loading={idx === 1 ? "lazy" : "eager"}
                    src={image.src}
                    alt={image.alt}
                    layout="fill"
                    sizes={sizes}
                    objectFit="contain"
                  />
                </Box>
              )}
            </div>
          ))}
        </div>
        <SliderArrows
          showForwardButton={activeSlide !== thumbs.length - 1}
          showBackButton={activeSlide !== 0}
          forwardHandler={() => slider.next()}
          backHandler={() => slider.prev()}
        />
      </Box>
      <Box position="relative">
        <div
          ref={thumbsRef as any}
          className={`keen-slider ${classes.thumbSlide}`}
        >
          {thumbs.map((image, idx) => (
            <Box
              key={image.src}
              onClick={() => changeSlide(idx)}
              className={`${classes.thumb} ${
                activeSlide === idx ? classes.thumbActive : ""
              } keen-slider__slide`}
            >
              <Box width="90px" height="90px">
                <Image
                  layout="fill"
                  objectFit="cover"
                  sizes="90px"
                  src={image.src}
                  alt={image.alt}
                />
              </Box>
            </Box>
          ))}
          {thumbs.length < THUMBS_SLIDES_PER_VIEW && // КОСТЫЛИК ))
            new Array(THUMBS_SLIDES_PER_VIEW - thumbs.length)
              .fill(1)
              .map((el, index) => index)
              .map((element) => (
                <Box key={element} className="keen-slider__slide" />
              ))}
        </div>
        {thumbSlide + THUMBS_SLIDES_PER_VIEW < thumbs.length && (
          <IconButton
            onClick={() => thumbser.next()}
            className={`${classes.forwardButton} ${classes.smallButton}`}
            size="large"
          >
            <ArrowForwardIosOutlined className={classes.smallIcon} />
          </IconButton>
        )}
        {thumbSlide !== 0 && (
          <IconButton
            onClick={() => thumbser.prev()}
            className={`${classes.backButton} ${classes.smallButton}`}
            size="large"
          >
            <ArrowBackIosOutlined className={classes.smallIcon} />
          </IconButton>
        )}
      </Box>
    </>
  );
}
