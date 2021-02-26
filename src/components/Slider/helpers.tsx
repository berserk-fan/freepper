import { Box, Typography } from "@material-ui/core";
import React, { memo } from "react";
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
