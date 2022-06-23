import React, { memo } from "react";
import IconButton from "@mui/material/IconButton";
import ArrowBackIosOutlined from "@mui/icons-material/ArrowBackIosOutlined";
import ArrowForwardIosOutlined from "@mui/icons-material/ArrowForwardIosOutlined";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { useStyles } from "./styles";

export const Dots = memo(
  ({
    size,
    currentSlide,
    onClick,
  }: {
    size: number;
    currentSlide: number;
    onClick: (val: number) => void;
  }) => {
    const classes = useStyles();
    return (
      <Box className={classes.navigationContainer}>
        {[...Array(size).keys()].map((idx) => (
          <button
            key={idx}
            aria-label="dot"
            type="button"
            onClick={() => onClick(idx)}
            className={`${classes.dot} ${
              currentSlide === idx ? classes.active : ""
            }`}
          />
        ))}
      </Box>
    );
  },
);

export const Numbers = memo(({ children }: { children: React.ReactNode }) => {
  const classes = useStyles();
  return (
    <Box className={classes.navigationContainer}>
      <Typography variant="caption" align="center">
        <Box fontFamily="Monospace">{children}</Box>
      </Typography>
    </Box>
  );
});

export type SliderArrowsProps = {
  showForwardButton: boolean;
  showBackButton: boolean;
  forwardHandler: () => void;
  backHandler: () => void;
};

export const SliderArrows = memo(
  ({
    showForwardButton,
    showBackButton,
    forwardHandler,
    backHandler,
  }: SliderArrowsProps) => {
    const classes = useStyles();
    return (
      <>
        {showForwardButton && (
          <IconButton
            onClick={forwardHandler}
            className={classes.forwardButton}
          >
            <ArrowForwardIosOutlined className={classes.icon} />
          </IconButton>
        )}
        {showBackButton && (
          <IconButton onClick={backHandler} className={classes.backButton}>
            <ArrowBackIosOutlined className={classes.icon} />
          </IconButton>
        )}
      </>
    );
  },
);
