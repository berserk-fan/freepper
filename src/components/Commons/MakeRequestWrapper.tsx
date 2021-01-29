import {Box, Fade, LinearProgress} from "@material-ui/core";
import React from "react";

export default function MakeRequestWrapper({children, isProcessing}) {
    return (
        <Box>
            <Box height={"4px"} marginTop={"-4px"} marginX={"2px"}>
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
    )
}
