import Typography from "@mui/material/Typography";
import React, { useEffect, useState } from "react";

export default function CountDown({
  countDownId,
  periodSec,
}: {
  countDownId: number;
  periodSec: number;
}) {
  const [time, setTime] = useState(-1);

  function countDown(idAtStart: number, seconds: number) {
    setTimeout(() => {
      const newTime = seconds - 1;
      if (idAtStart === countDownId && newTime > 0) {
        setTime(newTime);
        countDown(idAtStart, newTime);
      }
    }, 1000);
  }

  useEffect(() => {
    setTime(periodSec);
    countDown(countDownId, periodSec);
  }, [countDownId]);

  return (
    <Typography component="span" display="inline">
      {time.toString()}
    </Typography>
  );
}
