import Box from "@material-ui/core/Box/Box";
import Fade from "@material-ui/core/Fade/Fade";
import LinearProgress from "@material-ui/core/LinearProgress/LinearProgress";
import React from "react";

export default function MakeRequestWrapper({ children, isProcessing }) {
  return (
    <Box>
      <Box height="4px" marginTop="-4px" marginX="2px">
        <Fade
          in={isProcessing}
          style={{
            transitionDelay: isProcessing ? "800ms" : "0ms",
          }}
          unmountOnExit
        >
          <LinearProgress
            style={{
              borderRadius: "2px",
            }}
            color="secondary"
          />
        </Fade>
      </Box>
      {children}
    </Box>
  );
}
