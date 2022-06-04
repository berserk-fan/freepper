import React, { useEffect, useRef, useState } from "react";
import KeenSlider, { useKeenSlider } from "keen-slider/react";
import "keen-slider/keen-slider.min.css";
import Box from "@material-ui/core/Box";
import IconButton from "@material-ui/core/IconButton";
import ArrowBackIosOutlined from "@material-ui/icons/ArrowBackIosOutlined";
import ArrowForwardIosOutlined from "@material-ui/icons/ArrowForwardIosOutlined";
import Image from "next/image";
import { Image as ImageData } from "apis/image_list.pb";
import { SliderArrows } from "../Slider/helpers";
import { useStyles } from "./styles";
import { useSliderVirtualization } from "../Slider/utils";

const THUMBS_SLIDES_PER_VIEW = 5;

export default function SliderWithThumbs({
  images,
  thumbs,
  sizes,
}: {
  images: ImageData[];
  thumbs: ImageData[];
  sizes: string;
}) {
  const classes = useStyles();
  const thumbserDirRef = useRef<KeenSlider>(null);
  const [activeSlide, setActiveSlide] = useState(0);
  const [thumbSlide, setThumbSlide] = useState(0);

  // main slider

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
    lock.current = idx;
    slider.moveToSlideRelative(idx);
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
          >
            <ArrowForwardIosOutlined className={classes.smallIcon} />
          </IconButton>
        )}
        {thumbSlide !== 0 && (
          <IconButton
            onClick={() => thumbser.prev()}
            className={`${classes.backButton} ${classes.smallButton}`}
          >
            <ArrowBackIosOutlined className={classes.smallIcon} />
          </IconButton>
        )}
      </Box>
    </>
  );
}
